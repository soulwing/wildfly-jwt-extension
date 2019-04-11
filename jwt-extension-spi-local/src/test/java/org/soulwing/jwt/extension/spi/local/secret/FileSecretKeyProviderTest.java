/*
 * File created on Apr 7, 2019
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
package org.soulwing.jwt.extension.spi.local.secret;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.soulwing.jwt.extension.spi.local.secret.FileSecretKeyProvider.ENCODING;
import static org.soulwing.jwt.extension.spi.local.secret.FileSecretKeyProvider.Encoding;
import static org.soulwing.jwt.extension.spi.local.secret.FileSecretKeyProvider.PATH;
import static org.soulwing.jwt.extension.spi.local.secret.FileSecretKeyProvider.PROVIDER_NAME;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.Properties;
import java.util.Random;
import java.util.function.Function;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.soulwing.jwt.extension.spi.ClassLoaderServiceLocator;
import org.soulwing.jwt.extension.spi.NoSuchSecretKeyException;
import org.soulwing.jwt.extension.spi.SecretException;
import org.soulwing.jwt.extension.spi.SecretKeyProvider;

/**
 * Unit tests for {@link FileSecretKeyProvider}.
 *
 * @author Carl Harris
 */
public class FileSecretKeyProviderTest {

  private static final int LENGTH = 128;
  private static final String TYPE = "AES";

  private Path path;

  @Before
  public void setUp() throws Exception {
    path = Files.createTempFile("secret", "");
  }

  @After
  public void tearDown() throws Exception {
    Files.deleteIfExists(path);
  }

  @Test
  public void testGetSecretKey() throws Exception {

    final Properties properties = new Properties();
    properties.setProperty(PATH, path.toString());

    final SecretKeyProvider provider = ClassLoaderServiceLocator.INSTANCE.locate(
        SecretKeyProvider.class, PROVIDER_NAME, null);

    final byte[] data = writeSecret(128, path);
    final SecretKey secret = provider.getSecretKey(TYPE, LENGTH, properties);
    assertThat(secret, is(equalTo(new SecretKeySpec(data, TYPE))));
  }

  @Test
  public void testGetSecretKeyBase64() throws Exception {
    final Properties properties = new Properties();
    properties.setProperty(PATH, path.toString());
    properties.setProperty(ENCODING, Encoding.BASE64.name());

    final SecretKeyProvider provider = ClassLoaderServiceLocator.INSTANCE.locate(
        SecretKeyProvider.class, PROVIDER_NAME, null);

    final byte[] data = writeSecret(128, path, b -> Base64.getEncoder().encode(b));
    final SecretKey secret = provider.getSecretKey(TYPE, LENGTH, properties);
    assertThat(secret, is(equalTo(new SecretKeySpec(data, TYPE))));
  }


  @Test(expected = NoSuchSecretKeyException.class)
  public void testGetSecretKeyWhenFileDoesNotExist() throws Exception {
    final Properties properties = new Properties();
    properties.setProperty(PATH, path.toString());

    Files.deleteIfExists(path);

    final SecretKeyProvider provider = ClassLoaderServiceLocator.INSTANCE.locate(
        SecretKeyProvider.class, PROVIDER_NAME, null);

    provider.getSecretKey(TYPE, LENGTH, properties);
  }

  @Test(expected = SecretException.class)
  public void testGetSecretKeyWhenFileIsTooShort() throws Exception {
    final Properties properties = new Properties();
    properties.setProperty(PATH, path.toString());

    final SecretKeyProvider provider = ClassLoaderServiceLocator.INSTANCE.locate(
        SecretKeyProvider.class, PROVIDER_NAME, null);

    writeSecret(128, path);
    provider.getSecretKey(TYPE, 2 * LENGTH, properties);
  }

  private static byte[] writeSecret(int length, Path path) throws IOException {
    return writeSecret(length, path, b -> b);
  }

  private static byte[] writeSecret(int length, Path path,
      Function<byte[], byte[]> encoder) throws IOException {
    try (final OutputStream writer = new FileOutputStream(path.toFile())) {
      byte[] secret = new byte[length / Byte.SIZE];
      new Random().nextBytes(secret);
      writer.write(encoder.apply(secret));
      return secret;
    }
  }

}