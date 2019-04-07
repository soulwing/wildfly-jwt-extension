/*
 * File created on Mar 30, 2019
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
package org.soulwing.jwt.extension.api;

import java.time.Instant;
import java.time.temporal.TemporalUnit;
import java.util.List;
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

  /**
   * An enumeration of the supported value types for claims.
   */
  enum Type {
    NULL,
    STRING,
    BOOLEAN,
    NUMBER,
    ARRAY
  }

  /**
   * Gets the name of this claim.
   * @return claim name
   */
  String getName();

  /**
   * Gets the JSON type of this claim.
   * @return JSON type indicator
   */
  Type getType();

  /**
   * Retrieves the value of this claim as an object.
   * @return claim value
   */
  Object getValue();

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
   * @throws ClassCastException if the value is not of string type
   */
  String asString();

  /**
   * Retrieves the value of this claim as a boolean.
   * @return boolean value or null if this claim has no value
   * @throws ClassCastException if the value is not of boolean type
   */
  Boolean asBoolean();

  /**
   * Retrieves the value of this claim as an integer.
   * @return integer value or null if this claim has no value
   * @throws ClassCastException if the value cannot be coerced to an Integer
   */
  Integer asInt();

  /**
   * Retrieves the value of this claim as a long.
   * @return long value or null if this claim has no value
   * @throws ClassCastException if the value cannot be coerced to a Long
   */
  Long asLong();

  /**
   * Retrieves the value of this claim as a double.
   * @return double value or null if this claim has no value
   * @throws ClassCastException if the value cannot be coerced to a Double
   */
  Double asDouble();

  /**
   * Retrieves the value of a claim as an Instant.
   * <p>
   * This method assumes that the claim value is of the NumericDate type
   * described in the JWT specification (i.e. a number of seconds since the
   * epoch).
   * @return instant value or null if this claim has no value
   * @throws ClassCastException if the value is not a Number
   */
  Instant asInstant();

  /**
   * Retrieves the value of a claim as an Instant.
   * <p>
   * This method assumes that the claim value is a numeric value representing
   * an offset from the epoch, using the specified units.
   *
   * @param unit temporal unit for the underlying numeric claim value
   * @return instant value or null if this claim has no value
   * @throws ClassCastException if the value is not a Number
   */
  Instant asInstant(TemporalUnit unit);

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
   *    or if the values are not of the specified type
   */
  <T> List<T> asList(Class<? extends T> elementType);

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
   *    or if the values are not of the specified type
   */
  <T> Set<T> asSet(Class<? extends T> elementType);

  /**
   * Retrieves the value of this claim as an array of a given type.
   * @param elementType element type as a class
   * @param <T> element type
   * @return array of values or null of this claim has no value
   * @throws ClassCastException if the value is not of array type
   *    or if the values are not of the specified type
   */
  <T> T[] asArray(Class<? extends T> elementType);

}
