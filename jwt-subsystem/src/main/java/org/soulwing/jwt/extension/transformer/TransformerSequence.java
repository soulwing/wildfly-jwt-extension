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
package org.soulwing.jwt.extension.transformer;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * A transformer that invokes a sequence of configured transformers.
 *
 * @author Carl Harris
 */
public class TransformerSequence implements Function<Object, Object> {

  private final List<Function<Object, Object>> transformers =
      new ArrayList<>();
  
  @SuppressWarnings({ "rawtypes", "unchecked" })
  public TransformerSequence(List<Function<?, ?>> transformers) {
    for (Function transformer : transformers) {
      this.transformers.add(transformer);
    }
  }
  
  @Override
  public Object apply(Object value) {
    Object t = value;
    for (final Function<Object, Object> transformer : transformers) {
      t = transformer.apply(t);
    }
    return t;
  }

  @Override
  public String toString() {
    return transformers.toString();
  }

}
