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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

import java.util.function.Supplier;

import org.jboss.as.controller.ExtensionContext;
import org.jboss.as.controller.SubsystemRegistration;
import org.jboss.as.controller.operations.common.GenericSubsystemDescribeHandler;
import org.jboss.as.controller.parsing.ExtensionParsingContext;
import org.jboss.as.controller.registry.ManagementResourceRegistration;
import org.jmock.Expectations;
import org.jmock.api.Action;
import org.jmock.api.Invocation;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.jmock.lib.action.CustomAction;
import org.junit.Rule;
import org.junit.Test;

/**
 * Unit tests for {@link JwtExtension}.
 *
 * @author Carl Harris
 */
public class JwtExtensionTest {

  @Rule
  public final JUnitRuleMockery context = new JUnitRuleMockery();

  @Mock
  private ExtensionParsingContext extensionParsingContext;

  @Mock
  private ExtensionContext extensionContext;

  @Mock
  private SubsystemRegistration subsystem;

  @Mock
  private ManagementResourceRegistration registration;

  private JwtExtension extension = new JwtExtension();

  @Test
  @SuppressWarnings("unchecked")
  public void testInitializeParsers() throws Exception {
    context.checking(new Expectations() {
      {
        oneOf(extensionParsingContext).setSubsystemXmlMapping(
            with(JwtExtension.SUBSYSTEM_NAME),
            with(Namespace.VERSION_1_0.getUri()),
            with(any(Supplier.class)));
        will(testParserSupplier(2));
      }
    });

    extension.initializeParsers(extensionParsingContext);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testInitialize() throws Exception {
    context.checking(new Expectations() {
      {
        oneOf(extensionContext).registerSubsystem(JwtExtension.SUBSYSTEM_NAME,
            JwtExtension.CURRENT_MODEL_VERSION);
        will(returnValue(subsystem));
        oneOf(subsystem).registerSubsystemModel(
            JwtSubsystemDefinition.INSTANCE);
        will(returnValue(registration));
        oneOf(registration).registerOperationHandler(
            GenericSubsystemDescribeHandler.DEFINITION,
            GenericSubsystemDescribeHandler.INSTANCE, false);
        oneOf(subsystem).registerXMLElementWriter(with(any(Supplier.class)));
        will(testParserSupplier(0));
      }
    });

    extension.initialize(extensionContext);
  }

  private Action testParserSupplier(int parameterIndex) {
    return new CustomAction("test supplier") {
      @Override
      public Object invoke(Invocation invocation) throws Throwable {
        final Supplier<?> supplier = (Supplier<?>)
            invocation.getParameter(parameterIndex);
        assertThat(supplier.get(), is(instanceOf(JwtSubsystemParser_1_0.class)));
        return null;
      }
    };
  }


}