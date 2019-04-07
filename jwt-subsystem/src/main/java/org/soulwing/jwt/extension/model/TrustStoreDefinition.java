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
 * A model definition for a resource that holds a configuration for a source
 * of CA certificates that will be used as trust anchors.
 *
 * @author Carl Harris
 */
class TrustStoreDefinition extends PersistentResourceDefinition {

  static final RuntimeCapability<Void> TRUST_STORE_CAPABILITY =
      RuntimeCapability.Builder.of(Capabilities.CAPABILITY_TRUST_STORE, true,
          TrustStoreService.class)
      .setDynamicNameMapper(DynamicNameMappers.PARENT)
      .build();

  static final SimpleAttributeDefinition PATH =
      new SimpleAttributeDefinitionBuilder(Constants.PATH, ModelType.STRING)
          .setAllowExpression(true)
          .setRequired(false)
          .setRestartAllServices()
          .build();

  static final SimpleAttributeDefinition RELATIVE_TO =
      new SimpleAttributeDefinitionBuilder(Constants.RELATIVE_TO, ModelType.STRING)
          .setAllowExpression(true)
          .setRequired(false)
          .setRestartAllServices()
          .build();

  static final SimpleAttributeDefinition PASSWORD_SECRET =
      new SimpleAttributeDefinitionBuilder(
              Constants.PASSWORD_SECRET, ModelType.STRING)
          .setAllowExpression(true)
          .setRequired(false)
          .setRestartAllServices()
          .build();

  static final AttributeDefinition[] ATTRIBUTES = {
      PATH,
      RELATIVE_TO,
      PASSWORD_SECRET,
      ServiceProviderAttributes.PROVIDER,
      ServiceProviderAttributes.MODULE,
      ServiceProviderAttributes.PROPERTIES
  };

  static final TrustStoreDefinition INSTANCE = new TrustStoreDefinition();

  private TrustStoreDefinition() {
    super(new SimpleResourceDefinition.Parameters(
        Constants.TRUST_STORE_PATH,
        JwtExtension.getResolver(Constants.TRUST_STORE))
        .setAddHandler(TrustStoreAdd.INSTANCE)
        .setRemoveHandler(ReloadRequiredRemoveStepHandler.INSTANCE));
  }

  @Override
  public Collection<AttributeDefinition> getAttributes() {
    return Arrays.asList(ATTRIBUTES);
  }

}
