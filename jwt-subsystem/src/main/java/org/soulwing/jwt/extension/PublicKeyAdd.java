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

import java.util.function.Supplier;

import org.jboss.as.controller.AbstractAddStepHandler;
import org.jboss.as.controller.CapabilityServiceBuilder;
import org.jboss.as.controller.OperationContext;
import org.jboss.as.controller.OperationFailedException;
import org.jboss.as.controller.services.path.PathManager;
import org.jboss.dmr.ModelNode;
import org.jboss.msc.service.ServiceController;
import org.soulwing.jwt.crypto.PublicKeyFormat;
import org.soulwing.jwt.crypto.PublicKeyType;

/**
 * An add operation handler for the public key resource type.
 *
 * @author Carl Harris
 */
class PublicKeyAdd extends AbstractAddStepHandler {

  static final PublicKeyAdd INSTANCE = new PublicKeyAdd();

  private PublicKeyAdd() {
    super(PublicKeyDefinition.attributes());
  }

  @Override
  protected void populateModel(ModelNode operation, ModelNode model)
      throws OperationFailedException {
    super.populateModel(operation, model);
    ModelNodeUtil.setDefaultFromOpAddr(operation, model.get(Names.KID));
  }

  @Override
  protected void performRuntime(OperationContext context, ModelNode operation,
      ModelNode model) throws OperationFailedException {
    super.performRuntime(context, operation, model);

    final String kid = PublicKeyDefinition.KID
        .resolveModelAttribute(context, model).asString();
    final PublicKeyType type = PublicKeyType.valueOf(
        PublicKeyDefinition.TYPE
            .resolveModelAttribute(context, model).asString());
    final PublicKeyFormat format = PublicKeyFormat.valueOf(
        PublicKeyDefinition.FORMAT
            .resolveModelAttribute(context, model).asString());
    final String path = PublicKeyDefinition.PATH
        .resolveModelAttribute(context, model).asString();
    final String relativeTo = PublicKeyDefinition.RELATIVE_TO
        .resolveModelAttribute(context, model).asStringOrNull();

    final PublicKeyService service =
        new PublicKeyService(kid, type, format, path, relativeTo);

    final CapabilityServiceBuilder<PublicKeyService> builder =
        context.getCapabilityServiceTarget()
            .addCapability(PublicKeyDefinition.PUBLIC_KEY_CAPABILITY, service);

    final Supplier<PathManager> pathManager = builder
        .requiresCapability(Capabilities.REF_PATH_MANAGER, PathManager.class);

    final Supplier<ProfileService> profileService = builder
        .requiresCapability(Capabilities.CAPABILITY_JWT_PROFILE,
            ProfileService.class,
            context.getCurrentAddress().getParent().getLastElement().getValue());

    service.setPathManager(pathManager);
    service.setProfileService(profileService);

    builder.setInitialMode(ServiceController.Mode.ACTIVE)
        .install();
  }

}
