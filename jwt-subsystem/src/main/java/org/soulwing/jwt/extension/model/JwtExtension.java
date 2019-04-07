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

import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.SUBSYSTEM;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jboss.as.controller.Extension;
import org.jboss.as.controller.ExtensionContext;
import org.jboss.as.controller.ModelVersion;
import org.jboss.as.controller.PathElement;
import org.jboss.as.controller.SubsystemRegistration;
import org.jboss.as.controller.descriptions.StandardResourceDescriptionResolver;
import org.jboss.as.controller.operations.common.GenericSubsystemDescribeHandler;
import org.jboss.as.controller.parsing.ExtensionParsingContext;
import org.jboss.as.controller.registry.ManagementResourceRegistration;

/**
 * A Wildfly extension that supports JWT authentication.
 *
 * @author Carl Harris
 */
public class JwtExtension implements Extension {

  public static final String SUBSYSTEM_NAME = "jwt";

   static final PathElement SUBSYSTEM_PATH =
      PathElement.pathElement(SUBSYSTEM, SUBSYSTEM_NAME);

  static final ModelVersion CURRENT_MODEL_VERSION =
          ModelVersion.create(1, 0, 0);

  private static final String RESOURCE_NAME =
      JwtExtension.class.getPackage().getName() + ".LocalDescriptions";

  public static StandardResourceDescriptionResolver getResolver(
      final String... keyPrefix) {
    final String prefix = Stream.concat(Stream.of(SUBSYSTEM_NAME),
        Arrays.stream(keyPrefix)).collect(Collectors.joining("."));

    return new StandardResourceDescriptionResolver(prefix,
        RESOURCE_NAME, JwtExtension.class.getClassLoader(), true, false);
  }

  @Override
  public void initializeParsers(ExtensionParsingContext context) {
    context.setSubsystemXmlMapping(SUBSYSTEM_NAME,
        Namespace.VERSION_1_0.getUri(), JwtSubsystemParser_1_0::new);
  }

  @Override
  public void initialize(ExtensionContext context) {

    final SubsystemRegistration subsystem = context.registerSubsystem(
        SUBSYSTEM_NAME, CURRENT_MODEL_VERSION);

    final ManagementResourceRegistration registration =
        subsystem.registerSubsystemModel(JwtSubsystemDefinition.INSTANCE);

    registration.registerOperationHandler(
        GenericSubsystemDescribeHandler.DEFINITION,
        GenericSubsystemDescribeHandler.INSTANCE, false);

    subsystem.registerXMLElementWriter(JwtSubsystemParser_1_0::new);
  }

}
