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
package org.soulwing.jwt.extension.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

import java.net.URI;
import java.security.KeyStore;
import java.time.Duration;
import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.soulwing.jwt.api.JWS;
import org.soulwing.jwt.api.JWTProvider;
import org.soulwing.jwt.api.JWTProviderLocator;

/**
 * Unit tests for {@link DefaultAuthenticatorFactory}.
 *
 * @author Carl Harris
 */
public class DefaultAuthenticatorFactoryTest {

  @Test
  public void testNewAuthenticator() throws Exception {
    assertThat(DefaultAuthenticatorFactory.INSTANCE
        .newInstance(new MockConfiguration()),
        is(instanceOf(JwtAuthenticator.class)));

  }

  private static class MockConfiguration implements Configuration {

    @Override
    public JWTProvider getProvider() {
      return JWTProviderLocator.getProvider();
    }

    @Override
    public String getIssuer() {
      return "test-issuer";
    }

    @Override
    public URI getIssuerUrl() {
      return URI.create("test-issuer-url");
    }

    @Override
    public Duration getExpirationTolerance() {
      return Duration.ZERO;
    }

    @Override
    public String getAudience() {
      return null;
    }

    @Override
    public SignatureConfiguration getSignatureConfiguration() {
      return new SignatureConfiguration() {
        @Override
        public JWS.Algorithm getAlgorithm() {
          return JWS.Algorithm.HS256;
        }

        @Override
        public URI getIssuerUrl() {
          return MockConfiguration.this.getIssuerUrl();
        }

        @Override
        public KeyStore getTrustStore() {
          return null;
        }

        @Override
        public String getCertificateSubjectName() {
          return null;
        }

        @Override
        public boolean isCheckCertificateExpiration() {
          return false;
        }

        @Override
        public boolean isCheckCertificateRevocation() {
          return false;
        }

        @Override
        public boolean isCheckSubjectCertificateOnly() {
          return false;
        }

        @Override
        public List<SecretKeyConfiguration> getSecretKeys() {
          return null;
        }
      };
    }

    @Override
    public EncryptionConfiguration getEncryptionConfiguration() {
      return null;
    }

    @Override
    public List<AssertionConfiguration> getAssertions() {
      return Collections.emptyList();
    }

    @Override
    public List<TransformConfiguration> getTransforms() {
      return Collections.emptyList();
    }
  }

}