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

import org.jboss.as.controller.AbstractAddStepHandler;
import org.jboss.as.controller.CapabilityServiceBuilder;
import org.jboss.as.controller.OperationContext;
import org.jboss.as.controller.OperationFailedException;
import org.jboss.dmr.ModelNode;
import org.jboss.msc.service.ServiceController;
import org.jboss.msc.service.ServiceName;

/**
 * An add operation handler for the profile resource type.
 *
 * @author Carl Harris
 */
class ProfileAdd extends AbstractAddStepHandler {

  static final ProfileAdd INSTANCE = new ProfileAdd();

  private ProfileAdd() {
    super(ProfileDefinition.attributes());
  }

  @Override
  protected void performRuntime(OperationContext context, ModelNode operation,
      ModelNode model) throws OperationFailedException {
    super.performRuntime(context, operation, model);

    final String algorithm = ProfileDefinition.ALGORITHM
        .resolveModelAttribute(context, model).asString();
    final long clockSkewTolerance = ProfileDefinition.CLOCK_SKEW_TOLERANCE
        .resolveModelAttribute(context, model).asLong();

    final ProfileService service =
        new ProfileService(context.getCurrentAddressValue(),
            algorithm, clockSkewTolerance);

    final CapabilityServiceBuilder<ProfileService> builder =
        context.getCapabilityServiceTarget()
            .addCapability(ProfileDefinition.JWT_PROFILE_CAPABILITY, service);

    builder.setInitialMode(ServiceController.Mode.ACTIVE)
        .addAliases(ServiceName.of(
            ServiceName.parse(Capabilities.CAPABILITY_JWT_PROFILE),
            context.getCurrentAddress().getLastElement().toString()))
        .install();
  }

}
