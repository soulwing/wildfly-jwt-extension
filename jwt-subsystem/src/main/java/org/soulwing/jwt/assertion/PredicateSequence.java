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

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * A predicate that invokes a sequence of configured predicates.
 *
 * @author Carl Harris
 */
public class PredicateSequence implements Predicate<Object> {

  private final List<Predicate<Object>> predicates =
      new ArrayList<>();

  @SuppressWarnings({ "rawtypes", "unchecked" })
  public PredicateSequence(List<Predicate<?>> predicates) {
    for (Predicate predicate : predicates) {
      this.predicates.add(predicate);
    }
  }

  @Override
  public boolean test(Object value) {
    for (Predicate<Object> predicate : predicates) {
      if (!predicate.test(value)) return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return predicates.toString();
  }

}
