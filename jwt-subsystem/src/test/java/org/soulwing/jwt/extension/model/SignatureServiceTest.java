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
import java.security.KeyStore;
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
import org.soulwing.jwt.api.JWS;
import org.soulwing.jwt.extension.service.SecretKeyConfiguration;
import org.soulwing.jwt.extension.service.SignatureConfiguration;

/**
 * Unit tests for {@link SignatureService}.
 *
 * @author Carl Harris
 */
public class SignatureServiceTest {

  private static final JWS.Algorithm ALGORITHM = JWS.Algorithm.HS256;
  private static final String SUBJECT_NAME = "subjectName";
  private static final ServiceName SERVICE_NAME = ServiceName.of("test");
  private static final URI ISSUER_URL = URI.create("issuerUrl");

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
  private TrustStoreService trustStoreService;

  @Mock
  private SecretKeyService secretKeyService;

  @Mock
  private SecretKeyConfiguration secretKey;


  @Test(expected = IllegalArgumentException.class)
  public void testWhenNoAlgorithm() throws Exception {
    serviceBuilder().algorithm(null).build();
  }

  @Test
  public void testSuccessfulBuild() throws Exception {
    final SignatureService service = serviceBuilder().build();
    assertThat(service.getAlgorithm(), is(equalTo(ALGORITHM)));
    assertThat(service.getCertificateSubjectName(), is(equalTo(SUBJECT_NAME)));
    assertThat(service.isCheckCertificateExpiration(), is(true));
    assertThat(service.isCheckCertificateRevocation(), is(true));
    assertThat(service.isCheckSubjectCertificateOnly(), is(true));
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

    final SignatureService service = serviceBuilder().build();
    service.start(startContext);
    service.stop(stopContext);
  }

  @Test
  public void testGetConfiguration() throws Exception {
    final KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
    context.checking(new Expectations() {
      {
        allowing(trustStoreService).getTrustStore();
        will(returnValue(trustStore));
        allowing(secretKeyService).getSecretKey();
        will(returnValue(secretKey));
      }
    });

    final SignatureService service = serviceBuilder().build();
    service.setTrustStoreService(() -> trustStoreService);
    service.setSecretKeyServices(Collections.singletonList(() -> secretKeyService));

    final SignatureConfiguration config = service.getConfiguration(ISSUER_URL);
    assertThat(config.getAlgorithm(), is(equalTo(ALGORITHM)));
    assertThat(config.getIssuerUrl(), is(equalTo(ISSUER_URL)));
    assertThat(config.getCertificateSubjectName(), is(equalTo(SUBJECT_NAME)));
    assertThat(config.isCheckCertificateExpiration(), is(true));
    assertThat(config.isCheckCertificateRevocation(), is(true));
    assertThat(config.isCheckSubjectCertificateOnly(), is(true));
    assertThat(config.getTrustStore(), is(sameInstance(trustStore)));
    assertThat(config.getSecretKeys(), is(Collections.singletonList(secretKey)));
  }


  private SignatureService.Builder serviceBuilder() {
    return SignatureService.builder()
        .algorithm(ALGORITHM)
        .certificateSubjectName(SUBJECT_NAME)
        .checkCertificateExpiration(true)
        .checkCertificateRevocation(true)
        .checkSubjectCertificateOnly(true);
  }

}