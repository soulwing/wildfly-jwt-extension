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

import java.util.List;
import java.util.stream.Collectors;

import org.jboss.as.controller.AbstractAddStepHandler;
import org.jboss.as.controller.CapabilityServiceBuilder;
import org.jboss.as.controller.OperationContext;
import org.jboss.as.controller.OperationFailedException;
import org.jboss.as.controller.PathAddress;
import org.jboss.as.controller.descriptions.ModelDescriptionConstants;
import org.jboss.dmr.ModelNode;
import org.jboss.msc.service.ServiceController;

/**
 * An add step handler for {@link ClaimTransformDefinition}.
 *
 * @author Carl Harris
 */
class ClaimTransformAdd extends AbstractAddStepHandler {

  static final ClaimTransformAdd INSTANCE = new ClaimTransformAdd();

  private ClaimTransformAdd() {
    super(new Parameters().addAttribute(ClaimTransformDefinition.ATTRIBUTES));
  }

  @Override
  protected void performRuntime(OperationContext context, ModelNode operation,
      ModelNode model) throws OperationFailedException {

    final String name = PathAddress.pathAddress(
        operation.require(ModelDescriptionConstants.OP_ADDR))
        .getLastElement().getValue();

    final List<String> transformers = ClaimTransformDefinition.TRANSFORMERS
        .resolveModelAttribute(context, model).asListOrEmpty()
        .stream().map(ModelNode::asString).collect(Collectors.toList());

    final ClaimTransformService service = ClaimTransformService.builder()
        .claim(ClaimTransformDefinition.CLAIM
            .resolveModelAttribute(context, model).asString(name))
        .build();

    final CapabilityServiceBuilder<ClaimTransformService> builder =
        context.getCapabilityServiceTarget().addCapability(
            ClaimTransformDefinition.CLAIM_TRANSFORM_CAPABILITY
                .fromBaseCapability(name), service);

    service.setTransformerServices(transformers.stream().map(transformer ->
        builder.requiresCapability(Capabilities.CAPABILITY_TRANSFORMER,
        TransformerService.class, transformer)).collect(Collectors.toList()));

    builder.setInitialMode(ServiceController.Mode.ACTIVE).install();
  }

}
