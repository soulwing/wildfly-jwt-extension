/*
 * File created on Apr 7, 2019
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
package org.soulwing.jwt.extension.spi.local.transformer;

import java.util.Properties;

import org.soulwing.jwt.extension.spi.Transformer;


/**
 * An {@link Transformer} that flattens the
 * character case of a string.
 *
 * @author Carl Harris
 */
public class FlattenCaseTransformer
    implements Transformer<String, String> {

  static final String USE_UPPER_CASE = "use-upper-case";
  
  private boolean useUpperCase;

  @Override
  public String getName() {
    return "FlattenCase";
  }

  @Override
  public void initialize(Properties properties) {
    useUpperCase = Boolean.parseBoolean(properties.getProperty(USE_UPPER_CASE,
        Boolean.FALSE.toString()));
  }

  @Override
  public String apply(String value) {
    if (useUpperCase) {
      return value.toUpperCase();
    }
    return value.toLowerCase();
  }

  @Override
  public String toString() {
    return String.format("%s(%s=%s)",
        getClass().getSimpleName().replaceFirst(
            Transformer.class.getSimpleName() + "$", ""),
            USE_UPPER_CASE, useUpperCase);
  }


}
