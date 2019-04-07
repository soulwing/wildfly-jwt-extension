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
import org.jboss.dmr.ModelNode;
import org.jboss.dmr.ModelType;

/**
 * A model definition for a resource that provides a configuration for JWT
 * bearer token validation.
 *
 * @author Carl Harris
 */
class ValidatorDefinition extends PersistentResourceDefinition {

  static final RuntimeCapability<Void> VALIDATOR_CAPABILITY =
      RuntimeCapability.Builder.of(Capabilities.CAPABILITY_VALIDATOR, true,
              ValidatorService.class)
          .setDynamicNameMapper(DynamicNameMappers.PARENT)
          .build();

  static final SimpleAttributeDefinition ISSUER =
      new SimpleAttributeDefinitionBuilder(Constants.ISSUER, ModelType.STRING)
          .setAllowExpression(true)
          .setRequired(true)
          .setRestartAllServices()
          .build();

  static final SimpleAttributeDefinition ISSUER_URL =
      new SimpleAttributeDefinitionBuilder(Constants.ISSUER_URL,
              ModelType.STRING)
          .setAllowExpression(true)
          .setRequired(true)
          .setRestartAllServices()
          .build();

  static final SimpleAttributeDefinition AUDIENCE =
      new SimpleAttributeDefinitionBuilder(Constants.AUDIENCE, ModelType.STRING)
          .setAllowExpression(true)
          .setRequired(false)
          .setRestartAllServices()
          .build();

  static final SimpleAttributeDefinition EXPIRATION_TOLERANCE =
      new SimpleAttributeDefinitionBuilder(Constants.EXPIRATION_TOLERANCE,
              ModelType.LONG)
          .setAllowExpression(true)
          .setRequired(false)
          .setDefaultValue(new ModelNode(0L))
          .setRestartAllServices()
          .build();

  static final SimpleAttributeDefinition SIGNATURE =
      new SimpleAttributeDefinitionBuilder(Constants.SIGNATURE,
              ModelType.STRING)
          .setAllowExpression(true)
          .setRequired(true)
          .setRestartAllServices()
          .build();

  static final SimpleAttributeDefinition ENCRYPTION =
      new SimpleAttributeDefinitionBuilder(Constants.ENCRYPTION,
              ModelType.STRING)
          .setAllowExpression(true)
          .setRequired(false)
          .setRestartAllServices()
          .build();

  static final StringListAttributeDefinition TRANSFORMS =
      new StringListAttributeDefinition.Builder(Constants.TRANSFORMS)
          .setAllowExpression(true)
          .setRequired(false)
          .setAttributeParser(AttributeParser.STRING_LIST)
          .setAttributeMarshaller(AttributeMarshaller.STRING_LIST)
          .setRestartAllServices()
          .build();

  static final StringListAttributeDefinition ASSERTIONS =
      new StringListAttributeDefinition.Builder(Constants.ASSERTIONS)
          .setAllowExpression(true)
          .setRequired(false)
          .setAttributeParser(AttributeParser.STRING_LIST)
          .setAttributeMarshaller(AttributeMarshaller.STRING_LIST)
          .setRestartAllServices()
          .build();

  static final AttributeDefinition[] ATTRIBUTES = {
      ISSUER,
      ISSUER_URL,
      AUDIENCE,
      EXPIRATION_TOLERANCE,
      SIGNATURE,
      ENCRYPTION,
      TRANSFORMS,
      ASSERTIONS
  };

  static ValidatorDefinition INSTANCE = new ValidatorDefinition();

  private ValidatorDefinition() {
    super(new SimpleResourceDefinition.Parameters(
        Constants.VALIDATOR_PATH, JwtExtension.getResolver(Constants.VALIDATOR))
        .setAddHandler(ValidatorAdd.INSTANCE)
        .setRemoveHandler(ReloadRequiredRemoveStepHandler.INSTANCE));
  }

  @Override
  public Collection<AttributeDefinition> getAttributes() {
    return Arrays.asList(ATTRIBUTES);
  }

}
