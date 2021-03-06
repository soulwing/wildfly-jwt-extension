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

import java.util.Arrays;
import java.util.Collection;

import org.jboss.as.controller.AttributeDefinition;
import org.jboss.as.controller.PersistentResourceDefinition;
import org.jboss.as.controller.ReloadRequiredRemoveStepHandler;
import org.jboss.as.controller.SimpleResourceDefinition;
import org.jboss.as.controller.capability.DynamicNameMappers;
import org.jboss.as.controller.capability.RuntimeCapability;

/**
 * A model definition for a resource that holds a secret such as a password
 * or a symmetric key.
 *
 * @author Carl Harris
 */
class SecretDefinition extends PersistentResourceDefinition {

  static final RuntimeCapability<Void> SECRET_CAPABILITY =
      RuntimeCapability.Builder.of(Capabilities.CAPABILITY_SECRET, true,
          SecretService.class)
      .setDynamicNameMapper(DynamicNameMappers.PARENT)
      .build();

  static final AttributeDefinition[] ATTRIBUTES = {
      ServiceProviderAttributes.PROVIDER,
      ServiceProviderAttributes.MODULE,
      ServiceProviderAttributes.PROPERTIES
  };

  static final SecretDefinition INSTANCE = new SecretDefinition();

  private SecretDefinition() {
    super(new SimpleResourceDefinition.Parameters(
        Constants.SECRET_PATH, JwtExtension.getResolver(Constants.SECRET))
        .setAddHandler(SecretAdd.INSTANCE)
        .setRemoveHandler(ReloadRequiredRemoveStepHandler.INSTANCE));
  }

  @Override
  public Collection<AttributeDefinition> getAttributes() {
    return Arrays.asList(ATTRIBUTES);
  }

}
