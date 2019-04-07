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
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;

import java.net.URI;
import java.security.KeyStore;
import java.time.Duration;
import java.util.Collections;
import java.util.function.Function;
import java.util.function.Predicate;

import org.jmock.Expectations;
import org.jmock.api.Invocation;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.jmock.lib.action.CustomAction;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.soulwing.jwt.api.Assertions;
import org.soulwing.jwt.api.Claims;
import org.soulwing.jwt.api.JWE;
import org.soulwing.jwt.api.JWS;
import org.soulwing.jwt.api.JWTProvider;
import org.soulwing.jwt.api.JWTValidator;
import org.soulwing.jwt.api.KeyProvider;
import org.soulwing.jwt.api.PublicKeyLocator;
import org.soulwing.jwt.api.exceptions.JWTAssertionFailedException;
import org.soulwing.jwt.api.exceptions.JWTConfigurationException;
import org.soulwing.s2ks.KeyPairStorage;

/**
 * Unit tests for {@link JWTValidatorFactory}.
 *
 * @author Carl Harris
 */
public class JWTValidatorFactoryTest {

  private static final String ISSUER = "issuer";
  private static final String AUDIENCE = "audience";
  private static final Duration TOLERANCE = Duration.ZERO;

  private static final JWS.Algorithm SIGNATURE_ALGORITHM =
      JWS.Algorithm.HS256;

  private static final JWE.KeyManagementAlgorithm KEY_MANAGEMENT_ALGORITHM =
      JWE.KeyManagementAlgorithm.A128KW;

  private static final JWE.ContentEncryptionAlgorithm CONTENT_ENCRYPTION_ALGORITHM =
      JWE.ContentEncryptionAlgorithm.A128CBC_HS256;

  private static final JWE.CompressionAlgorithm COMPRESSION_ALGORITHM =
      JWE.CompressionAlgorithm.DEFLATE;
  private static final URI ISSUER_URL = URI.create("issuerUrl");
  private static final String SUBJECT_NAME = "subjectName";

  private static KeyStore trustStore;

  @Rule
  public final JUnitRuleMockery context = new JUnitRuleMockery();

  @Mock
  private JWTProvider provider;

  @Mock
  private Configuration configuration;

  @Mock
  private SignatureConfiguration signatureConfiguration;

  @Mock
  private EncryptionConfiguration encryptionConfiguration;

  @Mock
  private AssertionConfiguration assertionConfiguration;

  @Mock
  private SecretKeyConfiguration secretKeyConfiguration;

  @Mock
  private Predicate<Claims> predicate;

  @Mock
  private Function<Claims, JWTAssertionFailedException> errorSupplier;

  @Mock
  private JWTValidator.Builder validatorBuilder;

  @Mock
  private Assertions.Builder assertionsBuilder;

  @Mock
  private JWTValidator validator;

  @Mock
  private Assertions assertions;

  @Mock
  private JWS.Header jwsHeader;

  @Mock
  private JWS.Builder jwsBuilder;

  @Mock
  private JWE.Header jweHeader;

  @Mock
  private JWE.Builder jweBuilder;

  @Mock
  private JWS signatureOperator;

  @Mock
  private JWE encryptionOperator;

  @Mock
  private KeyPairStorage keyPairStorage;

  private JWS.Factory signatureOperatorFactory;

