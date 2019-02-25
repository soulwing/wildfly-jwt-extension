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
import org.jboss.dmr.ModelNode;
import org.jboss.dmr.ModelType;
import org.soulwing.jwt.crypto.PublicKeyFormat;
import org.soulwing.jwt.crypto.PublicKeyType;

/**
 * A definition for the {@code secret-key} resource type.
 *
 * @author Carl Harris
 */
class PublicKeyDefinition extends PersistentResourceDefinition {

  static final RuntimeCapability<Void> PUBLIC_KEY_CAPABILITY =
      RuntimeCapability.Builder.of(Capabilities.CAPABILITY_PUBLIC_KEY, true,
          PublicKeyService.class)
      .setDynamicNameMapper(DynamicNameMappers.PARENT)
      .build();

  static final SimpleAttributeDefinition KID =
      new SimpleAttributeDefinitionBuilder(Names.KID, ModelType.STRING)
          .setAllowExpression(false)
          .setRequired(false)
          .setRestartAllServices()
          .build();

  static final SimpleAttributeDefinition TYPE =
      new SimpleAttributeDefinitionBuilder(Names.TYPE, ModelType.STRING)
          .setAllowExpression(false)
          .setRequired(true)
          .setDefaultValue(new ModelNode().set(PublicKeyType.RSA.name()))
          .setValidator(new EnumValidator<>(PublicKeyType.class,
              EnumSet.allOf(PublicKeyType.class)))
          .setRestartAllServices()
          .build();

  static final SimpleAttributeDefinition FORMAT =
      new SimpleAttributeDefinitionBuilder(Names.FORMAT, ModelType.STRING)
          .setAllowExpression(false)
          .setRequired(true)
          .setDefaultValue(new ModelNode().set(PublicKeyFormat.PEM.name()))
          .setValidator(new EnumValidator<>(PublicKeyFormat.class,
              EnumSet.allOf(PublicKeyFormat.class)))
          .setRestartAllServices()
          .build();

  static final SimpleAttributeDefinition PATH =
      new SimpleAttributeDefinitionBuilder(Names.PATH, ModelType.STRING)
          .setAllowExpression(true)
          .setRequired(true)
          .setRestartAllServices()
          .build();

  static final SimpleAttributeDefinition RELATIVE_TO =
      new SimpleAttributeDefinitionBuilder(Names.RELATIVE_TO, ModelType.STRING)
          .setAllowExpression(true)
          .setRequired(false)
          .setRestartAllServices()
          .build();

  public static AttributeDefinition[] attributes() {
    return new AttributeDefinition[] {
        KID, TYPE, FORMAT, PATH, RELATIVE_TO
    };
  }

  public static final PublicKeyDefinition INSTANCE =
      new PublicKeyDefinition();

  private PublicKeyDefinition() {
    super(Paths.PUBLIC_KEY,
        ResourceUtil.getResolver(Names.PROFILE, Names.PUBLIC_KEY),
        PublicKeyAdd.INSTANCE,
        PublicKeyRemove.INSTANCE);
  }

  @Override
  public Collection<AttributeDefinition> getAttributes() {
    return Arrays.asList(attributes());
  }

}
