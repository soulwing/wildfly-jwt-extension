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
import org.soulwing.jwt.api.JWS;

/**
 * An add step handler for {@link SignatureDefinition}.
 *
 * @author Carl Harris
 */
class SignatureAdd extends AbstractAddStepHandler {

  static final SignatureAdd INSTANCE = new SignatureAdd();

  private SignatureAdd() {
    super(new Parameters().addAttribute(SignatureDefinition.ATTRIBUTES));
  }

  @Override
  protected void performRuntime(OperationContext context, ModelNode operation,
      ModelNode model) throws OperationFailedException {

    final String name = PathAddress.pathAddress(
        operation.require(ModelDescriptionConstants.OP_ADDR))
        .getLastElement().getValue();

    final String trustStore = SignatureDefinition.TRUST_STORE
        .resolveModelAttribute(context, model).asStringOrNull();

    final List<String> secretKeys = SignatureDefinition.SECRET_KEYS
        .resolveModelAttribute(context, model).asListOrEmpty()
        .stream().map(ModelNode::asString).collect(Collectors.toList());

    final SignatureService service = SignatureService.builder()
        .algorithm(JWS.Algorithm.of(SignatureDefinition.ALGORITHM
            .resolveModelAttribute(context, model).asString()))
        .certificateSubjectName(SignatureDefinition.CERT_SUBJECT_NAME
            .resolveModelAttribute(context, model).asStringOrNull())
        .checkCertificateExpiration(SignatureDefinition.CHECK_CERT_EXPIRATION
            .resolveModelAttribute(context, model).asBoolean())
        .checkCertificateRevocation(SignatureDefinition.CHECK_CERT_REVOCATION
            .resolveModelAttribute(context, model).asBoolean())
        .checkSubjectCertificateOnly(SignatureDefinition.CHECK_SUBJECT_CERT_ONLY
            .resolveModelAttribute(context, model).asBoolean())
        .build();

    final CapabilityServiceBuilder<SignatureService> builder =
        context.getCapabilityServiceTarget().addCapability(
            SignatureDefinition.SIGNATURE_CAPABILITY.fromBaseCapability(name),
            service);

    if (trustStore != null) {
      service.setTrustStoreService(builder
          .requiresCapability(Capabilities.CAPABILITY_TRUST_STORE,
              TrustStoreService.class, trustStore));
    }

    if (!secretKeys.isEmpty()) {
      service.setSecretKeyServices(secretKeys.stream().map(secretKey ->
          builder.requiresCapability(Capabilities.CAPABILITY_SECRET_KEY,
              SecretKeyService.class, secretKey)).collect(Collectors.toList()));
    }

    builder.setInitialMode(ServiceController.Mode.ACTIVE).install();

  }

}
