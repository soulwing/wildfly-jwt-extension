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
package org.soulwing.jwt.extension.model;

import java.util.Properties;
import java.util.stream.Collectors;

import org.jboss.dmr.ModelNode;
import org.jboss.dmr.Property;

/**
 * Utility methods for working with {@link org.jboss.dmr.ModelNode} objects.
 *
 * @author Carl Harris
 */
public class ModelNodeUtil {

  static Properties toProperties(ModelNode model) {
    final Properties properties = new Properties();
    if (model.isDefined()) {
      properties.putAll(model.asPropertyList().stream().collect(
          Collectors.toMap(Property::getName, p -> p.getValue().asString())));
    }
    return properties;
  }

}
