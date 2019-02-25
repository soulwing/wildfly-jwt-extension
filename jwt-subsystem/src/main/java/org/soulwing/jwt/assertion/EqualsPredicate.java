/*
 * File created on Feb 21, 2019
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
package org.soulwing.jwt.assertion;

import java.util.Properties;
import java.util.function.Predicate;

import org.soulwing.jwt.api.Configurable;

/**
 * A predicate that tests simple object equality.
 *
 * @author Carl Harris
 */
public class EqualsPredicate implements Predicate<Object>, Configurable {

  private static final String VALUE = "value";

  private Object value;

  @Override
  public void initialize(Properties properties) {
    value = properties.getProperty(VALUE);
  }

  @Override
  public boolean test(Object o) {
    if (value == null) return false;
    return value.equals(o);
  }

}
