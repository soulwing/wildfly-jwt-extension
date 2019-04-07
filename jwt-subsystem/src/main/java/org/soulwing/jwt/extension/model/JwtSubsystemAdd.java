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

import org.jboss.as.controller.AbstractBoottimeAddStepHandler;
import org.jboss.as.controller.OperationContext;
import org.jboss.as.controller.OperationFailedException;
import org.jboss.dmr.ModelNode;
import org.jboss.msc.service.ServiceController;
import org.soulwing.jwt.extension.deployment.DependenciesDeploymentProcessor;
import org.soulwing.jwt.extension.deployment.DescriptorDeploymentProcessor;
import org.soulwing.jwt.extension.deployment.ServletExtensionDeploymentProcessor;

/**
 * An add step handler for the JWT subsystem.
 *
 * @author Carl Harris
 */
class JwtSubsystemAdd extends AbstractBoottimeAddStepHandler {

  static final JwtSubsystemAdd INSTANCE = new JwtSubsystemAdd();

  private JwtSubsystemAdd() {
    super(JwtSubsystemDefinition.ATTRIBUTES);
  }

  @Override
  protected void performBoottime(OperationContext context,
      ModelNode operation, ModelNode model) throws OperationFailedException {

    final JwtService service = JwtService.builder()
        .statisticsEnabled(JwtSubsystemDefinition.STATISTICS_ENABLED
            .resolveModelAttribute(context, model).asBoolean())
        .build();

    context.getCapabilityServiceTarget().addCapability(
        JwtSubsystemDefinition.JWT_CAPABILITY, service)
        .setInitialMode(ServiceController.Mode.ACTIVE)
        .install();

    DescriptorDeploymentProcessor.addStepHandler(context);
    DependenciesDeploymentProcessor.addStepHandler(context);
    ServletExtensionDeploymentProcessor.addStepHandler(context);
  }

}
