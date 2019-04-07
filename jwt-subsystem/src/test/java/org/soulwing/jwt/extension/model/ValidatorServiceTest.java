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
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;

import java.net.URI;
import java.time.Duration;
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
import org.soulwing.jwt.extension.service.AssertionConfiguration;
import org.soulwing.jwt.extension.service.Authenticator;
import org.soulwing.jwt.extension.service.AuthenticatorFactory;
import org.soulwing.jwt.extension.service.Configuration;
import org.soulwing.jwt.extension.service.EncryptionConfiguration;
import org.soulwing.jwt.extension.service.SignatureConfiguration;
import org.soulwing.jwt.extension.service.TransformConfiguration;

/**
 * Unit tests for {@link ValidatorService}.
 *
 * @author Carl Harris
 */
public class ValidatorServiceTest {

  private static final String ISSUER = "issuer";
  private static final URI ISSUER_URL = URI.create("issuerUrl");
  private static final String AUDIENCE = "audience";
  private static final long TOLERANCE = -1L;
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
  private SignatureService signatureService;

  @Mock
  private EncryptionService encryptionService;

  @Mock
  private ClaimAssertionService assertionService;

  @Mock
  private ClaimTransformService transformService;

  @Mock
  private SignatureConfiguration signatureConfiguration;

  @Mock
  private EncryptionConfiguration encryptionConfiguration;

  @Mock
  private AssertionConfiguration assertion;

  @Mock
  private TransformConfiguration transform;

  @Mock
  private AuthenticatorFactory authenticatorFactory;

  @Mock
  private Authenticator authenticator;

  @Test(expected = IllegalArgumentException.class)
  public void testWhenNoIssuer() throws Exception {
    serviceBuilder().issuer(null).build();
  }

  @Test(expected = IllegalArgumentException.class)
  public void testWhenNoIssuerUrl() throws Exception {
    serviceBuilder().issuerUrl(null).build();
  }

  @Test
  public void testSuccessfulBuild() throws Exception {
    final ValidatorService service = serviceBuilder().build();
    assertThat(service.getIssuer(), is(equalTo(ISSUER)));
    assertThat(service.getIssuerUrl(), is(equalTo(ISSUER_URL)));
    assertThat(service.getAudience(), is(equalTo(AUDIENCE)));
    assertThat(service.getExpirationTolerance(), is(equalTo(TOLERANCE)));
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

        allowing(signatureService).getConfiguration(ISSUER_URL);
        will(returnValue(signatureConfiguration));
        allowing(signatureConfiguration).getTrustStore();
        will(returnValue(null));

        allowing(encryptionService).getConfiguration();
        will(returnValue(encryptionConfiguration));

        allowing(assertionService).getConfiguration();
        will(returnValue(assertion));

        allowing(transformService).getConfiguration();
        will(returnValue(transform));
      }
    });

    final ValidatorService service = serviceBuilder().build();
    service.setSignatureService(() -> signatureService);
    service.setEncryptionService(() -> encryptionService);
    service.setAssertionServices(Collections.singletonList(() -> assertionService));
    service.setTransformServices(Collections.singletonList(() -> transformService));
    service.start(startContext);

    final Configuration config = service.getConfiguration();
    assertThat(config.getProvider(), is(not(nullValue())));
    assertThat(config.getIssuer(), is(equalTo(ISSUER)));
    assertThat(config.getIssuerUrl(), is(equalTo(ISSUER_URL)));
    assertThat(config.getAudience(), is(equalTo(AUDIENCE)));
    assertThat(config.getExpirationTolerance(), is(equalTo(
        Duration.ofSeconds(TOLERANCE))));
    assertThat(config.getSignatureConfiguration(),
        is(sameInstance(signatureConfiguration)));
    assertThat(config.getEncryptionConfiguration(),
        is(sameInstance(encryptionConfiguration)));
    assertThat(config.getAssertions(),
        is(equalTo(Collections.singletonList(assertion))));
    assertThat(config.getTransforms(),
        is(equalTo(Collections.singletonList(transform))));

    context.checking(new Expectations() {
      {
        oneOf(authenticatorFactory).newInstance(config);
        will(returnValue(authenticator));
      }
    });

    assertThat(service.newAuthenticator(), is(sameInstance(authenticator)));
    service.stop(stopContext);

  }

  private ValidatorService.Builder serviceBuilder() {
    return ValidatorService.builder()
        .authenticatorFactory(authenticatorFactory)
        .issuer(ISSUER)
        .issuerUrl(ISSUER_URL)
        .audience(AUDIENCE)
        .expirationTolerance(TOLERANCE);
  }

}