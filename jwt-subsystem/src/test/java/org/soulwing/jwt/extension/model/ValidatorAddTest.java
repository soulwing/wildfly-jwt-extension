/*
 * File created on Apr 4, 2019
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
import static org.hamcrest.Matchers.sameInstance;

import java.net.URI;
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
 * Unit tests for {@link ValidatorAdd}.
 *
 * @author Carl Harris
 */
public class ValidatorAddTest {

  private static final String NAME = "name";
  private static final String ISSUER = "issuer";
  private static final URI ISSUER_URL = URI.create("issuerUrl");
  private static final String AUDIENCE = "audience";
  private static final long TOLERANCE = -1L;
  private static final String SIGNATURE = "signature";
  private static final String ENCRYPTION = "encryption";
  private static final String TRANSFORM = "transform";
  private static final String ASSERTION = "assertion";

  @Rule
  public final JUnitRuleMockery context = new JUnitRuleMockery();

  @Mock
  private OperationContext operationContext;

  @Mock
  private CapabilityServiceTarget target;

  @Mock
  private CapabilityServiceBuilder<SecretService> builder;

  @Mock
  private Supplier<SignatureService> signatureService;

  @Mock
  private Supplier<EncryptionService> encryptionService;

  @Mock
  private Supplier<ClaimTransformService> transformService;

  @Mock
  private Supplier<ClaimAssertionService> assertionService;

  private ModelNode operation = new ModelNode();

  private ModelNode model = new ModelNode();

  private ValidatorService service;

  @Before
  public void setUp() throws Exception {
    operation.set(ModelDescriptionConstants.OP_ADDR,
        PathAddress.pathAddress(Constants.TRUST_STORE, NAME)
            .toModelNode());

    model.get(Constants.ISSUER).set(ISSUER);
    model.get(Constants.ISSUER_URL).set(ISSUER_URL.toString());
    model.get(Constants.AUDIENCE).set(AUDIENCE);
    model.get(Constants.EXPIRATION_TOLERANCE).set(TOLERANCE);
    model.get(Constants.SIGNATURE).set(SIGNATURE);
    model.get(Constants.ENCRYPTION).set(ENCRYPTION);
    model.get(Constants.TRANSFORMS).add(TRANSFORM);
    model.get(Constants.ASSERTIONS).add(ASSERTION);
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
            with(any(ValidatorService.class)));
        will(new CustomAction("capture service") {
          @Override
          public Object invoke(Invocation invocation) throws Throwable {
            service = (ValidatorService) invocation.getParameter(1);
            return builder;
          }
        });
        oneOf(builder).requiresCapability(Capabilities.CAPABILITY_SIGNATURE,
            SignatureService.class, SIGNATURE);
        will(returnValue(signatureService));
        oneOf(builder).requiresCapability(Capabilities.CAPABILITY_ENCRYPTION,
            EncryptionService.class, ENCRYPTION);
        will(returnValue(encryptionService));
        oneOf(builder).requiresCapability(Capabilities.CAPABILITY_CLAIM_TRANSFORM,
            ClaimTransformService.class, TRANSFORM);
        will(returnValue(transformService));
        oneOf(builder).requiresCapability(Capabilities.CAPABILITY_CLAIM_ASSERTION,
            ClaimAssertionService.class, ASSERTION);
        will(returnValue(assertionService));
        oneOf(builder).setInitialMode(ServiceController.Mode.ACTIVE);
        will(returnValue(builder));
        oneOf(builder).install();
      }
    });

    ValidatorAdd.INSTANCE.performRuntime(operationContext, operation, model);
    assertThat(service.getIssuer(), is(equalTo(ISSUER)));
    assertThat(service.getIssuerUrl(), is(equalTo(ISSUER_URL)));
    assertThat(service.getAudience(), is(equalTo(AUDIENCE)));
    assertThat(service.getExpirationTolerance(), is(equalTo(TOLERANCE)));
    assertThat(service.getSignatureService(), is(sameInstance(signatureService)));
    assertThat(service.getEncryptionService(), is(sameInstance(encryptionService)));
    assertThat(service.getTransformServices(),
        is(Collections.singletonList(transformService)));
    assertThat(service.getAssertionServices(),
        is(Collections.singletonList(assertionService)));
  }

}