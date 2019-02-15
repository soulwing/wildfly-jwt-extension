/*
 * File created on Feb 14, 2019
 *
 * Copyright (c) 2019 Carl Harris, Jr
 * and others as noted
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.soulwing.jwt.jaas;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.soulwing.jwt.api.Claim;

/**
 * A {@link Claim} that delegates to an Auth0
 * {@link com.auth0.jwt.interfaces.Claim}.
 *
 * @author Carl Harris
 */
public class DelegatingClaim implements Claim {

  static final List<Class<?>> PARAMETER_TYPES =
      Arrays.asList(String.class, Double.class, Long.class, Integer.class,
          BigInteger.class, BigDecimal.class, Boolean.class, Object.class);
  private final Lock lock = new ReentrantLock();
  private final com.auth0.jwt.interfaces.Claim delegate;

  private volatile Type type;

  public DelegatingClaim(com.auth0.jwt.interfaces.Claim delegate) {
    this.delegate = delegate;
  }

  @Override
  public Type type() {
    if (type == null) {
      lock.lock();
      try {
        if (type == null) {
          type = testType();
        }
      }
      finally {
        lock.unlock();
      }
    }
    return type;
  }

  private Type testType() {
    if (delegate.isNull()) return Type.NULL;
    return type(delegate.as(Object.class));
  }

  @Override
  public boolean isNull() {
    return delegate.isNull();
  }

  @Override
  public String asString() {
    final Type type = type();
    final Object value = delegate.as(Object.class);
    return asString(type, value);
  }

  @Override
  public Boolean asBoolean() {
    final Object value = delegate.as(Object.class);
    if (value == null) return null;
    if (value instanceof Boolean) return (Boolean) value;
    throw new ClassCastException("cannot convert type " + type() + " to Boolean");
  }

  @Override
  public Integer asInt() {
    return asNumber().map(Number::intValue)
        .orElse(null);
  }

  @Override
  public Long asLong() {
    return asNumber().map(Number::longValue)
        .orElse(null);
  }

  @Override
  public Double asDouble() {
    return asNumber().map(Number::doubleValue)
        .orElse(null);
  }

  @Override
  public Instant asInstant() {
    return asNumber()
        .map(n -> Instant.ofEpochSecond(n.longValue()))
        .orElse(null);
  }

  @Override
  public Date asDate() {
    return asNumber()
        .map(n -> new Date(Instant.ofEpochSecond(n.longValue()).toEpochMilli()))
        .orElse(null);
  }

  private Optional<Number> asNumber() {
    final Object value = delegate.as(Object.class);
    if (value == null) return Optional.empty();
    if (value instanceof Number) return Optional.of((Number) value);
    throw new ClassCastException("cannot convert type " + type() + " to a Number");
  }

  @Override
  public <T> T[] asArray(Class<?> elementType) {
    return null;
  }

  @Override
  public List<?> asList() {
    final Object value = delegate.as(Object.class);
    if (value == null) return null;
    if (value instanceof List) return (List) value;
    if (value instanceof Collection && !(value instanceof Map)) {
      return new ArrayList<>(((Collection<?>) value));
    }
    throw new ClassCastException("cannot convert type " + type() + " to a List");
  }

  @Override
  public <T> List<T> asList(Class<? extends T> elementType) {
    return Optional.ofNullable(asList())
        .<List<T>>map(objects -> objects.stream()
            .map(v -> convertOrCast(elementType, v))
            .collect(Collectors.toCollection(ArrayList::new)))
        .orElse(null);
  }

  @Override
  public Set<?> asSet() {
    final Object value = delegate.as(Object.class);
    if (value == null) return null;
    if (value instanceof Collection && !(value instanceof Map)) {
      return new LinkedHashSet<>(((Collection<?>) value));
    }
    throw new ClassCastException("cannot convert type " + type() + " to a List");
  }

