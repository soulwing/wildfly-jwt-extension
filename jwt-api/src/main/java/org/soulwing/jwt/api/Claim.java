/*
 * File created on Feb 13, 2019
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
package org.soulwing.jwt.api;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * An instance of this type represents the value associated with a
 * claim.
 * <p>
 * The value of a claim can be retrieved in many different
 * representations using the methods of this interface.
 *
 * @author Carl Harris
 */
public interface Claim {

  enum Type {
    NULL,
    STRING,
    BOOLEAN,
    NUMBER,
    ARRAY,
    OBJECT
  }

  /**
   * Gets the JSON type of this claim.
   * @return JSON type indicator
   */
  Type type();

  /**
   * Tests whether this claim has no value.
   * @return {@code true} if this claim has no value
   */
  boolean isNull();

  /**
   * Retrieves the value of this claim as a string.
   * <p>
   * If the value is of a simple type other than string, it will be
   * converted to a string.
   * @return string value or null if this claim has no value
   * @throws ClassCastException if the value is not of a simple type
   *    that is either a string or can be converted to a string
   */
  String asString();

  /**
   * Retrieves the value of this claim as a boolean.
   * @return boolean value or null if this claim has no value
   * @throws ClassCastException if the value cannot be coerced to
   *    a Boolean
   */
  Boolean asBoolean();

  /**
   * Retrieves the value of this claim as an integer.
   * @return integer value or null if this claim has no value
   * @throws ClassCastException if the value cannot be coerced to
   *    an Integer
   */
  Integer asInt();

  /**
   * Retrieves the value of this claim as a long.
   * @return long value or null if this claim has no value
   * @throws ClassCastException if the value cannot be coerced to
   *    a Long
   */
  Long asLong();

  /**
   * Retrieves the value of this claim as a double.
   * @return double value or null if this claim has no value
   * @throws ClassCastException if the value cannot be coerced to
   *    a Double
   */
  Double asDouble();

  /**
   * Retrieves the value of a claim as an Instant.
   * <p>
   * An implementation of this method assumes that the claim value is
   * of the NumericDate type described in the JWT specification
   * (i.e. a number of seconds since the epoch).
   * @return instant value or null if this claim has no value
   * @throws ClassCastException if the value cannot be coerced to
   *    a Number for conversion to an Instant
   */
  Instant asInstant();

  /**
   * Retrieves the value of a claim as a Date.
   * <p>
   * An implementation of this method assumes that the claim value is
   * of the NumericDate type described in the JWT specification
   * (i.e. a number of seconds since the epoch).
   * @return date value or null if this claim has no value
   * @throws ClassCastException if the value cannot be coerced to
   *    a Number for converstion to a Date.
   */
  Date asDate();

  /**
   * Retrieves the value of this claim as an array of a given type.
   * @param elementType element type as a class
   * @param <T> element type
   * @return array of values or null of this claim has no value
   * @throws ClassCastException if the value is not of array type
   *    or if the values cannot be coerced to the given type
   */
  <T> T[] asArray(Class<?> elementType);

  /**
   * Retrieves the value of this claim as a generic list.
   * @return list of values or null if this claim has no value
   * @throws ClassCastException if the value is not of array type
   */
  List<?> asList();

  /**
   * Retrieves the value of this claim as a list of a given type.
   * @param elementType element type as a class
   * @param <T> element type
   * @return list of values or null if this claim has no value
   * @throws ClassCastException if the value is not of array type
   *    or if the values cannot be coerced to the given type
   */
  <T> List<T> asList(Class<?> elementType);

  /**
   * Retrieves the value of this claim as a generic set.
   * @return set of values or null if this claim has no value
   * @throws ClassCastException if the value is not of array type
   */
  Set<?> asSet();

  /**
   * Retrieves the value of this claim as a set of a given type
   * @param elementType element type as a class
   * @param <T> element type
   * @return set of values or null if this claim has no value
   * @throws ClassCastException if the value is not of array type
   *    or if the values cannot be coerced to the given type
   */
  <T> Set<T> asSet(Class<?> elementType);

  /**
   * Retrieves the value of this claim as a generic map.
   * @return map of values or null if this claim has no value
   * @throws ClassCastException if the value is not of object type
   */
  Map<String, ?> asMap();

  /**
   * Retrieves the value of this claim as a generic map.
   * @return map of values or null if this claim has no value
   * @throws ClassCastException if the value is not of object type
   *    or if the values cannot be coerced to the given type
   */
  <T> Map<String, T> asMap(Class<?> elementType);

  /**
   * Retrieves the value of this claim as an arbitrary value type.
   * <p>
   * Value type must be a public type that has either a public
   * constructor with a single argument or a public static method
   * with a single argument and a return type that is assignable to
   * type T. The argument type must be one of String, Boolean,
   * Number, Collection, Map, or Object. If the argument type is
   * other than Object, the claim value will first be coerced to the
   * given type. If the argument type is Object, no prior coercion
   * will be performed.
   * @param valueType value type as a class
   * @param <T> value type
   * @return an instance of type T or null if the claim has no value
   * @throws ClassCastException if the value is cannot be coerced to
   *    the given type
   * @throws IllegalArgumentException if {@code valueType} is not
   *    a supportable type.
   */
  <T> T as(Class<?> valueType);

}
