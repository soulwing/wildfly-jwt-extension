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
import static org.hamcrest.Matchers.sameInstance;

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
import org.soulwing.jwt.api.JWE;

/**
 * Unit tests for {@link EncryptionAdd}.
 *
 * @author Carl Harris
 */
public class EncryptionAddTest {

  private static final JWE.KeyManagementAlgorithm KEY_MANAGEMENT_ALGORITHM = JWE.KeyManagementAlgorithm.A256KW;
  private static final JWE.ContentEncryptionAlgorithm CONTENT_ENCRYPTION_ALGORITHM = JWE.ContentEncryptionAlgorithm.A256CBC_HS512;
  private static final JWE.CompressionAlgorithm COMPRESSION_ALGORITHM = JWE.CompressionAlgorithm.DEFLATE;
  private static final String KEY_PAIR_STORAGE = "keyPairStorage";
  private static final String SECRET_KEY = "secretKey";

  @Rule
  public final JUnitRuleMockery context = new JUnitRuleMockery();

  @Mock
  private OperationContext operationContext;

  @Mock
  private CapabilityServiceTarget target;

  @Mock
  private CapabilityServiceBuilder<SecretService> builder;

  @Mock
  private Supplier<KeyPairStorageService> keyPairStorageService;

  @Mock
  private Supplier<SecretKeyService> secretKeyService;

  private ModelNode operation = new ModelNode();

  private ModelNode model = new ModelNode();

  private EncryptionService service;

  @Before
  public void setUp() throws Exception {
    operation.set(ModelDescriptionConstants.OP_ADDR,
        PathAddress.pathAddress(Constants.TRUST_STORE, "encryption-name")
            .toModelNode());

    model.get(Constants.KEY_MANAGEMENT_ALGORITHM)
        .set(KEY_MANAGEMENT_ALGORITHM.toToken());
    model.get(Constants.CONTENT_ENCRYPTION_ALGORITHM)
        .set(CONTENT_ENCRYPTION_ALGORITHM.toToken());
    model.get(Constants.COMPRESSION_ALGORITHM)
        .set(COMPRESSION_ALGORITHM.toToken());
    model.get(Constants.KEY_PAIR_STORAGE).set(KEY_PAIR_STORAGE);
    model.get(Constants.SECRET_KEYS).add(SECRET_KEY);
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
            with(any(EncryptionService.class)));
        will(new CustomAction("capture service") {
          @Override
          public Object invoke(Invocation invocation) throws Throwable {
            service = (EncryptionService) invocation.getParameter(1);
            return builder;
          }
        });
        oneOf(builder).requiresCapability(
            Capabilities.CAPABILITY_KEY_PAIR_STORAGE,
            KeyPairStorageService.class,
            KEY_PAIR_STORAGE);
        will(returnValue(keyPairStorageService));
        oneOf(builder).requiresCapability(
            Capabilities.CAPABILITY_SECRET_KEY,
            SecretKeyService.class,
            SECRET_KEY);
        will(returnValue(secretKeyService));
        oneOf(builder).setInitialMode(ServiceController.Mode.ACTIVE);
        will(returnValue(builder));
        oneOf(builder).install();
      }
    });

    EncryptionAdd.INSTANCE.performRuntime(operationContext, operation, model);
    assertThat(service.getKeyManagementAlgorithm(),
        is(equalTo(KEY_MANAGEMENT_ALGORITHM)));
    assertThat(service.getContentEncryptionAlgorithm(),
        is(equalTo(CONTENT_ENCRYPTION_ALGORITHM)));
    assertThat(service.getCompressionAlgorithm(),
        is(equalTo(COMPRESSION_ALGORITHM)));
    assertThat(service.getKeyPairStorageService(),
        is(sameInstance(keyPairStorageService)));
    assertThat(service.getSecretKeyServices(),
        is(equalTo(Collections.singletonList(secretKeyService))));
  }

}