  @Override
  public <T> Set<T> asSet(Class<? extends T> elementType) {
    return Optional.ofNullable(asSet())
        .<Set<T>>map(objects -> objects.stream()
          .map(v -> convertOrCast(elementType, v))
          .collect(Collectors.toCollection(LinkedHashSet::new)))
        .orElse(null);
  }

  @Override
  public Map<String, ?> asMap() {
    return delegate.asMap();
  }

  @Override
  @SuppressWarnings("unchecked")
  public <V> V as(Class<? extends V> valueType) {
    if (isNull()) return null;
    if (Object.class.equals(valueType)) return (V) delegate.as(Object.class);
    final Optional<Method> method = findFactoryMethod(valueType);
    if (method.isPresent()) {
      return invokeMethod(method.get());
    }
    return findConstructor(valueType)
        .map(this::invokeConstructor)
        .orElse(null);
  }

  @SuppressWarnings("unchecked")
  private <V> V invokeMethod(Method method) {
    final Class<?> sourceType = method.getParameterTypes()[0];
    try {
      final Object value = convertOrCast(sourceType, delegate.as(Object.class));
      return (V) method.invoke(null, value);
    }
    catch (InvocationTargetException | IllegalAccessException ex) {
      throw new RuntimeException(ex);
    }
  }

  private <V> V invokeConstructor(Constructor<V> constructor) {
    final Class<?> sourceType = constructor.getParameterTypes()[0];
    try {
      final Object value = convertOrCast(sourceType, delegate.as(Object.class));
      return constructor.newInstance(value);
    }
    catch (InstantiationException | InvocationTargetException
        | IllegalAccessException ex) {
      throw new RuntimeException(ex);
    }
  }

  @SuppressWarnings("unchecked")
  private <V> Optional<Constructor<V>> findConstructor(
      Class<? extends V> valueType) {
    return Arrays.stream(valueType.getConstructors())
        .map(c -> (Constructor<V>) c)
        .filter(c -> c.getParameterTypes().length == 1)
        .filter(c -> PARAMETER_TYPES.stream()
            .anyMatch(t -> c.getParameterTypes()[0].isAssignableFrom(t)))
        .findFirst();
  }

  private <V> Optional<Method> findFactoryMethod(Class<? extends V> valueType) {
    return Arrays.stream(valueType.getMethods())
        .filter(m -> (m.getModifiers() & Modifier.STATIC) != 0)
        .filter(m -> m.getParameterTypes().length == 1)
        .filter(m -> PARAMETER_TYPES.stream()
            .anyMatch(t -> m.getParameterTypes()[0].isAssignableFrom(t)))
        .filter(m -> valueType.isAssignableFrom(m.getReturnType()))
        .findFirst();
  }

  @Override
  public <T, V> V as(Function<T, V> converter, Class<? extends T> sourceType) {
    if (isNull()) return null;
    return converter.apply(convertOrCast(sourceType, delegate.as(sourceType)));
  }

  @SuppressWarnings("unchecked")
  private <T> T convertOrCast(Class<? extends T> elementType, Object value) {
    if (String.class.isAssignableFrom(elementType)) {
      return (T) asString(type(value), value);
    }
    return elementType.cast(value);
  }

  private Type type(Object value) {
    if (value == null) return Type.NULL;
    if (value instanceof String) return Type.STRING;
    if (value instanceof Boolean) return Type.BOOLEAN;
    if (value instanceof Number) return Type.NUMBER;
    if (value instanceof Map) return Type.OBJECT;
    if (value instanceof Collection) return Type.ARRAY;
    if (value.getClass().isArray()) return Type.ARRAY;
    throw new IllegalArgumentException("unsupported type");
  }

  private String asString(Type type, Object value) {
    switch (type) {
      case NULL:
        return null;
      case STRING:
        return (String) value;
      case NUMBER:
      case BOOLEAN:
        return value.toString();
      default:
        throw new ClassCastException("cannot convert type " + type + " to string");
    }
  }

}