  private JWE.Factory encryptionOperatorFactory;

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
    trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
    trustStore.load(null, null);
  }

  @Test
  public void testNewValidator() throws Exception {
    context.checking(assertionsExpectations(null));
    context.checking(validatorExpectations());

    assertThat(JWTValidatorFactory.getInstance().newValidator(configuration),
        is(sameInstance(validator)));

    context.checking(signatureOperatorExpectations());

    assertThat(signatureOperatorFactory.getOperator(jwsHeader),
        is(sameInstance(signatureOperator)));

    context.checking(encryptionOperatorExpectations(keyPairStorage));
    assertThat(encryptionOperatorFactory.getOperator(jweHeader),
        is(sameInstance(encryptionOperator)));
  }

  @Test
  public void testNewValidatorWithCertificateSubjectName() throws Exception {
    context.checking(assertionsExpectations(SUBJECT_NAME));
    context.checking(validatorExpectations());

    assertThat(JWTValidatorFactory.getInstance().newValidator(configuration),
        is(sameInstance(validator)));

    context.checking(signatureOperatorExpectations());

    assertThat(signatureOperatorFactory.getOperator(jwsHeader),
        is(sameInstance(signatureOperator)));

    context.checking(encryptionOperatorExpectations(keyPairStorage));
    assertThat(encryptionOperatorFactory.getOperator(jweHeader),
        is(sameInstance(encryptionOperator)));
  }

  @Test
  public void testNewValidatorWithNoKeyPairStorage() throws Exception {
    context.checking(assertionsExpectations(SUBJECT_NAME));
    context.checking(validatorExpectations());

    assertThat(JWTValidatorFactory.getInstance().newValidator(configuration),
        is(sameInstance(validator)));

    context.checking(signatureOperatorExpectations());

    assertThat(signatureOperatorFactory.getOperator(jwsHeader),
        is(sameInstance(signatureOperator)));

    context.checking(encryptionOperatorExpectations(null));
    assertThat(encryptionOperatorFactory.getOperator(jweHeader),
        is(sameInstance(encryptionOperator)));
  }


  @Test(expected = JWTConfigurationException.class)
  public void testNewValidatorWhenSignatureAlgorithmMismatch() throws Exception {
    context.checking(assertionsExpectations(null));
    context.checking(validatorExpectations());

    assertThat(JWTValidatorFactory.getInstance().newValidator(configuration),
        is(sameInstance(validator)));

    context.checking(new Expectations() {
      {
        oneOf(provider).signatureOperator();
        will(returnValue(jwsBuilder));
        allowing(signatureConfiguration).getAlgorithm();
        will(returnValue(JWS.Algorithm.HS256));
        allowing(jwsHeader).getAlgorithm();
        will(returnValue(null));
      }
    });

    signatureOperatorFactory.getOperator(jwsHeader);
  }

  @Test(expected = JWTConfigurationException.class)
  public void testNewValidatorWhenEncryptionKeyManagementAlgorithmMismatch()
      throws Exception {
    context.checking(assertionsExpectations(null));
    context.checking(validatorExpectations());

    assertThat(JWTValidatorFactory.getInstance().newValidator(configuration),
        is(sameInstance(validator)));

    context.checking(signatureOperatorExpectations());
    assertThat(signatureOperatorFactory.getOperator(jwsHeader),
        is(sameInstance(signatureOperator)));

    context.checking(encryptionAlgorithmExpectations(
        null,
        CONTENT_ENCRYPTION_ALGORITHM.toToken(),
        COMPRESSION_ALGORITHM.toToken()));

    encryptionOperatorFactory.getOperator(jweHeader);
  }

  @Test(expected = JWTConfigurationException.class)
  public void testNewValidatorWhenEncryptionContentEncryptionAlgorithmMismatch()
      throws Exception {
    context.checking(assertionsExpectations(null));
    context.checking(validatorExpectations());

    assertThat(JWTValidatorFactory.getInstance().newValidator(configuration),
        is(sameInstance(validator)));

    context.checking(signatureOperatorExpectations());
    assertThat(signatureOperatorFactory.getOperator(jwsHeader),
        is(sameInstance(signatureOperator)));

    context.checking(encryptionAlgorithmExpectations(
        KEY_MANAGEMENT_ALGORITHM.toToken(),
        null,
        COMPRESSION_ALGORITHM.toToken()));

    encryptionOperatorFactory.getOperator(jweHeader);
  }

  @Test(expected = JWTConfigurationException.class)
  public void testNewValidatorWhenEncryptionCompressionAlgorithmMismatch()
      throws Exception {
    context.checking(assertionsExpectations(null));
    context.checking(validatorExpectations());

    assertThat(JWTValidatorFactory.getInstance().newValidator(configuration),
        is(sameInstance(validator)));

    context.checking(signatureOperatorExpectations());
    assertThat(signatureOperatorFactory.getOperator(jwsHeader),
        is(sameInstance(signatureOperator)));

    context.checking(encryptionAlgorithmExpectations(
        KEY_MANAGEMENT_ALGORITHM.toToken(),
        CONTENT_ENCRYPTION_ALGORITHM.toToken(),
        null));

    encryptionOperatorFactory.getOperator(jweHeader);
  }


  private Expectations encryptionAlgorithmExpectations(
      String keyManagementAlgorithm, String contentEncryptionAlgorithm,
      String compressionAlgorithm) throws Exception {
    return new Expectations() {
      {
        allowing(provider).encryptionOperator();
        will(returnValue(jweBuilder));
        allowing(jweBuilder).keyManagementAlgorithm(KEY_MANAGEMENT_ALGORITHM);
        will(returnValue(jweBuilder));
        allowing(jweBuilder).contentEncryptionAlgorithm(CONTENT_ENCRYPTION_ALGORITHM);
        will(returnValue(jweBuilder));
        allowing(jweBuilder).compressionAlgorithm(COMPRESSION_ALGORITHM);
        will(returnValue(jweBuilder));

        allowing(encryptionConfiguration).getKeyManagementAlgorithm();
        will(returnValue(KEY_MANAGEMENT_ALGORITHM));
        allowing(encryptionConfiguration).getContentEncryptionAlgorithm();
        will(returnValue(CONTENT_ENCRYPTION_ALGORITHM));
        allowing(encryptionConfiguration).getCompressionAlgorithm();
        will(returnValue(COMPRESSION_ALGORITHM));

        allowing(jweHeader).getKeyManagementAlgorithm();
        will(returnValue(keyManagementAlgorithm));
        allowing(jweHeader).getContentEncryptionAlgorithm();
        will(returnValue(contentEncryptionAlgorithm));
        allowing(jweHeader).getCompressionAlgorithm();
        will(returnValue(compressionAlgorithm));
      }
    };
  }

  private Expectations validatorExpectations() throws Exception {
    return new Expectations() {
      {
        allowing(configuration).getProvider();
        will(returnValue(provider));
        allowing(configuration).getSignatureConfiguration();
        will(returnValue(signatureConfiguration));
        allowing(configuration).getEncryptionConfiguration();
        will(returnValue(encryptionConfiguration));

        oneOf(provider).validator();
        will(returnValue(validatorBuilder));
        oneOf(validatorBuilder).claimsAssertions(assertions);
        will(returnValue(validatorBuilder));
        oneOf(validatorBuilder).signatureOperatorFactory(
            with(any(JWS.Factory.class)));
        will(new CustomAction("capture signature operator factory") {
          @Override
          public Object invoke(Invocation invocation) throws Throwable {
            signatureOperatorFactory = (JWS.Factory) invocation.getParameter(0);
            return validatorBuilder;
          }
        });
        oneOf(validatorBuilder).encryptionOperatorFactory(
            with(any(JWE.Factory.class)));
        will(new CustomAction("capture encryption operator factory") {
          @Override
          public Object invoke(Invocation invocation) throws Throwable {
            encryptionOperatorFactory = (JWE.Factory) invocation.getParameter(0);
            return validatorBuilder;
          }
        });
        oneOf(validatorBuilder).build();
        will(returnValue(validator));
      }
    };
  }

  private Expectations signatureOperatorExpectations() throws Exception {
    return new Expectations() {
      {
        allowing(signatureConfiguration).getAlgorithm();
        will(returnValue(SIGNATURE_ALGORITHM));
        allowing(jwsHeader).getAlgorithm();
        will(returnValue(SIGNATURE_ALGORITHM.toToken()));
        allowing(signatureConfiguration).getTrustStore();
        will(returnValue(trustStore));
        atLeast(1).of(signatureConfiguration).getSecretKeys();
        will(returnValue(Collections.singletonList(secretKeyConfiguration)));
        oneOf(signatureConfiguration).getIssuerUrl();
        will(returnValue(ISSUER_URL));
        oneOf(signatureConfiguration).isCheckCertificateExpiration();
        will(returnValue(true));
        oneOf(signatureConfiguration).isCheckCertificateRevocation();
        will(returnValue(true));
        oneOf(signatureConfiguration).isCheckSubjectCertificateOnly();
        will(returnValue(true));

        oneOf(provider).signatureOperator();
        will(returnValue(jwsBuilder));
        oneOf(jwsBuilder).algorithm(SIGNATURE_ALGORITHM);
        will(returnValue(jwsBuilder));
        oneOf(jwsBuilder).publicKeyLocator(with(any(PublicKeyLocator.class)));
        will(returnValue(jwsBuilder));
        oneOf(jwsBuilder).keyProvider(with(any(KeyProvider.class)));
        will(returnValue(jwsBuilder));
        oneOf(jwsBuilder).build();
        will(returnValue(signatureOperator));
      }
    };
  }

  private Expectations encryptionOperatorExpectations(
      KeyPairStorage keyPairStorage) throws Exception {
    return new Expectations() {
      {
        atLeast(1).of(encryptionConfiguration).getKeyManagementAlgorithm();
        will(returnValue(KEY_MANAGEMENT_ALGORITHM));
        atLeast(1).of(encryptionConfiguration).getContentEncryptionAlgorithm();
        will(returnValue(CONTENT_ENCRYPTION_ALGORITHM));
        atLeast(1).of(encryptionConfiguration).getCompressionAlgorithm();
        will(returnValue(COMPRESSION_ALGORITHM));

        atLeast(1).of(jweHeader).getKeyManagementAlgorithm();
        will(returnValue(KEY_MANAGEMENT_ALGORITHM.toToken()));
        atLeast(1).of(jweHeader).getContentEncryptionAlgorithm();
        will(returnValue(CONTENT_ENCRYPTION_ALGORITHM.toToken()));
        atLeast(1).of(jweHeader).getCompressionAlgorithm();
        will(returnValue(COMPRESSION_ALGORITHM.toToken()));

        atLeast(1).of(encryptionConfiguration).getKeyPairStorage();
        will(returnValue(keyPairStorage));
        if (keyPairStorage == null) {
          atLeast(1).of(encryptionConfiguration).getSecretKeys();
          will(returnValue(Collections.singletonList(secretKeyConfiguration)));
        }

        oneOf(provider).encryptionOperator();
        will(returnValue(jweBuilder));
        oneOf(jweBuilder).keyManagementAlgorithm(KEY_MANAGEMENT_ALGORITHM);
        will(returnValue(jweBuilder));
        oneOf(jweBuilder).contentEncryptionAlgorithm(CONTENT_ENCRYPTION_ALGORITHM);
        will(returnValue(jweBuilder));
        oneOf(jweBuilder).compressionAlgorithm(COMPRESSION_ALGORITHM);
        will(returnValue(jweBuilder));
        oneOf(jweBuilder).keyProvider(with(any(KeyProvider.class)));

        oneOf(jweBuilder).build();
        will(returnValue(encryptionOperator));
      }
    };
  }

  @SuppressWarnings("unchecked")
  private Expectations assertionsExpectations(String subjectName)
      throws Exception {
    return new Expectations() {
      {
        allowing(configuration).getIssuer();
        will(returnValue(ISSUER));
        allowing(configuration).getExpirationTolerance();
        will(returnValue(TOLERANCE));
        allowing(signatureConfiguration).getCertificateSubjectName();
        will(returnValue(subjectName));
        allowing(configuration).getAudience();
        will(returnValue(AUDIENCE));
        allowing(signatureConfiguration).getTrustStore();
        will(returnValue(trustStore));
        allowing(configuration).getAssertions();
        will(returnValue(Collections.singletonList(assertionConfiguration)));

        oneOf(provider).assertions();
        will(returnValue(assertionsBuilder));
        oneOf(assertionsBuilder).requireIssuer(ISSUER);
        will(returnValue(assertionsBuilder));
        oneOf(assertionsBuilder).requireNotExpired(TOLERANCE);
        will(returnValue(assertionsBuilder));
        oneOf(assertionsBuilder).requireAudience(AUDIENCE);
        will(returnValue(assertionsBuilder));
        oneOf(assertionsBuilder).requireSubjectSatisfies(
            with(any(Predicate.class)), with(any(Function.class)));
        will(new CustomAction("test error supplier") {
          @Override
          public Object invoke(Invocation invocation) throws Throwable {
            Function<Claims, JWTAssertionFailedException> supplier =
                (Function<Claims, JWTAssertionFailedException>)
                    invocation.getParameter(1);
            assertThat(supplier.apply(null).getMessage(),
                containsString("`sub`"));
            return assertionsBuilder;
          }
        });

        if (trustStore != null) {
          if (subjectName != null) {
            oneOf(assertionsBuilder).requireCertificateSubjectMatches(subjectName);
          }
          else {
            oneOf(assertionsBuilder).requireCertificateSubjectMatchesIssuer();
          }
          will(returnValue(assertionsBuilder));
        }

        oneOf(assertionConfiguration).getPredicate();
        will(returnValue(predicate));
        oneOf(assertionConfiguration).getErrorSupplier();
        will(returnValue(errorSupplier));

        oneOf(assertionsBuilder).requireSatisfies(predicate, errorSupplier);
        will(returnValue(assertionsBuilder));

        oneOf(assertionsBuilder).build();
        will(returnValue(assertions));
      }
    };
  }

}