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

import java.util.Objects;
import java.util.Optional;

import org.soulwing.jwt.api.Assertions;
import org.soulwing.jwt.api.JWE;
import org.soulwing.jwt.api.JWS;
import org.soulwing.jwt.api.JWTProvider;
import org.soulwing.jwt.api.JWTValidator;
import org.soulwing.jwt.api.exceptions.JWTAssertionFailedException;
import org.soulwing.jwt.api.exceptions.JWTConfigurationException;
import org.soulwing.jwt.api.locator.JcaPublicKeyLocator;
import org.soulwing.jwt.api.locator.JcaX509CertificateValidator;

/**
 * A factory that produces JWT validator instances.
 *
 * @author Carl Harris
 */
class JWTValidatorFactory {

  private static final JWTValidatorFactory INSTANCE = new JWTValidatorFactory();

  static JWTValidatorFactory getInstance() {
    return INSTANCE;
  }

  JWTValidator newValidator(Configuration configuration)
      throws JWTConfigurationException {
    final JWTProvider provider = configuration.getProvider();
    final JWTValidator.Builder builder = provider.validator()
        .claimsAssertions(newAssertions(configuration))
        .signatureOperatorFactory(
            newSignatureOperator(configuration.getSignatureConfiguration(),
                provider));

    if (configuration.getEncryptionConfiguration() != null) {
      builder.encryptionOperatorFactory(newEncryptionOperator(
          configuration.getEncryptionConfiguration(),
          provider));
    }

    return builder.build();
  }

  private JWS.Factory newSignatureOperator(SignatureConfiguration configuration,
      JWTProvider provider) {
    return header -> {
      final JWS.Builder builder = provider.signatureOperator();
      builder.algorithm(signatureAlgorithm(configuration.getAlgorithm(), header));
      if (configuration.getSecretKeys() != null) {
        builder.keyProvider(new ListSecretKeyProvider(
            configuration.getSecretKeys()));
      }
      if (configuration.getTrustStore() != null) {
        builder.publicKeyLocator(JcaPublicKeyLocator.builder()
            .chainLoader(new CertificateChainLoader(configuration.getIssuerUrl()))
            .certificateValidator(JcaX509CertificateValidator.builder()
                .trustStore(configuration.getTrustStore())
                .checkExpiration(configuration.isCheckCertificateExpiration())
                .checkRevocation(configuration.isCheckCertificateRevocation())
                .checkSubjectOnly(configuration.isCheckSubjectCertificateOnly())
                .build())
            .build());
      }
      return builder.build();
    };
  }

  private JWS.Algorithm signatureAlgorithm(JWS.Algorithm algorithm,
      JWS.Header header) throws JWTConfigurationException {
    final JWS.Algorithm headerAlgorithm =
        Optional.ofNullable(header.getAlgorithm())
          .map(JWS.Algorithm::of).orElse(null);

    if (algorithm != null && !algorithm.equals(headerAlgorithm)) {
      throw new JWTConfigurationException("required algorithm is `"
          + algorithm.toToken());
    }
    return headerAlgorithm;
  }

  private JWE.Factory newEncryptionOperator(
      EncryptionConfiguration configuration, JWTProvider provider) {
    return header -> {
      final JWE.Builder builder = provider.encryptionOperator();
      builder.keyManagementAlgorithm(keyManagementAlgorithm(
          configuration.getKeyManagementAlgorithm(), header));
      builder.contentEncryptionAlgorithm(contentEncryptionAlgorithm(
          configuration.getContentEncryptionAlgorithm(), header));
      builder.compressionAlgorithm(compressionAlgorithm(
          configuration.getCompressionAlgorithm(), header));

      if (configuration.getKeyPairStorage() != null) {
        builder.keyProvider(new KeyPairStorageKeyProvider(
            configuration.getKeyPairStorage()));
      }
      else if (configuration.getSecretKeys() != null) {
        builder.keyProvider(new ListSecretKeyProvider(
            configuration.getSecretKeys()));
      }

      return builder.build();
    };

  }

  private JWE.KeyManagementAlgorithm keyManagementAlgorithm(
      JWE.KeyManagementAlgorithm algorithm,
      JWE.Header header) throws JWTConfigurationException {
    final JWE.KeyManagementAlgorithm headerAlgorithm =
        Optional.ofNullable(header.getKeyManagementAlgorithm())
            .map(JWE.KeyManagementAlgorithm::of)
            .orElse(null);
    if (algorithm != null && !algorithm.equals(headerAlgorithm)) {
      throw new JWTConfigurationException(
          "required key management algorithm is `" + algorithm.toToken());
    }
    return headerAlgorithm;
  }

  private JWE.ContentEncryptionAlgorithm contentEncryptionAlgorithm(
      JWE.ContentEncryptionAlgorithm algorithm,
      JWE.Header header) throws JWTConfigurationException {
    final JWE.ContentEncryptionAlgorithm headerAlgorithm =
        Optional.ofNullable(header.getContentEncryptionAlgorithm())
            .map(JWE.ContentEncryptionAlgorithm::of)
            .orElse(null);
    if (algorithm != null && !algorithm.equals(headerAlgorithm)) {
      throw new JWTConfigurationException(
          "required content encryption algorithm is `" + algorithm.toToken());
    }
    return headerAlgorithm;
  }

  private JWE.CompressionAlgorithm compressionAlgorithm(
      JWE.CompressionAlgorithm algorithm,
      JWE.Header header) throws JWTConfigurationException {
    final JWE.CompressionAlgorithm headerAlgorithm =
        Optional.ofNullable(header.getCompressionAlgorithm())
            .map(JWE.CompressionAlgorithm::of)
            .orElse(null);
    if (algorithm != null && !algorithm.equals(headerAlgorithm)) {
      throw new JWTConfigurationException(
          "required compression algorithm is `");
    }
    return headerAlgorithm;
  }

  private Assertions newAssertions(Configuration configuration) {
    final Assertions.Builder builder = configuration.getProvider().assertions();
    builder.requireIssuer(configuration.getIssuer());
    builder.requireNotExpired(configuration.getExpirationTolerance());
    builder.requireSubjectSatisfies(Objects::nonNull,
        v -> new JWTAssertionFailedException("`sub` claim is required"));

    if (configuration.getSignatureConfiguration().getTrustStore() != null) {
      final String subjectName = configuration.getSignatureConfiguration()
          .getCertificateSubjectName();
      if (subjectName != null) {
        builder.requireCertificateSubjectMatches(subjectName);
      }
      else {
        builder.requireCertificateSubjectMatchesIssuer();
      }
    }

    if (configuration.getAudience() != null) {
      builder.requireAudience(configuration.getAudience());
    }

    configuration.getAssertions().forEach(assertion ->
        builder.requireSatisfies(assertion.getPredicate(),
        assertion.getErrorSupplier()));

    return builder.build();
  }

}
