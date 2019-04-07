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
import org.jboss.as.controller.SimpleMapAttributeDefinition;
import org.jboss.as.controller.SimpleResourceDefinition;
import org.jboss.as.controller.capability.DynamicNameMappers;
import org.jboss.as.controller.capability.RuntimeCapability;
import org.jboss.dmr.ModelType;

/**
 * A model definition for a resource that provides configuration for a key
 * pair storage instance.
 *
 * @author Carl Harris
 */
class KeyPairStorageDefinition extends PersistentResourceDefinition {

  static final RuntimeCapability<Void> KEY_PAIR_STORAGE_CAPABILITY =
      RuntimeCapability.Builder.of(Capabilities.CAPABILITY_KEY_PAIR_STORAGE,
          true, KeyPairStorageService.class)
      .setDynamicNameMapper(DynamicNameMappers.PARENT)
      .build();

  static final SimpleAttributeDefinition PROVIDER =
      new SimpleAttributeDefinitionBuilder(Constants.PROVIDER, ModelType.STRING)
          .setAllowExpression(true)
          .setRequired(true)
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

  static final AttributeDefinition[] ATTRIBUTES = {
      PROVIDER, MODULE, PROPERTIES
  };

  static final KeyPairStorageDefinition INSTANCE = new KeyPairStorageDefinition();

  private KeyPairStorageDefinition() {
    super(new SimpleResourceDefinition.Parameters(
        Constants.KEY_PAIR_STORAGE_PATH,
            JwtExtension.getResolver(Constants.KEY_PAIR_STORAGE))
        .setAddHandler(KeyPairStorageAdd.INSTANCE)
        .setRemoveHandler(ReloadRequiredRemoveStepHandler.INSTANCE));
  }

  @Override
  public Collection<AttributeDefinition> getAttributes() {
    return Arrays.asList(ATTRIBUTES);
  }

}
