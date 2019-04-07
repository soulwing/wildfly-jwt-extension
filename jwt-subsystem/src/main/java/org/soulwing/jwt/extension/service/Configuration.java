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
import java.time.Duration;
import java.util.List;

import org.soulwing.jwt.api.JWTProvider;

/**
 * An (immutable) configuration for an {@link Authenticator}.
 *
 * @author Carl Harris
 */
public interface Configuration {

  /**
   * Gets the JWT provider.
   * @return JWT provider
   */
  JWTProvider getProvider();

  /**
   * Gets the name which must appear in the {@code iss} claim of valid
   * bearer tokens.
   * @return issuer name
   */
  String getIssuer();

  /**
   * Gets the base URL of the token issuer.
   * @return token issuer URL
   */
  URI getIssuerUrl();

  /**
   * Gets the tolerance to allow in expiration time to account for clock skew
   * @return tolerance
   */
  Duration getExpirationTolerance();

  /**
   * Gets the audience name which must appear in the {@code aud} claim of
   * valid tokens.
   * @return audience name or {@code null} if no assertion should be made on
   *    the value of the audience claim
   */
  String getAudience();

  /**
   * Gets the configuration to use for bearer token signature validation.
   * @return signature configuration
   */
  SignatureConfiguration getSignatureConfiguration();

  /**
   * Gets the configuration to use for bearer token decryption.
   * @return encryption configuration or {@code null} if decryption of
   *    bearer tokens is not needed
   */
  EncryptionConfiguration getEncryptionConfiguration();

  /**
   * Gets the claim assertions associated with this configuration.
   * @return list of assertions
   */
  List<AssertionConfiguration> getAssertions();

  /**
   * Gets the claim transforms associated with this configuration.
   * <p>
   * @return list of transformss
   */
  List<TransformConfiguration> getTransforms();
  
}
