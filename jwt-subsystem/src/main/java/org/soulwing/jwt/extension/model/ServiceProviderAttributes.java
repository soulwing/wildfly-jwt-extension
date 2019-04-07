/*
 * File created on Apr 5, 2019
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

import org.jboss.as.controller.SimpleAttributeDefinition;
import org.jboss.as.controller.SimpleAttributeDefinitionBuilder;
import org.jboss.as.controller.SimpleMapAttributeDefinition;
import org.jboss.dmr.ModelType;

/**
 * Attributes used to create service provider instances. Service providers
 * are loaded using the {@link ServiceLoader} mechanism.
 *
 * @author Carl Harris
 */
class ServiceProviderAttributes {

  static final SimpleAttributeDefinition PROVIDER =
      new SimpleAttributeDefinitionBuilder(Constants.PROVIDER, ModelType.STRING)
          .setAllowExpression(true)
          .setRequired(false)
          .setRestartAllServices()
          .build();

  static final SimpleAttributeDefinition MODULE =
      new SimpleAttributeDefinitionBuilder(Constants.MODULE, ModelType.STRING)
          .setAllowExpression(true)
          .setRequired(false)
          .setRestartAllServices()
          .build();

  static final SimpleMapAttributeDefinition PROPERTIES =
      new SimpleMapAttributeDefinition.Builder(Constants.PROPERTIES, false)
          .setRequired(false)
          .setAllowExpression(true)
          .setRestartAllServices()
          .build();

}
