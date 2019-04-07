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
 * An {@link Transformer} that performs a
 * replacement using a regular expression pattern.
 *
 * @author Carl Harris
 */
public class ReplacePatternTransformer
    implements Transformer<String, String> {

  static final String PATTERN = "pattern";
  static final String REPLACEMENT = "replacement";
  static final String REPLACE_ALL = "replace-all";
  
  private String pattern;
  private String replacement;
  private boolean replaceAll;

  @Override
  public String getName() {
    return "ReplacePattern";
  }

  @Override
  public void initialize(Properties properties) {
    pattern = properties.getProperty(PATTERN);
    replacement = properties.getProperty(REPLACEMENT, "");
    replaceAll = Boolean.parseBoolean(properties.getProperty(REPLACE_ALL,
        Boolean.FALSE.toString()));
    
    if (pattern == null) {
      throw new IllegalArgumentException("pattern is required");
    }
  }

  @Override
  public String apply(String value) {
    if (replaceAll) {
      return value.replaceAll(pattern, replacement);
    }
    return value.replaceFirst(pattern, replacement);
  }

  @Override
  public String toString() {
    return String.format("%s(%s='%s', %s='%s', %s=%s)",
        getClass().getSimpleName().replaceFirst(
            Transformer.class.getSimpleName() + "$", ""),
            PATTERN, pattern,
            REPLACEMENT, replacement,
            REPLACE_ALL, replaceAll);
  }

}
