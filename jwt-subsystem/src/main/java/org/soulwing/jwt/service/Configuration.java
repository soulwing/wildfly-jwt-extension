/*
 * File created on Feb 19, 2019
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
package org.soulwing.jwt.service;

import java.nio.file.attribute.UserPrincipal;
import java.security.PublicKey;
import java.util.Map;
import java.util.function.Predicate;

import org.soulwing.jwt.api.Transformer;

/**
 * An (immutable) configuration for an {@link Authenticator}.
 *
 * @author Carl Harris
 */
public interface Configuration {

  /**
   * Gets the algorithm.
   * @return algorithm
   */
  String getAlgorithm();

  /**
   * Gets the clock skew tolerance in milliseconds.
   * @return clock skew tolerance
   */
  long getClockSkewTolerance();

  /**
   * Gets the specified secret key.
   * @param keyId ID of the key to retrieve
   * @return secret key
   * @throws NoSuchKeyException if the specified key is not available
   */
  byte[] getSecretKey(String keyId) throws NoSuchKeyException;

  /**
   * Gets the secret keys associated with this configuration.
   * <p>
   * The keys of the returned map are values that may appear as a key identifier
   * ({@code kid}) in the header of JWT; the corresponding value is a the
   * secret to be used to derive the secret key for HMAC signature validation.
   * <p>
   * As a special case, if the map contains the key {@code default} the
   * corresponding value is to be used as the secret for JWTs whose headers do
   * not specify a key identifier.
   * @return map of secret key identifiers and corresponding secrets
   */
  Map<String, byte[]> getSecretKeys();

  /**
   * Gets the specified public key.
   * @param keyId ID of the key to retrieve
   * @return public key
   * @throws NoSuchKeyException if the specified key is not available
   */
  PublicKey getPublicKey(String keyId) throws NoSuchKeyException;

  /**
   * Gets the public keys associated with this configuration.
   * <p>
   * The keys of the returned map are values that may appear as a key identifier
   * ({@code kid}) in the header of JWT.
   * <p>
   * As a special case, if the map contains the key {@code default} the
   * corresponding key is to be used for for JWTs whose headers do
   * not specify a key identifier.
   * @return map of public key identifiers and corresponding public keys
   */
  Map<String, PublicKey> getPublicKeys();

  /**
   * Gets the claim assertion predicates associated with this configuration.
   * <p>
   * The keys of the returned map correspond to the names of claims that
   * may be appear in the {@link UserPrincipal} after a successful
   * authentication.  The corresponding value is a predicate that should be
   * applied to determine whether the value associated with the named claim
   * satisfies the requirement of the assertion.
   * @return claim assertion map
   */
  Map<String, Predicate<Object>> getClaimAssertions();

  /**
   * Gets the identity assertion attribute transformers associated with this 
   * configuration.
   * <p>
   * The keys of the returned map correspond to the names of attributes that
   * may be appear in the {@link UserPrincipal} after a successful 
   * authentication.  The corresponding value is a function that should be
   * applied to each attribute of the given name to transform the value in
   * some manner. 
   * @return attribute transformer map
   */
  Map<String, Transformer<Object, Object>> getClaimTransforms();
  
}
