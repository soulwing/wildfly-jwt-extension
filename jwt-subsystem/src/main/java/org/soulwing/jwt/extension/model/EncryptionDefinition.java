/*
 * File created on Apr 4, 2019
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
import org.jboss.as.controller.AttributeMarshaller;
import org.jboss.as.controller.AttributeParser;
import org.jboss.as.controller.PersistentResourceDefinition;
import org.jboss.as.controller.ReloadRequiredRemoveStepHandler;
import org.jboss.as.controller.SimpleAttributeDefinition;
import org.jboss.as.controller.SimpleAttributeDefinitionBuilder;
import org.jboss.as.controller.SimpleResourceDefinition;
import org.jboss.as.controller.StringListAttributeDefinition;
import org.jboss.as.controller.capability.DynamicNameMappers;
import org.jboss.as.controller.capability.RuntimeCapability;
import org.jboss.dmr.ModelType;

/**
 * A model definition for a resource that describes a configuration for JWT
 * payload decryption.
 *
 * @author Carl Harris
 */
class EncryptionDefinition extends PersistentResourceDefinition {

  static final RuntimeCapability<Void> ENCRYPTION_CAPABILITY =
      RuntimeCapability.Builder.of(Capabilities.CAPABILITY_ENCRYPTION, true,
              EncryptionService.class)
          .setDynamicNameMapper(DynamicNameMappers.PARENT)
          .build();

  static final SimpleAttributeDefinition KEY_MANAGEMENT_ALGORITHM =
      new SimpleAttributeDefinitionBuilder(
              Constants.KEY_MANAGEMENT_ALGORITHM, ModelType.STRING)
          .setAllowExpression(true)
          .setRequired(true)
          .setValidator(KeyManagementAlgorithmValidator.INSTANCE)
          .setRestartAllServices()
          .build();

  static final SimpleAttributeDefinition CONTENT_ENCRYPTION_ALGORITHM =
      new SimpleAttributeDefinitionBuilder(
          Constants.CONTENT_ENCRYPTION_ALGORITHM, ModelType.STRING)
          .setAllowExpression(true)
          .setRequired(true)
          .setValidator(ContentEncryptionAlgorithmValidator.INSTANCE)
          .setRestartAllServices()
          .build();

  static final SimpleAttributeDefinition COMPRESSION_ALGORITHM =
      new SimpleAttributeDefinitionBuilder(
          Constants.COMPRESSION_ALGORITHM, ModelType.STRING)
          .setAllowExpression(true)
          .setRequired(false)
          .setValidator(CompressionAlgorithmValidator.INSTANCE)
          .setRestartAllServices()
          .build();

  static final SimpleAttributeDefinition KEY_PAIR_STORAGE =
      new SimpleAttributeDefinitionBuilder(
              Constants.KEY_PAIR_STORAGE, ModelType.STRING)
          .setAllowExpression(true)
          .setRequired(false)
          .setRestartAllServices()
          .build();

  static final StringListAttributeDefinition SECRET_KEYS =
      new StringListAttributeDefinition.Builder(Constants.SECRET_KEYS)
          .setAllowExpression(true)
          .setRequired(false)
          .setAttributeParser(AttributeParser.STRING_LIST)
          .setAttributeMarshaller(AttributeMarshaller.STRING_LIST)
          .setRestartAllServices()
          .build();

  static final AttributeDefinition[] ATTRIBUTES = {
      KEY_MANAGEMENT_ALGORITHM,
      CONTENT_ENCRYPTION_ALGORITHM,
      COMPRESSION_ALGORITHM,
      KEY_PAIR_STORAGE,
      SECRET_KEYS
  };

  static final EncryptionDefinition INSTANCE = new EncryptionDefinition();

  private EncryptionDefinition() {
    super(new SimpleResourceDefinition.Parameters(
        Constants.ENCRYPTION_PATH,
            JwtExtension.getResolver(Constants.ENCRYPTION))
        .setAddHandler(EncryptionAdd.INSTANCE)
        .setRemoveHandler(ReloadRequiredRemoveStepHandler.INSTANCE));
  }

  @Override
  public Collection<AttributeDefinition> getAttributes() {
    return Arrays.asList(ATTRIBUTES);
  }

}
