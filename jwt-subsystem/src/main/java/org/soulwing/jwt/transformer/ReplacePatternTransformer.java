/*
 * File created on Feb 19, 2019
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
package org.soulwing.jwt.transformer;

import java.util.Properties;
import javax.xml.transform.Transformer;

import org.soulwing.jwt.api.ClaimTransformer;

/**
 * An {@link ClaimTransformer} that performs a replacement using a
 * regular expression pattern.
 *
 * @author Carl Harris
 */
public class ReplacePatternTransformer
    extends ClaimTransformer<String, String> {

  public static final String PATTERN = "pattern";
  public static final String REPLACEMENT = "replacement";
  public static final String REPLACE_ALL = "replace-all";
  
  private String pattern;
  private String replacement;
  private boolean replaceAll;
  
  /**
   * {@inheritDoc}
   */
  @Override
  public void initialize(Properties properties) {
    super.initialize(properties);
    pattern = properties.getProperty(PATTERN);
    replacement = properties.getProperty(REPLACEMENT, "");
    replaceAll = Boolean.parseBoolean(properties.getProperty(REPLACE_ALL,
        Boolean.FALSE.toString()));
    
    if (pattern == null) {
      throw new IllegalArgumentException("pattern is required");
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String transform(String value) {
    if (replaceAll) {
      return value.replaceAll(pattern, replacement);
    }
    return value.replaceFirst(pattern, replacement);
  }

  /**
   * {@inheritDoc}
   */
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
