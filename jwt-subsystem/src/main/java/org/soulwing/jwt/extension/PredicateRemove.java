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

import org.jboss.as.controller.AbstractRemoveStepHandler;
import org.jboss.as.controller.OperationContext;
import org.jboss.as.controller.OperationFailedException;
import org.jboss.dmr.ModelNode;
import org.jboss.msc.service.ServiceName;

/**
 * A remove operation handler for the predicate resource type.
 *
 * @author Carl Harris
 */
class PredicateRemove extends AbstractRemoveStepHandler {

  static final PredicateRemove INSTANCE =
      new PredicateRemove();

  @Override
  protected void performRuntime(OperationContext context, ModelNode operation,
      ModelNode model) throws OperationFailedException {
    super.performRuntime(context, operation, model);
    final ServiceName serviceName =
        PredicateDefinition.PREDICATE_CAPABILITY.fromBaseCapability(
            context.getCurrentAddress()).getCapabilityServiceName();

    context.removeService(serviceName);
  }

  @Override
  protected void recoverServices(OperationContext context,
      ModelNode operation, ModelNode model) throws OperationFailedException {
    super.recoverServices(context, operation, model);
    PredicateAdd.INSTANCE.performRuntime(context, operation, model);
  }

}
