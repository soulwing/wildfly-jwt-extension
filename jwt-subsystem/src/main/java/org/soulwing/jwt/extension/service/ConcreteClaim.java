/*
 * File created on Apr 3, 2019
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
package org.soulwing.jwt.extension.service;

import java.lang.reflect.Array;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.soulwing.jwt.extension.api.Claim;


/**
 * A concrete {@link Claim} implementation.
 *
 * @author Carl Harris
 */
public class ConcreteClaim implements Claim {

  private static final List<Class<?>> NUMBER_TYPES =
      Arrays.asList(Integer.class, Long.class, Double.class);

  private static final List<Class<?>> SCALAR_TYPES = new ArrayList<>();

  static {
    SCALAR_TYPES.add(String.class);
    SCALAR_TYPES.add(Boolean.class);
    SCALAR_TYPES.addAll(NUMBER_TYPES);
  }

  private final String name;
  private final Type type;
  private final Object value;
  private final Function<Object, Object> transformer;

  public ConcreteClaim(String name, Object value,
      Function<Object, Object> transformer) {
    this.name = name;
    this.type = type(value);
    this.value = this.type != Type.ARRAY ? value : asList(value);
    this.transformer = transformer;
  }

  private static Type type(Object value) {
    if (value == null) return Type.NULL;
    if (value instanceof Collection) return Type.ARRAY;
    if (value.getClass().isArray()) return Type.ARRAY;
    if (value instanceof String) return Type.STRING;
    if (value instanceof Boolean) return Type.BOOLEAN;
    if (value instanceof Number
        && NUMBER_TYPES.stream().anyMatch(t -> t.equals(value.getClass()))) {
      return Type.NUMBER;
    }

    throw new IllegalArgumentException("unsupported value type");
  }

  private static List<?> asList(Object value) {
    final List<?> list = toList(value);
    final boolean elementsOk = list.stream()
        .allMatch(v -> v == null
            || SCALAR_TYPES.stream().anyMatch(t -> t.isInstance(v)));
    if (elementsOk) return list;
    throw new IllegalArgumentException(
        "list-like types must contain scalar values");
  }

  private static List<?> toList(Object value) {
    if (value instanceof Collection && !(value instanceof List)) {
      return new ArrayList<>((Collection<?>) value);
    }
    if (value instanceof Object[]) {
      return new ArrayList<>(Arrays.asList((Object[]) value));
    }
    assert value instanceof List;
    return (List<?>) value;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public Type getType() {
    return type;
  }

  @Override
  public Object getValue() {
    return Optional.ofNullable(value).map(transformer::apply).orElse(null);
  }

  @Override
  public boolean isNull() {
    return getValue() == null;
  }

  @Override
  public String asString() {
    return Optional.ofNullable(getValue())
        .map(v -> (String) getValue())
        .orElse(null);
  }

  @Override
  public Boolean asBoolean() {
    return Optional.ofNullable(getValue())
        .map(v -> (Boolean) getValue())
        .orElse(null);
  }

  @Override
  public Integer asInt() {
    return Optional.ofNullable(getValue()).map(v -> (Number) v)
        .map(Number::intValue)
        .orElse(null);
  }

  @Override
  public Long asLong() {
    return Optional.ofNullable(getValue()).map(v -> (Number) v)
        .map(Number::longValue)
        .orElse(null);
  }

  @Override
  public Double asDouble() {
    return Optional.ofNullable(getValue()).map(v -> (Number) v)
        .map(Number::doubleValue)
        .orElse(null);
  }

  @Override
  public Instant asInstant() {
    return asInstant(ChronoUnit.SECONDS);
  }

  @Override
  public Instant asInstant(TemporalUnit unit) {
    return Optional.ofNullable(getValue()).map(v -> (Number) v)
        .map(n -> Instant.EPOCH.plus(Duration.of(n.longValue(), unit)))
        .orElse(null);
  }

  @Override
  public List<?> asList() {
    return Optional.ofNullable(getValue())
        .map(v -> v instanceof List ?
            new ArrayList<>((List<?>) v) : Collections.singletonList(v))
        .orElse(null);
  }

  @Override
  public <T> List<T> asList(Class<? extends T> elementType) {
    return Optional.ofNullable(asList())
        .<List<T>>map(objects -> objects.stream()
            .map(elementType::cast)
            .collect(Collectors.toCollection(ArrayList::new)))
        .orElse(null);
  }

  @Override
  public Set<?> asSet() {
    return Optional.ofNullable(asList())
        .map(LinkedHashSet::new)
        .orElse(null);
  }

  @Override
  public <T> Set<T> asSet(Class<? extends T> elementType) {
    return Optional.ofNullable(this.<T>asList(elementType))
        .map(LinkedHashSet::new)
        .orElse(null);
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T> T[] asArray(Class<? extends T> elementType) {
    return Optional.ofNullable(this.<T>asList(elementType))
        .map(l -> l.toArray((T[]) Array.newInstance(elementType, l.size())))
        .orElse(null);
  }

  @Override
  public String toString() {
    return Optional.ofNullable(getValue()).map(Object::toString).orElse(null);
  }

}
