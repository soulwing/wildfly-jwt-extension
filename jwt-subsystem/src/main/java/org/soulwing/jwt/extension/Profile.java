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
package org.soulwing.jwt.extension;

import java.io.Serializable;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

import org.soulwing.jwt.api.Transformer;
import org.soulwing.jwt.service.Configuration;
import org.soulwing.jwt.service.NoSuchKeyException;

/**
 * A {@link Configuration} for a JWT client.
 *
 * @author Carl Harris
 */
class Profile implements Configuration, Serializable {

  private static final long serialVersionUID = 6170159365985362873L;

  private final Map<String, byte[]> secretKeys = new HashMap<>();
  private final Map<String, PublicKey> publicKeys = new HashMap<>();

  private final Map<String, Predicate<Object>> claimAssertions =
      new HashMap<>();

  private final Map<String, Transformer<Object, Object>> claimTransforms =
      new HashMap<>();

  private String name;
  private String algorithm;
  private long clockSkewTolerance;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public String getAlgorithm() {
    return algorithm;
  }

  public void setAlgorithm(String algorithm) {
    this.algorithm = algorithm;
  }

  @Override
  public long getClockSkewTolerance() {
    return clockSkewTolerance;
  }

  void setClockSkewTolerance(long clockSkewTolerance) {
    this.clockSkewTolerance = clockSkewTolerance;
  }

  @Override
  public byte[] getSecretKey(String keyId) throws NoSuchKeyException {
    return Optional.ofNullable(secretKeys.get(keyId))
        .orElseThrow(() -> new NoSuchKeyException(
            "secret key '" + keyId + "' not found"));
  }

  @Override
  public Map<String, byte[]> getSecretKeys() {
    return secretKeys;
  }

  void putSecretKey(String kid, byte[] secret) {
    secretKeys.put(kid, secret);
  }

  @Override
  public PublicKey getPublicKey(String keyId) throws NoSuchKeyException {
    return Optional.ofNullable(publicKeys.get(keyId))
        .orElseThrow(() -> new NoSuchKeyException(
            "public key '" + keyId + "' not found"));
  }

  @Override
  public Map<String, PublicKey> getPublicKeys() {
    return publicKeys;
  }

  void putPublicKey(String kid, PublicKey publicKey) {
    publicKeys.put(kid, publicKey);
  }

  @Override
  public Map<String, Predicate<Object>> getClaimAssertions() {
    return claimAssertions;
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  void putClaimAssertion(String claimName, Predicate predicate) {
    claimAssertions.put(claimName, predicate);
  }

  @Override
  public Map<String, Transformer<Object, Object>> getClaimTransforms() {
    return claimTransforms;
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  void putClaimTransform(String attributeName,
     Transformer transformer) {
    claimTransforms.put(attributeName, transformer);
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder();
    sb.append("profile=").append(name).append("[");
    sb.append("algorithm=").append(algorithm).append(", ");
    sb.append("clockSkewTolerance=").append(clockSkewTolerance).append(", ");
    sb.append("secretKeys=").append(secretKeys).append(", ");
    sb.append("publicKeys=").append(publicKeys).append(", ");
    sb.append("claimAssertions=").append(claimAssertions).append(", ");
    sb.append("claimTransforms=").append(claimTransforms).append("]");
    return sb.toString();
  }
}
