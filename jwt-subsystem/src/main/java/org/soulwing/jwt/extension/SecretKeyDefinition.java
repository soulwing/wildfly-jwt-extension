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

import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;

import org.jboss.as.controller.AttributeDefinition;
import org.jboss.as.controller.PersistentResourceDefinition;
import org.jboss.as.controller.SimpleAttributeDefinition;
import org.jboss.as.controller.SimpleAttributeDefinitionBuilder;
import org.jboss.as.controller.capability.DynamicNameMappers;
import org.jboss.as.controller.capability.RuntimeCapability;
import org.jboss.as.controller.operations.validation.EnumValidator;
import org.jboss.dmr.ModelType;

/**
 * A definition for the {@code secret-key} resource type.
 *
 * @author Carl Harris
 */
class SecretKeyDefinition extends PersistentResourceDefinition {

  static final RuntimeCapability<Void> SECRET_KEY_CAPABILITY =
      RuntimeCapability.Builder.of(Capabilities.CAPABILITY_SECRET_KEY, true,
          SecretKeyService.class)
          .setDynamicNameMapper(DynamicNameMappers.PARENT)
          .build();

  static final SimpleAttributeDefinition KID =
      new SimpleAttributeDefinitionBuilder(Names.KID, ModelType.STRING)
          .setAllowExpression(false)
          .setRequired(false)
          .setRestartAllServices()
          .build();

  static final SimpleAttributeDefinition SECRET =
      new SimpleAttributeDefinitionBuilder(Names.SECRET, ModelType.STRING)
          .setAllowExpression(true)
          .setRequired(true)
          .setRestartAllServices()
          .build();

  static final SimpleAttributeDefinition ENCODING =
      new SimpleAttributeDefinitionBuilder(Names.ENCODING, ModelType.STRING)
          .setAllowExpression(true)
          .setRequired(true)
          .setValidator(new EnumValidator<>(SecretKeyEncoding.class,
              EnumSet.allOf(SecretKeyEncoding.class)))
          .setRestartAllServices()
          .build();

  public static AttributeDefinition[] attributes() {
    return new AttributeDefinition[] {
        KID, SECRET, ENCODING
    };
  }

  public static final SecretKeyDefinition INSTANCE =
      new SecretKeyDefinition();

  private SecretKeyDefinition() {
    super(Paths.SECRET_KEY,
        ResourceUtil.getResolver(Names.PROFILE, Names.SECRET_KEY),
        SecretKeyAdd.INSTANCE,
        SecretKeyRemove.INSTANCE);
  }

  @Override
  public Collection<AttributeDefinition> getAttributes() {
    return Arrays.asList(attributes());
  }

}
