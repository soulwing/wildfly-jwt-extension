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
package org.soulwing.jwt.extension.deployment;

import static org.soulwing.jwt.extension.deployment.DeploymentLogger.LOGGER;

import java.util.function.Supplier;

import org.jboss.as.controller.OperationContext;
import org.jboss.as.server.AbstractDeploymentChainStep;
import org.jboss.as.server.DeploymentProcessorTarget;
import org.jboss.as.server.deployment.DeploymentPhaseContext;
import org.jboss.as.server.deployment.DeploymentUnit;
import org.jboss.as.server.deployment.DeploymentUnitProcessor;
import org.jboss.as.server.deployment.Phase;
import org.jboss.msc.service.ServiceBuilder;
import org.jboss.msc.service.ServiceName;
import org.soulwing.jwt.extension.model.Capabilities;
import org.soulwing.jwt.extension.service.AuthenticationService;
import org.soulwing.jwt.extension.undertow.JwtServletExtension;
import org.wildfly.extension.undertow.deployment.UndertowAttachments;

/**
 * A {@link DeploymentUnitProcessor} that adds the CAS servlet extension to
 * a web application deployment.
 */
public class ServletExtensionDeploymentProcessor
    implements DeploymentUnitProcessor {

  private static final String EXTENSION_NAME = "jwt-extension";

  private static final Phase PHASE = Phase.FIRST_MODULE_USE;
  private static final int PRIORITY = 0x8000;

  private static final ServletExtensionDeploymentProcessor INSTANCE =
      new ServletExtensionDeploymentProcessor();

  private ServletExtensionDeploymentProcessor() {
  }
  
  /**
   * Adds a step handler for a deployment chain step that adds an instance
   * of this processor to the deployment processor target.
   * @param context context to which the step handler will be added
   */
  public static void addStepHandler(OperationContext context) {
    context.addStep(new AbstractDeploymentChainStep() {
      public void execute(DeploymentProcessorTarget processorTarget) {
        processorTarget.addDeploymentProcessor(
            Constants.SUBSYSTEM_NAME, PHASE, PRIORITY, INSTANCE);
      }
    }, OperationContext.Stage.RUNTIME);
  }

  @Override
  public void deploy(DeploymentPhaseContext phaseContext) {

    final DeploymentUnit deploymentUnit = phaseContext.getDeploymentUnit();
    final AppConfiguration config = deploymentUnit.getAttachment(
        DeploymentAttachments.JWT_DESCRIPTOR);
    if (config == null) return;

    final ServiceName authServiceName = ServiceName.of(
        ServiceName.parse(Capabilities.CAPABILITY_VALIDATOR), config.getValidatorId());

    final JwtServletExtension extension = new JwtServletExtension();
    installServletExtension(phaseContext, extension, authServiceName);
            
    deploymentUnit.addToAttachmentList(        
        UndertowAttachments.UNDERTOW_SERVLET_EXTENSIONS, extension);
    
    LOGGER.info("attached JWT servlet extension to deployment "
            + deploymentUnit.getName()
            + "; validator=" + config.getValidatorId());
  }

  @Override
  public void undeploy(DeploymentUnit context) {
  }

  private void installServletExtension(
      DeploymentPhaseContext phaseContext, JwtServletExtension extension,
      ServiceName authServiceName) {

    final ServiceName extensionServiceName = ServiceName.of(
        phaseContext.getPhaseServiceName().getParent(), EXTENSION_NAME);

    final ServiceBuilder<?> builder =
        phaseContext.getServiceTarget()
        .addService(extensionServiceName);

    final Supplier<AuthenticationService> authenticationService =
        builder.requires(authServiceName);

    extension.setAuthenticationService(authenticationService);

    builder.setInstance(extension).install();
  }
  
}
