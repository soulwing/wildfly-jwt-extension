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

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.soulwing.jwt.extension.spi.ClassLoaderServiceLocator;
import org.soulwing.jwt.extension.spi.NoSuchSecretException;
import org.soulwing.jwt.extension.spi.Secret;
import org.soulwing.jwt.extension.spi.SecretProvider;

/**
 * Unit tests for {@link FileSecretProvider}.
 *
 * @author Carl Harris
 */
public class FileSecretProviderTest {

  private static final String SECRET = "this is a secret";

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
  public void testGetSecretUsingDefaultEncoding() throws Exception {
    writeSecret(SECRET, StandardCharsets.UTF_8, path);

    final Properties properties = new Properties();
    properties.setProperty(FileSecretProvider.PATH, path.toString());

    final SecretProvider provider = ClassLoaderServiceLocator.INSTANCE.locate(
        SecretProvider.class, FileSecretProvider.PROVIDER_NAME, null);

    final Secret secret = provider.getSecret(properties);
    assertThat(secret.asString(), is(equalTo(SECRET)));
  }

  @Test
  public void testGetSecretUsingSpecificEncoding() throws Exception {
    writeSecret(SECRET, StandardCharsets.UTF_16BE, path);

    final Properties properties = new Properties();
    properties.setProperty(FileSecretProvider.PATH, path.toString());
    properties.setProperty(FileSecretProvider.ENCODING,
        StandardCharsets.UTF_16BE.name());

    final SecretProvider provider = ClassLoaderServiceLocator.INSTANCE.locate(
        SecretProvider.class, FileSecretProvider.PROVIDER_NAME, null);

    final Secret secret = provider.getSecret(properties);
    assertThat(secret.asString(), is(equalTo(SECRET)));
  }

  @Test(expected = NoSuchSecretException.class)
  public void testGetSecretWhenFileDoesNotExist() throws Exception {
    final Properties properties = new Properties();
    properties.setProperty(FileSecretProvider.PATH, path.toString());

    Files.deleteIfExists(path);

    final SecretProvider provider = ClassLoaderServiceLocator.INSTANCE.locate(
        SecretProvider.class, FileSecretProvider.PROVIDER_NAME, null);

    provider.getSecret(properties);
  }

  private static void writeSecret(String secret, Charset encoding,
      Path path) throws IOException {
    try (final Writer writer = new OutputStreamWriter(
        new FileOutputStream(path.toFile()), encoding)) {
      writer.write(secret);
    }
  }
}