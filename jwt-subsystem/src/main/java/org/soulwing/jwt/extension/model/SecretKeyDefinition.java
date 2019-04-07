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

import java.util.Arrays;
import java.util.Collection;

import org.jboss.as.controller.AttributeDefinition;
import org.jboss.as.controller.PersistentResourceDefinition;
import org.jboss.as.controller.ReloadRequiredRemoveStepHandler;
import org.jboss.as.controller.SimpleAttributeDefinition;
import org.jboss.as.controller.SimpleAttributeDefinitionBuilder;
import org.jboss.as.controller.SimpleResourceDefinition;
import org.jboss.as.controller.capability.DynamicNameMappers;
import org.jboss.as.controller.capability.RuntimeCapability;
import org.jboss.dmr.ModelType;

/**
 * A model for a resource that provides configuration for a secret key.
 *
 * @author Carl Harris
 */
class SecretKeyDefinition extends PersistentResourceDefinition {

  static final RuntimeCapability<Void> SECRET_KEY_CAPABILITY =
      RuntimeCapability.Builder.of(Capabilities.CAPABILITY_SECRET_KEY, true,
              SecretKeyService.class)
          .setDynamicNameMapper(DynamicNameMappers.PARENT)
          .build();

  static final SimpleAttributeDefinition ID =
      new SimpleAttributeDefinitionBuilder(Constants.ID, ModelType.STRING)
          .setAllowExpression(true)
          .setRequired(false)
          .setRestartAllServices()
          .build();

  static final SimpleAttributeDefinition TYPE =
      new SimpleAttributeDefinitionBuilder(Constants.TYPE, ModelType.STRING)
          .setAllowExpression(true)
          .setRequired(true)
          .setRestartAllServices()
          .build();

  static final SimpleAttributeDefinition LENGTH =
      new SimpleAttributeDefinitionBuilder(Constants.LENGTH, ModelType.INT)
          .setAllowExpression(true)
          .setRequired(true)
          .setAllowedValues(128, 192, 256, 384, 512)
          .setRestartAllServices()
          .build();

  static AttributeDefinition[] ATTRIBUTES = {
      ID,
      TYPE,
      LENGTH,
      ServiceProviderAttributes.PROVIDER,
      ServiceProviderAttributes.MODULE,
      ServiceProviderAttributes.PROPERTIES
  };

  static final SecretKeyDefinition INSTANCE = new SecretKeyDefinition();

  private SecretKeyDefinition() {
    super(new SimpleResourceDefinition.Parameters(
        Constants.SECRET_KEY_PATH, JwtExtension.getResolver(Constants.SECRET_KEY))
        .setAddHandler(SecretKeyAdd.INSTANCE)
        .setRemoveHandler(ReloadRequiredRemoveStepHandler.INSTANCE));
  }

  @Override
  public Collection<AttributeDefinition> getAttributes() {
    return Arrays.asList(ATTRIBUTES);
  }

}
