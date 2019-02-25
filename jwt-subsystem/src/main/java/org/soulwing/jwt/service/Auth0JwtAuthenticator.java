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


import static org.soulwing.jwt.service.ServiceLogger.LOGGER;

import java.security.PublicKey;
import java.security.interfaces.ECPublicKey;
import java.security.interfaces.RSAPublicKey;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

/**
 * An {@link Authenticator} that uses the Auth0 JWT library.
 *
 * @author Carl Harris
 */
public class Auth0JwtAuthenticator implements Authenticator {

  private final Configuration config;

  Auth0JwtAuthenticator(Configuration config) {
    this.config = config;
  }

  @Override
  public Credential validate(String token) throws AuthenticationException {
    try {
      LOGGER.info("validating token '" + token + "'");
      final DecodedJWT decodedToken = JWT.decode(token);
      if (!config.getAlgorithm().equals(decodedToken.getAlgorithm())) {
        LOGGER.warn("algorithm mismatch; expected " + config.getAlgorithm()
            + " but got " + decodedToken.getAlgorithm());
        throw new AuthenticationException("algorithm mismatch");
      }

      final String keyId = keyIdOrDefault(decodedToken.getKeyId());

      final Algorithm algorithm = algorithm(config.getAlgorithm(), keyId);

      JWT.require(algorithm)
          .acceptLeeway(config.getClockSkewTolerance())
          .build()
          .verify(token);

      validateAssertions(decodedToken);

      LOGGER.info("authentication successful");
      return new JwtCredential(new DelegatingUserPrincipal(decodedToken));
    }
    catch (JWTVerificationException ex) {
      throw new AuthenticationException(ex.getMessage(), ex);
    }
  }

  private void validateAssertions(DecodedJWT decodedToken)
      throws ClaimAssertionFailedException {
    for (final String name : config.getClaimAssertions().keySet()) {
      if (!config.getClaimAssertions().get(name).test(
          decodedToken.getClaim(name).as(Object.class))) {
        LOGGER.warn("assertion on claim '" + name + "' failed; value was "
            + decodedToken.getClaim(name).as(Object.class));
        throw new ClaimAssertionFailedException("assertion on claim'" + name +
            "' failed");
      }
    }
  }

  private String keyIdOrDefault(String keyId) {
    if (keyId == null) return "default";
    return keyId;
  }

  private Algorithm algorithm(String name, String keyId)
      throws NoSuchKeyException, InvalidKeyLengthException, InvalidKeyTypeException {
    switch (name) {
      case "none":
        return Algorithm.none();
      case "HS256":
        return Algorithm.HMAC256(secretKey(keyId, 256));
      case "HS384":
        return Algorithm.HMAC384(secretKey(keyId, 384));
      case "HS512":
        return Algorithm.HMAC512(secretKey(keyId, 512));
      case "RS256":
        return Algorithm.RSA256(rsaPublicKey(keyId, 256), null);
      case "RS384":
        return Algorithm.RSA384(rsaPublicKey(keyId, 384), null);
      case "RS512":
        return Algorithm.RSA512(rsaPublicKey(keyId, 512), null);
      case "ES256":
        return Algorithm.ECDSA256(ecPublicKey(keyId, 256), null);
      case "ES384":
        return Algorithm.ECDSA384(ecPublicKey(keyId, 384), null);
      case "ES512":
        return Algorithm.ECDSA512(ecPublicKey(keyId, 512), null);
      default:
        throw new IllegalArgumentException("unrecognized algorithm");
    }
  }

  private byte[] secretKey(String keyId, int bitLength)
      throws NoSuchKeyException, InvalidKeyLengthException {
    final byte[] secret = config.getSecretKey(keyId);
    if (secret.length * 8 < bitLength) {
      throw new InvalidKeyLengthException("secret key '" + keyId
          + "' has fewer than " + bitLength + " bits of content");
    }
    return secret;
  }

  private RSAPublicKey rsaPublicKey(String keyId, int bitLength)
      throws NoSuchKeyException, InvalidKeyLengthException, InvalidKeyTypeException {

    final PublicKey key = config.getPublicKey(keyId);
    if (!(key instanceof RSAPublicKey)) {
      throw new InvalidKeyTypeException("public key '" + keyId
          + "' is not an RSA public key");
    }

    final int keyLength = ((RSAPublicKey) key).getModulus().bitLength();
    if (keyLength < 2048) {
      throw new InvalidKeyLengthException("RSA public key '" + keyId
          + "' is only " + keyLength + " bits; 2048 or more or required");
    }

    if (bitLength == 384 && keyLength < 3072) {
      LOGGER.warn("RSA keys of at least 3072 bits are recommended for 384-bit signature validation");
    }
    if (bitLength == 512 && keyLength < 4096) {
      LOGGER.warn("RSA keys of at least 4096 bits are recommended for 512-bit signature validation");
    }

    return (RSAPublicKey) key;
  }

  private ECPublicKey ecPublicKey(String keyId, int bitLength)
      throws NoSuchKeyException, InvalidKeyLengthException, InvalidKeyTypeException {

    final PublicKey key = config.getPublicKey(keyId);
    if (!(key instanceof ECPublicKey)) {
      throw new InvalidKeyTypeException("public key '" + keyId
          + "' is not an EC public key");
    }

    final int keyLength = ((ECPublicKey) key).getParams().getOrder().bitLength();
    if (keyLength < bitLength) {
      throw new InvalidKeyLengthException("EC public key '" + keyId
          + "' has fewer than " + bitLength + " bits of content");
    }

    return (ECPublicKey) key;
  }


}
