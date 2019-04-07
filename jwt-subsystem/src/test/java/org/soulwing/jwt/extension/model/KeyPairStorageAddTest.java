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

import org.jboss.as.controller.CapabilityServiceBuilder;
import org.jboss.as.controller.CapabilityServiceTarget;
import org.jboss.as.controller.OperationContext;
import org.jboss.as.controller.PathAddress;
import org.jboss.as.controller.capability.RuntimeCapability;
import org.jboss.as.controller.descriptions.ModelDescriptionConstants;
import org.jboss.dmr.ModelNode;
import org.jboss.msc.service.ServiceController;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * Unit tests for {@link KeyPairStorageAdd}.
 *
 * @author Carl Harris
 */
public class KeyPairStorageAddTest {

  private static final String NAME = "name";
  private static final String PROVIDER = "provider";
  private static final String MODULE = "module";
  private static final String PASSWORD_SECRET = "password-secret";
  private static final String PROPERTY_NAME = "property-name";
  private static final String PROPERTY_VALUE = "property-value";

  @Rule
  public final JUnitRuleMockery context = new JUnitRuleMockery();;

  @Mock
  private OperationContext operationContext;

  @Mock
  private CapabilityServiceTarget target;

  @Mock
  private CapabilityServiceBuilder<SecretService> builder;

  private ModelNode operation = new ModelNode();
  private ModelNode model = new ModelNode();

  @Before
  public void setUp() throws Exception {
    operation.set(ModelDescriptionConstants.OP_ADDR,
        PathAddress.pathAddress(Constants.SECRET, NAME).toModelNode());

    model.get(Constants.PROVIDER).set(PROVIDER);
    model.get(Constants.MODULE).set(MODULE);
    model.get(Constants.PASSWORD_SECRET).set(PASSWORD_SECRET);
    model.get(Constants.PROPERTIES).get(PROPERTY_NAME).set(PROPERTY_VALUE);
  }

  @Test
  public void testPerformBoottime() throws Exception {
    context.checking(new Expectations() {
      {
        allowing(operationContext).resolveExpressions(with(any(ModelNode.class)));
        will(OperationContextUtil.resolveExpressionsAction());
        oneOf(operationContext).getCapabilityServiceTarget();
        will(returnValue(target));
        oneOf(target).addCapability(with(any(RuntimeCapability.class)),
            with(any(KeyPairStorageService.class)));
        will(returnValue(builder));
        oneOf(builder).setInitialMode(ServiceController.Mode.ACTIVE);
        will(returnValue(builder));
        oneOf(builder).install();
      }
    });

    KeyPairStorageAdd.INSTANCE.performRuntime(operationContext, operation, model);
  }


}