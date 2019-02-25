/*
 * File created on Feb 25, 2019
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
package org.soulwing.jwt.util;

import java.io.IOException;
import java.io.StringReader;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.soulwing.jwt.crypto.PublicKeyType;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

/**
 * A simple token generator.
 *
 * @author Carl Harris
 */
public class GenerateToken {

  public static void main(String[] args) throws Exception {

    final String token = JWT.create()
        .withIssuer("fakeIssuer")
        .withAudience("summit")
        .withSubject("ceharris")
        .withArrayClaim("grp", new String[] { "valid-user",
            "research.summit.app.pre-award",
            "research.summit.app.app-admin",
            "research.summit.app.pre-award-manager" })
        .sign(getAlgorithm());

    System.out.println(token);
  }

  private static Algorithm getAlgorithm()
      throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
    final String name = getRequiredEnv("JWT_ALGORITHM");
    switch (name) {
      case "none":
        return Algorithm.none();
      case "HS256":
        return Algorithm.HMAC256(loadSecretKey());
      case "HS384":
        return Algorithm.HMAC384(loadSecretKey());
      case "HS512":
        return Algorithm.HMAC512(loadSecretKey());
      case "RS256":
        return Algorithm.RSA256(null,
            (RSAPrivateKey) loadPrivateKey(PublicKeyType.RSA));
      case "RS384":
        return Algorithm.RSA384(null,
            (RSAPrivateKey) loadPrivateKey(PublicKeyType.RSA));
      case "RS512":
        return Algorithm.RSA512(null,
            (RSAPrivateKey) loadPrivateKey(PublicKeyType.RSA));
      case "ES256":
        return Algorithm.ECDSA256(null,
            (ECPrivateKey) loadPrivateKey(PublicKeyType.EC));
      case "ES384":
        return Algorithm.ECDSA384(null,
            (ECPrivateKey) loadPrivateKey(PublicKeyType.EC));
      case "ES512":
        return Algorithm.ECDSA512(null,
            (ECPrivateKey) loadPrivateKey(PublicKeyType.EC));
      default:
        throw new IllegalArgumentException("unrecognized algorithm");
    }
  }

  private static PrivateKey loadPrivateKey(PublicKeyType type)
      throws IOException, InvalidKeySpecException, NoSuchAlgorithmException {
    final String text = getRequiredEnv("JWT_PRIVATE_KEY").replaceAll("\\\\n", "\n");
    System.out.println(text);
    try (final PemReader reader =
        new PemReader(new StringReader(text))) {
      final PemObject obj = reader.readPemObject();
      if (obj == null) throw new InvalidKeySpecException();
      final PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(obj.getContent());
      final KeyFactory kf = KeyFactory.getInstance(type.name());
      return kf.generatePrivate(spec);
    }
  }

  private static PublicKey loadPublicKey(PublicKeyType type)
      throws IOException, InvalidKeySpecException, NoSuchAlgorithmException {
    try (final PemReader reader =
             new PemReader(new StringReader(getRequiredEnv("JWT_PUBLIC_KEY")))) {
      final PemObject obj = reader.readPemObject();
      if (obj == null) throw new InvalidKeySpecException();
      final X509EncodedKeySpec spec = new X509EncodedKeySpec(obj.getContent());
      final KeyFactory kf = KeyFactory.getInstance(type.name());
      return kf.generatePublic(spec);
    }
  }

  private static byte[] loadSecretKey() {
    return Base64.decode(getRequiredEnv("JWT_SECRET_KEY"));
  }

  private static String getRequiredEnv(String name) {
    final String value = System.getenv(name);
    if (value == null) {
      throw new IllegalArgumentException(name + " environment variable is not set");
    }
    return value;
  }



}
