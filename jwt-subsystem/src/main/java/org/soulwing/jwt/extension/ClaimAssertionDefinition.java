/*
 * File created on Feb 21, 2019
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
 * A definition for the {@code claim-assertion} resource type.
 *
 * @author Carl Harris
 */
public class ClaimAssertionDefinition extends PersistentResourceDefinition {

  static final RuntimeCapability<Void> CLAIM_ASSERTION_CAPABILITY =
      RuntimeCapability.Builder.of(Capabilities.CAPABILITY_CLAIM_ASSERTION, true,
              ClaimAssertionService.class)
          .setDynamicNameMapper(DynamicNameMappers.PARENT)
          .build();

  static final SimpleAttributeDefinition NAME =
      new SimpleAttributeDefinitionBuilder(Names.NAME, ModelType.STRING)
          .setAllowExpression(false)
          .setRequired(false)
          .setRestartAllServices()
          .build();

  static final SimpleAttributeDefinition MODE =
      new SimpleAttributeDefinitionBuilder(Names.MODE, ModelType.STRING)
          .setAllowExpression(false)
          .setRequired(false)
          .setDefaultValue(new ModelNode().set(ClaimAssertionMode.ALL.name()))
          .setValidator(new EnumValidator<>(ClaimAssertionMode.class,
              EnumSet.allOf(ClaimAssertionMode.class)))
          .setRestartAllServices()
          .build();


  public static AttributeDefinition[] attributes() {
    return new AttributeDefinition[] {
        NAME,
        MODE
    };
  }

  public static final ClaimAssertionDefinition INSTANCE =
      new ClaimAssertionDefinition();

  private ClaimAssertionDefinition() {
    super(Paths.CLAIM_ASSERTION,
        ResourceUtil.getResolver(Names.PROFILE, Names.CLAIM_ASSERTION),
        ClaimAssertionAdd.INSTANCE,
        ClaimAssertionRemove.INSTANCE);
  }

  @Override
  public Collection<AttributeDefinition> getAttributes() {
    return Arrays.asList(attributes());
  }

  @Override
  public void registerChildren(
      ManagementResourceRegistration resourceRegistration) {
    super.registerChildren(resourceRegistration);
    resourceRegistration.registerSubModel(PredicateDefinition.INSTANCE);
  }

}
