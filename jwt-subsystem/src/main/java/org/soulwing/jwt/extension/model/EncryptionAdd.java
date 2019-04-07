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
import java.util.Optional;
import java.util.stream.Collectors;

import org.jboss.as.controller.AbstractAddStepHandler;
import org.jboss.as.controller.CapabilityServiceBuilder;
import org.jboss.as.controller.OperationContext;
import org.jboss.as.controller.OperationFailedException;
import org.jboss.as.controller.PathAddress;
import org.jboss.as.controller.descriptions.ModelDescriptionConstants;
import org.jboss.dmr.ModelNode;
import org.jboss.msc.service.ServiceController;
import org.soulwing.jwt.api.JWE;

/**
 * An add step handler for {@link EncryptionDefinition}.
 *
 * @author Carl Harris
 */
class EncryptionAdd extends AbstractAddStepHandler {

  static final EncryptionAdd INSTANCE = new EncryptionAdd();

  private EncryptionAdd() {
    super(new Parameters().addAttribute(EncryptionDefinition.ATTRIBUTES));
  }

  @Override
  protected void performRuntime(OperationContext context, ModelNode operation,
      ModelNode model) throws OperationFailedException {

    final String name = PathAddress.pathAddress(
        operation.require(ModelDescriptionConstants.OP_ADDR))
        .getLastElement().getValue();

    final String keyPairStorage = EncryptionDefinition.KEY_PAIR_STORAGE
        .resolveModelAttribute(context, model).asStringOrNull();

    final List<String> secretKeys = EncryptionDefinition.SECRET_KEYS
        .resolveModelAttribute(context, model).asListOrEmpty()
        .stream().map(ModelNode::asString).collect(Collectors.toList());

    final EncryptionService service = EncryptionService.builder()
        .keyManagementAlgorithm(JWE.KeyManagementAlgorithm.of(
            EncryptionDefinition.KEY_MANAGEMENT_ALGORITHM
                .resolveModelAttribute(context, model).asString()))
        .contentEncryptionAlgorithm(JWE.ContentEncryptionAlgorithm.of(
            EncryptionDefinition.CONTENT_ENCRYPTION_ALGORITHM
                .resolveModelAttribute(context, model).asString()))
        .compressionAlgorithm(Optional.ofNullable(
            EncryptionDefinition.COMPRESSION_ALGORITHM
                .resolveModelAttribute(context, model).asStringOrNull())
                .map(JWE.CompressionAlgorithm::of).orElse(null))
        .build();

    final CapabilityServiceBuilder<EncryptionService> builder =
        context.getCapabilityServiceTarget().addCapability(
            EncryptionDefinition.ENCRYPTION_CAPABILITY.fromBaseCapability(name),
            service);

    if (keyPairStorage != null) {
      service.setKeyPairStorageService(builder.requiresCapability(
          Capabilities.CAPABILITY_KEY_PAIR_STORAGE,
          KeyPairStorageService.class,
          keyPairStorage));
    }

    if (!secretKeys.isEmpty()) {
      service.setSecretKeyServices(secretKeys.stream().map(secretKey ->
          builder.requiresCapability(Capabilities.CAPABILITY_SECRET_KEY,
              SecretKeyService.class, secretKey)).collect(Collectors.toList()));
    }

    builder.setInitialMode(ServiceController.Mode.ACTIVE).install();

  }

}
