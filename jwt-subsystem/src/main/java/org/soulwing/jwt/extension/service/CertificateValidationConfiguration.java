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

import java.security.KeyStore;

/**
 * A configuration for certificate validation.
 *
 * @author Carl Harris
 */
public interface CertificateValidationConfiguration {

  /**
   * Gets the key store containing trust anchors for certificate chains.
   * @return trust store
   */
  KeyStore getTrustStore();

  /**
   * Tests whether certificates should be checked for expiration.
   * @return {@code true} if certificate expiration checks should be performed
   */
  boolean isCheckExpiration();

  /**
   * Tests whether certificates should be checked for revocation.
   * @return {@code true} if certificate revocation checks should be performed
   */
  boolean isCheckRevocation();

  /**
   * Tests whether expiration and/or revocation checks should be performed only
   * on the subject certificate.
   * @return {@code true} if expiration/revocation checks should be performed
   *    only on the subject certificate
   */
  boolean isSubjectOnly();

}
