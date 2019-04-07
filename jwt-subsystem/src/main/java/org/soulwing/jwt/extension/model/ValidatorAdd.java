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

import java.net.URI;
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
 * An add step handler for {@link ValidatorDefinition}.
 *
 * @author Carl Harris
 */
class ValidatorAdd extends AbstractAddStepHandler {

  static final ValidatorAdd INSTANCE = new ValidatorAdd();

  private ValidatorAdd() {
    super(new Parameters().addAttribute(ValidatorDefinition.ATTRIBUTES));
  }

  @Override
  protected void performRuntime(OperationContext context, ModelNode operation,
      ModelNode model) throws OperationFailedException {

    final String name = PathAddress.pathAddress(
        operation.require(ModelDescriptionConstants.OP_ADDR))
        .getLastElement().getValue();

    final String signature = ValidatorDefinition.SIGNATURE
        .resolveModelAttribute(context, model).asString();

    final String encryption = ValidatorDefinition.ENCRYPTION
        .resolveModelAttribute(context, model).asStringOrNull();

    final List<String> transforms = ValidatorDefinition.TRANSFORMS
        .resolveModelAttribute(context, model).asListOrEmpty()
        .stream().map(ModelNode::asString).collect(Collectors.toList());

    final List<String> assertions = ValidatorDefinition.ASSERTIONS
        .resolveModelAttribute(context, model).asListOrEmpty()
        .stream().map(ModelNode::asString).collect(Collectors.toList());

    final ValidatorService service = ValidatorService.builder()
        .issuer(ValidatorDefinition.ISSUER
            .resolveModelAttribute(context, model).asString())
        .issuerUrl(URI.create(ValidatorDefinition.ISSUER_URL
            .resolveModelAttribute(context, model).asString()))
        .audience(ValidatorDefinition.AUDIENCE
            .resolveModelAttribute(context, model).asStringOrNull())
        .expirationTolerance(ValidatorDefinition.EXPIRATION_TOLERANCE
            .resolveModelAttribute(context, model).asLong())
        .build();

    final CapabilityServiceBuilder<ValidatorService> builder =
        context.getCapabilityServiceTarget()
        .addCapability(ValidatorDefinition.VALIDATOR_CAPABILITY
            .fromBaseCapability(name), service);

    service.setSignatureService(
        builder.requiresCapability(Capabilities.CAPABILITY_SIGNATURE,
        SignatureService.class, signature));

    if (encryption != null) {
      service.setEncryptionService(
          builder.requiresCapability(Capabilities.CAPABILITY_ENCRYPTION,
              EncryptionService.class, encryption));
    }

    service.setTransformServices(transforms.stream().map(transform ->
        builder.requiresCapability(Capabilities.CAPABILITY_CLAIM_TRANSFORM,
            ClaimTransformService.class, transform)).collect(Collectors.toList()));

    service.setAssertionServices(assertions.stream().map(assertion ->
        builder.requiresCapability(Capabilities.CAPABILITY_CLAIM_ASSERTION,
            ClaimAssertionService.class, assertion)).collect(Collectors.toList()));

    builder.setInitialMode(ServiceController.Mode.ACTIVE).install();

  }

}
