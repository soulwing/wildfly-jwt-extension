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
package org.soulwing.jwt.crypto;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyPairGenerator;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Random;

import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemWriter;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Unit tests for {@link PublicKeyLoader}.
 *
 * @author Carl Harris
 */
public class PublicKeyLoaderTest {

  private static PublicKey rsaKey;
  private static PublicKey ecKey;
  private static byte[] garbage = new byte[2048];

  private Path path;

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
    final KeyPairGenerator rsaGenerator = KeyPairGenerator.getInstance("RSA");
    rsaGenerator.initialize(2048);
    rsaKey = rsaGenerator.generateKeyPair().getPublic();

    final KeyPairGenerator ecGenerator = KeyPairGenerator.getInstance("EC");
    ecGenerator.initialize(256);
    ecKey = ecGenerator.generateKeyPair().getPublic();

    final Random random = new Random();
    random.nextBytes(garbage);
  }

  @Before
  public void setUp() throws Exception {
    path = Files.createTempFile("test", "key");
  }

  @After
  public void tearDown() throws Exception {
    Files.delete(path);
  }

  @Test
  public void testLoadDerEncodedRsaKey() throws Exception {
    writeBytes(rsaKey.getEncoded());
    final PublicKey key = PublicKeyLoader.loadKey(path,
        PublicKeyFormat.DER, PublicKeyType.RSA);

    assertThat(key, is(equalTo(rsaKey)));
  }

  @Test
  public void testLoadPemEncodedRsaKey() throws Exception {
    writePem(rsaKey.getEncoded());
    final PublicKey key = PublicKeyLoader.loadKey(path,
        PublicKeyFormat.PEM, PublicKeyType.RSA);

    assertThat(key, is(equalTo(rsaKey)));
  }

  @Test
  public void testLoadDerEncodedEcKey() throws Exception {
    writeBytes(ecKey.getEncoded());
    final PublicKey key = PublicKeyLoader.loadKey(path,
        PublicKeyFormat.DER, PublicKeyType.EC);

    assertThat(key, is(equalTo(ecKey)));
  }

  @Test
  public void testLoadPemEncodedEcKey() throws Exception {
    writePem(ecKey.getEncoded());
    final PublicKey key = PublicKeyLoader.loadKey(path,
        PublicKeyFormat.PEM, PublicKeyType.EC);

    assertThat(key, is(equalTo(ecKey)));
  }

  @Test(expected = InvalidKeySpecException.class)
  public void testLoadInvalidDerRsaContent() throws Exception {
    writeBytes(garbage);
    PublicKeyLoader.loadKey(path, PublicKeyFormat.DER, PublicKeyType.RSA);
  }

  @Test(expected = InvalidKeySpecException.class)
  public void testLoadInvalidPemRsaContent() throws Exception {
    writePem(garbage);
    PublicKeyLoader.loadKey(path, PublicKeyFormat.PEM, PublicKeyType.RSA);
  }

  @Test(expected = InvalidKeySpecException.class)
  public void testLoadInvalidDerEcContent() throws Exception {
    writeBytes(garbage);
    PublicKeyLoader.loadKey(path, PublicKeyFormat.DER, PublicKeyType.EC);
  }

  @Test(expected = InvalidKeySpecException.class)
  public void testLoadInvalidPemEcContent() throws Exception {
    writePem(garbage);
    PublicKeyLoader.loadKey(path, PublicKeyFormat.PEM, PublicKeyType.EC);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testLoadWhenFileNotFound() throws Exception {
    PublicKeyLoader.loadKey(Paths.get("some/invalid/path"),
        PublicKeyFormat.DER, PublicKeyType.RSA);
  }


  @Test(expected = IllegalArgumentException.class)
  public void testLoadPemWhenNotPem() throws Exception {
    writeBytes(garbage);
    PublicKeyLoader.loadKey(path, PublicKeyFormat.PEM, PublicKeyType.RSA);
  }

  private void writeBytes(byte[] encoded) throws IOException {
    Files.write(path, encoded);
  }

  private void writePem(byte[] encoded)
      throws IOException {
    try (final PemWriter writer = new PemWriter(
        new OutputStreamWriter(new FileOutputStream(path.toFile())))) {
      final String description = "PUBLIC KEY";
      final PemObject object = new PemObject(description, encoded);
      writer.writeObject(object);
    }
  }

}
