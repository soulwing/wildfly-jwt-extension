/*
 * File created on Feb 22, 2019
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

import java.util.Properties;
import java.util.function.Supplier;

import org.jboss.as.controller.AbstractAddStepHandler;
import org.jboss.as.controller.CapabilityServiceBuilder;
import org.jboss.as.controller.OperationContext;
import org.jboss.as.controller.OperationFailedException;
import org.jboss.dmr.ModelNode;
import org.jboss.msc.service.ServiceController;

/**
 * An add operation handler for the predicate resource type.
 *
 * @author Carl Harris
 */
class PredicateAdd extends AbstractAddStepHandler {

  static final PredicateAdd INSTANCE = new PredicateAdd();

  private PredicateAdd() {
    super(PredicateDefinition.attributes());
  }

  @Override
  protected void populateModel(ModelNode operation, ModelNode model)
      throws OperationFailedException {
    super.populateModel(operation, model);
    ModelNodeUtil.setDefaultFromOpAddr(operation, model.get(Names.CODE));
  }

  @Override
  protected void performRuntime(OperationContext context, ModelNode operation,
      ModelNode model) throws OperationFailedException {
    super.performRuntime(context, operation, model);

    final String code = PredicateDefinition.CODE
        .resolveModelAttribute(context, model).asStringOrNull();
    final String module = PredicateDefinition.MODULE
        .resolveModelAttribute(context, model).asStringOrNull();

    final Properties options = new Properties();
    final ModelNode optionModel = PredicateDefinition.OPTIONS
        .resolveModelAttribute(context, model);
    if (optionModel.isDefined()) {
      optionModel.asPropertyList()
          .forEach(p -> options.setProperty(p.getName(), p.getValue().asString()));
    }

    final PredicateService service = new PredicateService(code, module, options);

    final CapabilityServiceBuilder<PredicateService> builder =
        context.getCapabilityServiceTarget()
            .addCapability(PredicateDefinition.PREDICATE_CAPABILITY, service);

    final Supplier<ClaimAssertionService> claimAssertionService = builder
        .requiresCapability(Capabilities.CAPABILITY_CLAIM_ASSERTION,
            ClaimAssertionService.class,
            context.getCurrentAddress().getParent().getParent().getLastElement().getValue(),
            context.getCurrentAddress().getParent().getLastElement().getValue());

    service.setClaimAssertionService(claimAssertionService);

    builder.setInitialMode(ServiceController.Mode.ACTIVE)
        .install();
  }

}
