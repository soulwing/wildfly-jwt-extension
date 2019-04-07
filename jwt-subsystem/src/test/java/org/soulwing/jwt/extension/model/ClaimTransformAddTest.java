/*
 * File created on Apr 5, 2019
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
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.util.Collections;
import java.util.function.Supplier;

import org.jboss.as.controller.CapabilityServiceBuilder;
import org.jboss.as.controller.CapabilityServiceTarget;
import org.jboss.as.controller.OperationContext;
import org.jboss.as.controller.PathAddress;
import org.jboss.as.controller.capability.RuntimeCapability;
import org.jboss.as.controller.descriptions.ModelDescriptionConstants;
import org.jboss.dmr.ModelNode;
import org.jboss.msc.service.ServiceController;
import org.jmock.Expectations;
import org.jmock.api.Invocation;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.jmock.lib.action.CustomAction;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * Unit tests for {@link ClaimTransformAdd}.
 *
 * @author Carl Harris
 */
public class ClaimTransformAddTest {

  private static final String CLAIM = "claim";
  private static final String TRANSFORMER = "transformer";

  @Rule
  public final JUnitRuleMockery context = new JUnitRuleMockery();;

  @Mock
  private OperationContext operationContext;

  @Mock
  private CapabilityServiceTarget target;

  @Mock
  private CapabilityServiceBuilder<SecretService> builder;

  @Mock
  private Supplier<TransformerService> transformerService;

  private ModelNode operation = new ModelNode();
  private ModelNode model = new ModelNode();

  private ClaimTransformService service;

  @Before
  public void setUp() throws Exception {
    operation.set(ModelDescriptionConstants.OP_ADDR,
        PathAddress.pathAddress(Constants.CLAIM_TRANSFORM,
            "claimTransform").toModelNode());

    model.get(Constants.CLAIM).set(CLAIM);
    model.get(Constants.TRANSFORMERS).add(TRANSFORMER);
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
            with(any(ClaimTransformService.class)));
        will(new CustomAction("capture service") {
          @Override
          public Object invoke(Invocation invocation) throws Throwable {
            service = (ClaimTransformService) invocation.getParameter(1);
            return builder;
          }
        });
        oneOf(builder).requiresCapability(Capabilities.CAPABILITY_TRANSFORMER,
            TransformerService.class, TRANSFORMER);
        will(returnValue(transformerService));
        oneOf(builder).setInitialMode(ServiceController.Mode.ACTIVE);
        will(returnValue(builder));
        oneOf(builder).install();
      }
    });

    ClaimTransformAdd.INSTANCE.performRuntime(operationContext, operation, model);
    assertThat(service.getClaim(), is(equalTo(CLAIM)));
    assertThat(service.getTransformerServices(),
        is(equalTo(Collections.singletonList(transformerService))));
  }

}