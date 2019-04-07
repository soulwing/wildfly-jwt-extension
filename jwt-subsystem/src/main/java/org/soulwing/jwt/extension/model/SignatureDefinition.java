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
 * A model definition for a resource that describes a configuration for JWT
 * signature verification.
 *
 * @author Carl Harris
 */
class SignatureDefinition extends PersistentResourceDefinition {

  static final RuntimeCapability<Void> SIGNATURE_CAPABILITY =
      RuntimeCapability.Builder.of(Capabilities.CAPABILITY_SIGNATURE, true,
              SignatureService.class)
          .setDynamicNameMapper(DynamicNameMappers.PARENT)
          .build();

  static final SimpleAttributeDefinition ALGORITHM =
      new SimpleAttributeDefinitionBuilder(Constants.ALGORITHM, ModelType.STRING)
          .setAllowExpression(true)
          .setRequired(true)
          .setValidator(SignatureAlgorithmValidator.INSTANCE)
          .setRestartAllServices()
          .build();

  static final SimpleAttributeDefinition TRUST_STORE =
      new SimpleAttributeDefinitionBuilder(
              Constants.TRUST_STORE, ModelType.STRING)
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

  static final SimpleAttributeDefinition CERT_SUBJECT_NAME =
      new SimpleAttributeDefinitionBuilder(Constants.CERT_SUBJECT_NAME,
          ModelType.STRING)
          .setAllowExpression(true)
          .setRequired(false)
          .setRestartAllServices()
          .build();

  static final SimpleAttributeDefinition CHECK_CERT_EXPIRATION =
      new SimpleAttributeDefinitionBuilder(
              Constants.CHECK_CERT_EXPIRATION, ModelType.BOOLEAN)
          .setAllowExpression(true)
          .setRequired(false)
          .setDefaultValue(new ModelNode(true))
          .setRestartAllServices()
          .build();

  static final SimpleAttributeDefinition CHECK_CERT_REVOCATION =
      new SimpleAttributeDefinitionBuilder(
              Constants.CHECK_CERT_REVOCATION, ModelType.BOOLEAN)
          .setAllowExpression(true)
          .setRequired(false)
          .setDefaultValue(new ModelNode(true))
          .setRestartAllServices()
          .build();

  static final SimpleAttributeDefinition CHECK_SUBJECT_CERT_ONLY =
      new SimpleAttributeDefinitionBuilder(
              Constants.CHECK_SUBJECT_CERT_ONLY, ModelType.BOOLEAN)
          .setAllowExpression(true)
          .setRequired(false)
          .setDefaultValue(new ModelNode(false))
          .setRestartAllServices()
          .build();

  static final AttributeDefinition[] ATTRIBUTES = {
      ALGORITHM,
      TRUST_STORE,
      SECRET_KEYS,
      CERT_SUBJECT_NAME,
      CHECK_CERT_EXPIRATION,
      CHECK_CERT_REVOCATION,
      CHECK_SUBJECT_CERT_ONLY
  };

  static final SignatureDefinition INSTANCE = new SignatureDefinition();

  private SignatureDefinition() {
    super(new SimpleResourceDefinition.Parameters(
        Constants.SIGNATURE_PATH, JwtExtension.getResolver(Constants.SIGNATURE))
        .setAddHandler(SignatureAdd.INSTANCE)
        .setRemoveHandler(ReloadRequiredRemoveStepHandler.INSTANCE));
  }

  @Override
  public Collection<AttributeDefinition> getAttributes() {
    return Arrays.asList(ATTRIBUTES);
  }

}
