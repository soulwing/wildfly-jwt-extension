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

import org.jboss.as.controller.AttributeDefinition;
import org.jboss.as.controller.PersistentResourceDefinition;
import org.jboss.as.controller.SimpleAttributeDefinition;
import org.jboss.as.controller.SimpleAttributeDefinitionBuilder;
import org.jboss.as.controller.SimpleMapAttributeDefinition;
import org.jboss.as.controller.capability.DynamicNameMappers;
import org.jboss.as.controller.capability.RuntimeCapability;
import org.jboss.dmr.ModelType;

/**
 * A definition for the predicate resource type.
 *
 * @author Carl Harris
 */
class PredicateDefinition extends PersistentResourceDefinition {

  static final RuntimeCapability<Void> PREDICATE_CAPABILITY =
      RuntimeCapability.Builder.of(Capabilities.CAPABILITY_PREDICATE, true,
              PredicateService.class)
          .setDynamicNameMapper(DynamicNameMappers.GRAND_PARENT)
          .build();

  static final SimpleAttributeDefinition CODE =
      new SimpleAttributeDefinitionBuilder(Names.CODE, ModelType.STRING)
          .setAllowExpression(true)
          .setRequired(false)
          .setRestartAllServices()
          .build();

  static final SimpleAttributeDefinition MODULE =
      new SimpleAttributeDefinitionBuilder(Names.MODULE, ModelType.STRING)
          .setAllowExpression(true)
          .setRequired(false)
          .setRestartAllServices()
          .build();

  static final SimpleMapAttributeDefinition OPTIONS =
      new SimpleMapAttributeDefinition.Builder(Names.OPTIONS, false)
          .setRequired(false)
          .setAllowExpression(false)
          .setRestartAllServices()
          .build();

  public static AttributeDefinition[] attributes() {
    return new AttributeDefinition[] {
        CODE, MODULE, OPTIONS
    };
  }

  public static final PredicateDefinition INSTANCE =
      new PredicateDefinition();

  private PredicateDefinition() {
    super(Paths.PREDICATE,
        ResourceUtil.getResolver(Names.PROFILE, Names.CLAIM_ASSERTION,
            Names.PREDICATE),
        PredicateAdd.INSTANCE,
        PredicateRemove.INSTANCE);
  }

  @Override
  public Collection<AttributeDefinition> getAttributes() {
    return Arrays.asList(attributes());
  }

}
