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

import org.jboss.as.controller.OperationContext;
import org.jboss.as.server.AbstractDeploymentChainStep;
import org.jboss.as.server.DeploymentProcessorTarget;
import org.jboss.as.server.deployment.Attachments;
import org.jboss.as.server.deployment.DeploymentPhaseContext;
import org.jboss.as.server.deployment.DeploymentUnit;
import org.jboss.as.server.deployment.DeploymentUnitProcessor;
import org.jboss.as.server.deployment.Phase;
import org.jboss.as.server.deployment.module.ModuleDependency;
import org.jboss.as.server.deployment.module.ModuleSpecification;
import org.jboss.modules.Module;

/**
 * A {@link DeploymentUnitProcessor} that adds JWT dependencies to the
 * deployment.
 */
public class DependenciesDeploymentProcessor implements DeploymentUnitProcessor {

  private static final String API_MODULE_NAME = "org.soulwing.jwt.api";
  private static final boolean NOT_OPTIONAL = false;
  private static final boolean EXPORT = true;
  private static final boolean DONT_IMPORT_SERVICES = false;
  private static final boolean NOT_USER_SPECIFIED = false;

  private static final Phase PHASE = Phase.DEPENDENCIES;

  private static final int PRIORITY = 0x8000;
  
  private static final DependenciesDeploymentProcessor INSTANCE =
      new DependenciesDeploymentProcessor();

  private DependenciesDeploymentProcessor() { }

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
    if (config == null || !config.isAddDependencies()) return;

    final ModuleDependency dependency = new ModuleDependency(
        Module.getBootModuleLoader(),
        API_MODULE_NAME,
        NOT_OPTIONAL,
        EXPORT,
        DONT_IMPORT_SERVICES,
        NOT_USER_SPECIFIED);

    final ModuleSpecification moduleSpec = phaseContext.getDeploymentUnit()
        .getAttachment(Attachments.MODULE_SPECIFICATION);
    moduleSpec.addSystemDependency(dependency);

    LOGGER.info("added JWT API module dependency to deployment "
        + deploymentUnit.getName());
  }
  
  @Override
  public void undeploy(DeploymentUnit context) {
  }

}
