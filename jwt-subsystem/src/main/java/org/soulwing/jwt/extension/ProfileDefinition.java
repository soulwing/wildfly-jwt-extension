/*
 * File created on Feb 19, 2019
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
package org.soulwing.jwt.extension;

import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;

import org.jboss.as.controller.AttributeDefinition;
import org.jboss.as.controller.PersistentResourceDefinition;
import org.jboss.as.controller.SimpleAttributeDefinition;
import org.jboss.as.controller.SimpleAttributeDefinitionBuilder;
import org.jboss.as.controller.capability.DynamicNameMappers;
import org.jboss.as.controller.capability.RuntimeCapability;
import org.jboss.as.controller.operations.validation.EnumValidator;
import org.jboss.as.controller.registry.ManagementResourceRegistration;
import org.jboss.dmr.ModelNode;
import org.jboss.dmr.ModelType;

/**
 *
 * A definition for the configuration profile resource.
 *
 * @author Carl Harris
 */
class ProfileDefinition extends PersistentResourceDefinition {

  static final RuntimeCapability<Void> JWT_PROFILE_CAPABILITY =
      RuntimeCapability.Builder.of(Capabilities.CAPABILITY_JWT_PROFILE, true, ProfileService.class)
          .setDynamicNameMapper(DynamicNameMappers.SIMPLE)
          .build();

  static final SimpleAttributeDefinition ALGORITHM =
      new SimpleAttributeDefinitionBuilder(Names.ALGORITHM, ModelType.STRING)
          .setAllowExpression(true)
          .setRequired(true)
          .setDefaultValue(new ModelNode().set(AlgorithmName.HS256.toString()))
          .setValidator(new EnumValidator<>(AlgorithmName.class,
              EnumSet.allOf(AlgorithmName.class)))
          .setRestartAllServices()
          .build();

  static final SimpleAttributeDefinition CLOCK_SKEW_TOLERANCE =
      new SimpleAttributeDefinitionBuilder(Names.CLOCK_SKEW_TOLERANCE, ModelType.LONG)
          .setAllowExpression(true)
          .setRequired(false)
          .setDefaultValue(new ModelNode(30000L))
          .setRestartAllServices()
          .build();

  public static final ProfileDefinition INSTANCE =
      new ProfileDefinition();

  public static AttributeDefinition[] attributes() {
    return new AttributeDefinition[] {
      ALGORITHM,
      CLOCK_SKEW_TOLERANCE
    };
  }

  private ProfileDefinition() {
    super(Paths.PROFILE,
        ResourceUtil.getResolver(Names.PROFILE),
        ProfileAdd.INSTANCE,
        ProfileRemove.INSTANCE);
  }

  @Override
  public Collection<AttributeDefinition> getAttributes() {
    return Arrays.asList(attributes());
  }


  @Override
  public void registerChildren(
      ManagementResourceRegistration resourceRegistration) {
    super.registerChildren(resourceRegistration);
    resourceRegistration.registerSubModel(SecretKeyDefinition.INSTANCE);
    resourceRegistration.registerSubModel(PublicKeyDefinition.INSTANCE);
    resourceRegistration.registerSubModel(ClaimAssertionDefinition.INSTANCE);
    resourceRegistration.registerSubModel(ClaimTransformDefinition.INSTANCE);
  }

}
