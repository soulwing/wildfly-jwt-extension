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

import org.jboss.as.controller.AbstractAddStepHandler;
import org.jboss.as.controller.OperationContext;
import org.jboss.as.controller.OperationFailedException;
import org.jboss.as.controller.PathAddress;
import org.jboss.as.controller.descriptions.ModelDescriptionConstants;
import org.jboss.dmr.ModelNode;
import org.jboss.msc.service.ServiceController;

/**
 * An add step handler for {@link KeyPairStorageDefinition}.
 *
 * @author Carl Harris
 */
class KeyPairStorageAdd extends AbstractAddStepHandler {

  static final KeyPairStorageAdd INSTANCE = new KeyPairStorageAdd();

  private KeyPairStorageAdd() {
    super(new Parameters()
        .addAttribute(KeyPairStorageDefinition.ATTRIBUTES));
  }

  @Override
  protected void performRuntime(OperationContext context, ModelNode operation,
      ModelNode model) throws OperationFailedException {

    final String name = PathAddress.pathAddress(
        operation.require(ModelDescriptionConstants.OP_ADDR))
            .getLastElement().getValue();

    final KeyPairStorageService service = KeyPairStorageService.builder()
        .provider(KeyPairStorageDefinition.PROVIDER
            .resolveModelAttribute(context, model).asString())
        .module(KeyPairStorageDefinition.MODULE
            .resolveModelAttribute(context, model).asStringOrNull())
        .properties(ModelNodeUtil.toProperties(
            KeyPairStorageDefinition.PROPERTIES
            .resolveModelAttribute(context, model)))
        .build();

    context.getCapabilityServiceTarget().addCapability(
        KeyPairStorageDefinition.KEY_PAIR_STORAGE_CAPABILITY
            .fromBaseCapability(name), service)
        .setInitialMode(ServiceController.Mode.ACTIVE)
        .install();
  }

}
