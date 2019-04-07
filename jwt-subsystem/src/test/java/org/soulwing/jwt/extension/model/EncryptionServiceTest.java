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

import java.util.Collections;

import org.jboss.msc.service.ServiceController;
import org.jboss.msc.service.ServiceName;
import org.jboss.msc.service.StartContext;
import org.jboss.msc.service.StopContext;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.jmock.lib.concurrent.Synchroniser;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Rule;
import org.junit.Test;
import org.soulwing.jwt.api.JWE;
import org.soulwing.jwt.extension.service.EncryptionConfiguration;
import org.soulwing.jwt.extension.service.SecretKeyConfiguration;
import org.soulwing.s2ks.KeyPairStorage;

/**
 * Unit tests for {@link EncryptionService}.
 *
 * @author Carl Harris
 */
public class EncryptionServiceTest {

  private static final JWE.KeyManagementAlgorithm KEY_MANAGEMENT_ALGORITHM =
      JWE.KeyManagementAlgorithm.A256KW;

  private static final JWE.ContentEncryptionAlgorithm CONTENT_ENCRYPTION_ALGORITHM =
      JWE.ContentEncryptionAlgorithm.A256CBC_HS512;

  private static final JWE.CompressionAlgorithm COMPRESSION_ALGORITHM =
      JWE.CompressionAlgorithm.DEFLATE;

  private static final ServiceName SERVICE_NAME = ServiceName.of("test");

  @Rule
  public final JUnitRuleMockery context = new JUnitRuleMockery() {{
    setImposteriser(ClassImposteriser.INSTANCE);
    setThreadingPolicy(new Synchroniser());
  }};

  @Mock
  private StartContext startContext;

  @Mock
  private StopContext stopContext;

  @Mock
  private ServiceController<?> serviceController;

  @Mock
  private SecretKeyService secretKeyService;

  @Mock
  private KeyPairStorageService keyPairStorageService;

  @Mock
  private SecretKeyConfiguration secretKey;

  @Mock
  private KeyPairStorage keyPairStorage;


  @Test(expected = IllegalArgumentException.class)
  public void testWhenNoKeyManagementAlgorithm() throws Exception {
    serviceBuilder().keyManagementAlgorithm(null).build();
  }

  @Test(expected = IllegalArgumentException.class)
  public void testWhenNoContentEncryptionAlgorithm() throws Exception {
    serviceBuilder().contentEncryptionAlgorithm(null).build();
  }

  @Test
  public void testSuccessfulBuild() throws Exception {
    final EncryptionService service = serviceBuilder().build();
    assertThat(service.getKeyManagementAlgorithm(),
        is(equalTo(KEY_MANAGEMENT_ALGORITHM)));
    assertThat(service.getContentEncryptionAlgorithm(),
        is(equalTo(CONTENT_ENCRYPTION_ALGORITHM)));
    assertThat(service.getCompressionAlgorithm(),
        is(equalTo(COMPRESSION_ALGORITHM)));
    assertThat(service.getValue(), is(sameInstance(service)));
  }

  @Test
  public void testStartStop() throws Exception {
    context.checking(new Expectations() {
      {
        oneOf(startContext).getController();
        will(returnValue(serviceController));
        oneOf(stopContext).getController();
        will(returnValue(serviceController));
        allowing(serviceController).getName();
        will(returnValue(SERVICE_NAME));
      }
    });

    final EncryptionService service = serviceBuilder().build();
    service.start(startContext);
    service.stop(stopContext);
  }

  @Test
  public void testGetConfiguration() throws Exception {
    context.checking(new Expectations() {
      {
        allowing(keyPairStorageService).getKeyPairStorage();
        will(returnValue(keyPairStorage));
        allowing(secretKeyService).getSecretKey();
        will(returnValue(secretKey));
      }
    });
    final EncryptionService service = serviceBuilder().build();
    service.setSecretKeyServices(
        Collections.singletonList(() -> secretKeyService));
    service.setKeyPairStorageService(
        () -> keyPairStorageService);

    final EncryptionConfiguration config = service.getConfiguration();
    assertThat(config.getKeyManagementAlgorithm(),
        is(equalTo(KEY_MANAGEMENT_ALGORITHM)));
    assertThat(config.getContentEncryptionAlgorithm(),
        is(equalTo(CONTENT_ENCRYPTION_ALGORITHM)));
    assertThat(config.getCompressionAlgorithm(),
        is(equalTo(COMPRESSION_ALGORITHM)));
    assertThat(config.getKeyPairStorage(), is(sameInstance(keyPairStorage)));
    assertThat(config.getSecretKeys(), is(Collections.singletonList(secretKey)));
  }


  private EncryptionService.Builder serviceBuilder() {
    return EncryptionService.builder()
        .keyManagementAlgorithm(KEY_MANAGEMENT_ALGORITHM)
        .contentEncryptionAlgorithm(CONTENT_ENCRYPTION_ALGORITHM)
        .compressionAlgorithm(COMPRESSION_ALGORITHM);
  }

}