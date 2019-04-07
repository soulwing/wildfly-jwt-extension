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

import java.net.URI;
import java.security.KeyStore;
import java.util.List;

import org.soulwing.jwt.api.JWS;

/**
 * A configuration for validating JWS payloads of bearer tokens.
 *
 * @author Carl Harris
 */
public interface SignatureConfiguration {

  /**
   * Gets the signature algorithm.
   * @return algorithm ID or {@code null} if the algorithm specified in the
   *    JOSE header is to be used
   */
  JWS.Algorithm getAlgorithm();

  /**
   * Gets the URL for the token issuer.
   * <p>
   * This URL is used as the base URL when the JOSE header specifies a relative
   * URL for the {@code x5u} or {@code jku} headers.
   * @return issuer URL or {@code null} to require JOSE headers to use a
   *    fully qualified URL
   */
  URI getIssuerUrl();

  /**
   * Gets a trust store to use in validating signatures that include a
   * certification chain.
   * @return trust store
   */
  KeyStore getTrustStore();

  /**
   * Gets the certificate subject name to require in a valid signature.
   * @return subject name or {@code null} to require that subject name matches
   *    the value of the {@code iss} claim.
   */
  String getCertificateSubjectName();

  /**
   * Gets a flag that indicates whether certificate expiration dates should
   * be checked in the certificate verification process.
   * @return flag state
   */
  boolean isCheckCertificateExpiration();

  /**
   * Gets a flag that indicates whether certificate revocation status should
   * be checked in the certificate verification process.
   * @return flag state
   */
  boolean isCheckCertificateRevocation();

  /**
   * Gets a flag that indicates whether certificate expiration and revocation
   * checks should only be performed against the subject certificate.
   * @return flag state
   */
  boolean isCheckSubjectCertificateOnly();

  /**
   * Gets a list of secret keys to use for shared key signature algorithms.
   * @return secret keys
   */
  List<SecretKeyConfiguration> getSecretKeys();

}
