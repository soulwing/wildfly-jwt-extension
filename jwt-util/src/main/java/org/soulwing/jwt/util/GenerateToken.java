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

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Properties;

import org.soulwing.jwt.api.Claims;
import org.soulwing.jwt.api.JWS;
import org.soulwing.jwt.api.JWTProvider;
import org.soulwing.jwt.api.JWTProviderLocator;
import org.soulwing.s2ks.KeyPairStorage;
import org.soulwing.s2ks.KeyPairStorageLocator;

/**
 * A simple token generator.
 *
 * @author Carl Harris
 */
public class GenerateToken {

  public static void main(String[] args) throws Exception {
    final JWTProvider provider = JWTProviderLocator.getProvider();
    final Instant now = Instant.now();
    final Instant expires = now.plus(30, ChronoUnit.MINUTES);
    final Claims claims = provider.claims()
        .id("f91d9ed1-ef2b-4561-ae0f-24e3f89d22f2")
        .issuer("token-issuer")
        .issuedAt(now)
        .expiresAt(expires)
        .subject("meggan")
        .audience("test-service")
        .set("uid", 12172773L)
        .set("afl", "VT-EMPLOYEE", "VT-STUDENT", "VT-ALUM")
        .set("grp", "uugid=research.summit.app.pre-award,ou=Groups,dc=vt,dc=edu")
        .set("cn", "Meggan Marshall")
        .set("eml", "meggan@vt.edu")
        .build();

    final String token = provider.generator()
        .signature(provider.signatureOperator()
            .algorithm(JWS.Algorithm.RS256)
            .keyProvider(getKeyProvider())
            .build())
        .build()
        .generate(claims);

    System.out.println(token);
  }

  private static KeyPairStorageKeyProvider getKeyProvider() throws Exception {
    return new KeyPairStorageKeyProvider("test", getKeyPairStorage());
  }

  private static KeyPairStorage getKeyPairStorage() throws Exception {
    Properties properties = new Properties();
    properties.setProperty("storageDirectory", getResourcePath("keys"));
    properties.setProperty("passwordFile", getResourcePath("keys/test/key-password"));
    return KeyPairStorageLocator.getInstance("LOCAL",
        properties);
  }

  private static String getResourcePath(String name) throws Exception {
    URL url = GenerateToken.class.getClassLoader().getResource(name);
    if (url == null) throw new FileNotFoundException(name);
    return new File(url.toURI()).getPath();
  }

}
