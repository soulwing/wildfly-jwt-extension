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
import org.jboss.as.controller.capability.RuntimeCapability;
import org.jboss.as.controller.registry.ManagementResourceRegistration;
import org.jboss.dmr.ModelNode;
import org.jboss.dmr.ModelType;

/**
 * A root model definition for the JWT subsystem.
 *
 * @author Carl Harris
 */
class JwtSubsystemDefinition extends PersistentResourceDefinition {

  static final RuntimeCapability<Void> JWT_CAPABILITY =
      RuntimeCapability.Builder.of(Capabilities.CAPABILITY_JWT, false,
      JwtService.class).build();

  static final SimpleAttributeDefinition STATISTICS_ENABLED =
      new SimpleAttributeDefinitionBuilder(
          Constants.STATISTICS_ENABLED, ModelType.BOOLEAN, true)
          .setRestartAllServices()
          .setAllowExpression(true)
          .setDefaultValue(new ModelNode(false))
          .build();

  static final AttributeDefinition[] ATTRIBUTES = {
      STATISTICS_ENABLED
  };

  static final JwtSubsystemDefinition INSTANCE = new JwtSubsystemDefinition();

  private JwtSubsystemDefinition() {
    super(new SimpleResourceDefinition.Parameters(
            JwtExtension.SUBSYSTEM_PATH, JwtExtension.getResolver())
        .setAddHandler(JwtSubsystemAdd.INSTANCE)
        .setRemoveHandler(ReloadRequiredRemoveStepHandler.INSTANCE)
        .addCapabilities(JWT_CAPABILITY));

  }

  @Override
  public Collection<AttributeDefinition> getAttributes() {
    return Arrays.asList(ATTRIBUTES);
  }

  @Override
  public void registerChildren(ManagementResourceRegistration registration) {
    registration.registerSubModel(SecretDefinition.INSTANCE);
    registration.registerSubModel(SecretKeyDefinition.INSTANCE);
    registration.registerSubModel(TrustStoreDefinition.INSTANCE);
    registration.registerSubModel(KeyPairStorageDefinition.INSTANCE);
    registration.registerSubModel(TransformerDefinition.INSTANCE);
    registration.registerSubModel(ClaimTransformDefinition.INSTANCE);
    registration.registerSubModel(ClaimAssertionDefinition.INSTANCE);
    registration.registerSubModel(SignatureDefinition.INSTANCE);
    registration.registerSubModel(EncryptionDefinition.INSTANCE);
    registration.registerSubModel(ValidatorDefinition.INSTANCE);
  }


}
