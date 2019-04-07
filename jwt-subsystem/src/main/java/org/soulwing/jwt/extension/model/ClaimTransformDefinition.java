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
 * A model for a resource that transforms values for a particular claim.
 *
 * @author Carl Harris
 */
class ClaimTransformDefinition extends PersistentResourceDefinition {

  static final RuntimeCapability<Void> CLAIM_TRANSFORM_CAPABILITY =
      RuntimeCapability.Builder.of(Capabilities.CAPABILITY_CLAIM_TRANSFORM,
              true, ClaimTransformService.class)
          .setDynamicNameMapper(DynamicNameMappers.PARENT)
          .build();

  static final SimpleAttributeDefinition CLAIM =
      new SimpleAttributeDefinitionBuilder(Constants.CLAIM, ModelType.STRING)
          .setAllowExpression(true)
          .setRequired(false)
          .setRestartAllServices()
          .build();

  static final StringListAttributeDefinition TRANSFORMERS =
      new StringListAttributeDefinition.Builder(Constants.TRANSFORMERS)
      .setAllowExpression(true)
          .setRequired(false)
          .setAttributeParser(AttributeParser.STRING_LIST)
          .setAttributeMarshaller(AttributeMarshaller.STRING_LIST)
          .setRestartAllServices()
          .build();

  static AttributeDefinition[] ATTRIBUTES = {
      CLAIM,
      TRANSFORMERS
  };

  static final ClaimTransformDefinition INSTANCE =
      new ClaimTransformDefinition();

  private ClaimTransformDefinition() {
    super(new SimpleResourceDefinition.Parameters(
        Constants.CLAIM_TRANSFORM_PATH,
        JwtExtension.getResolver(Constants.CLAIM_TRANSFORM))
        .setAddHandler(ClaimTransformAdd.INSTANCE)
        .setRemoveHandler(ReloadRequiredRemoveStepHandler.INSTANCE));
  }

  @Override
  public Collection<AttributeDefinition> getAttributes() {
    return Arrays.asList(ATTRIBUTES);
  }

}
