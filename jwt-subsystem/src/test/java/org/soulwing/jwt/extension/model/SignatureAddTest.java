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
import org.soulwing.jwt.api.JWS;

/**
 * Unit tests for {@link SignatureAdd}.
 *
 * @author Carl Harris
 */
public class SignatureAddTest {

  private static final String NAME = "name";
  private static final JWS.Algorithm ALGORITHM = JWS.Algorithm.HS256;
  private static final String TRUST_STORE = "trustStore";
  private static final String SECRET_KEY = "secretKey";
  private static final String SUBJECT_NAME = "subjectName";

  @Rule
  public final JUnitRuleMockery context = new JUnitRuleMockery();

  @Mock
  private OperationContext operationContext;

  @Mock
  private CapabilityServiceTarget target;

  @Mock
  private CapabilityServiceBuilder<SecretService> builder;

  @Mock
  private Supplier<TrustStoreService> trustStoreService;

  @Mock
  private Supplier<SecretKeyService> secretKeyService;

  private ModelNode operation = new ModelNode();

  private ModelNode model = new ModelNode();

  private SignatureService service;

  @Before
  public void setUp() throws Exception {
    operation.set(ModelDescriptionConstants.OP_ADDR,
        PathAddress.pathAddress(Constants.TRUST_STORE, NAME)
            .toModelNode());

    model.get(Constants.ALGORITHM).set(ALGORITHM.toToken());
    model.get(Constants.TRUST_STORE).set(TRUST_STORE);
    model.get(Constants.SECRET_KEYS).add(SECRET_KEY);
    model.get(Constants.CERT_SUBJECT_NAME).set(SUBJECT_NAME);
    model.get(Constants.CHECK_CERT_EXPIRATION).set(true);
    model.get(Constants.CHECK_CERT_REVOCATION).set(true);
    model.get(Constants.CHECK_SUBJECT_CERT_ONLY).set(true);
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
            with(any(SignatureService.class)));
        will(new CustomAction("capture service") {
          @Override
          public Object invoke(Invocation invocation) throws Throwable {
            service = (SignatureService) invocation.getParameter(1);
            return builder;
          }
        });
        oneOf(builder).requiresCapability(Capabilities.CAPABILITY_TRUST_STORE,
            TrustStoreService.class, TRUST_STORE);
        will(returnValue(trustStoreService));
        oneOf(builder).requiresCapability(Capabilities.CAPABILITY_SECRET_KEY,
            SecretKeyService.class, SECRET_KEY);
        will(returnValue(secretKeyService));
        oneOf(builder).setInitialMode(ServiceController.Mode.ACTIVE);
        will(returnValue(builder));
        oneOf(builder).install();
      }
    });

    SignatureAdd.INSTANCE.performRuntime(operationContext, operation, model);
    assertThat(service.getAlgorithm(), is(equalTo(ALGORITHM)));
    assertThat(service.getCertificateSubjectName(), is(equalTo(SUBJECT_NAME)));
    assertThat(service.isCheckCertificateExpiration(), is(true));
    assertThat(service.isCheckCertificateRevocation(), is(true));
    assertThat(service.isCheckSubjectCertificateOnly(), is(true));
    assertThat(service.getTrustStoreService(), is(equalTo(trustStoreService)));
    assertThat(service.getSecretKeyServices(),
        is(equalTo(Collections.singletonList(secretKeyService))));
  }

}