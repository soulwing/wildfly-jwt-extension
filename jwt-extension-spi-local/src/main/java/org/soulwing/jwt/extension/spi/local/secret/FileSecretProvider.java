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

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Properties;

import org.soulwing.jwt.extension.spi.NoSuchSecretException;
import org.soulwing.jwt.extension.spi.Secret;
import org.soulwing.jwt.extension.spi.SecretException;
import org.soulwing.jwt.extension.spi.SecretProvider;

/**
 * A {@link SecretProvider} that reads a secret from a file.
 *
 * @author Carl Harris
 */
public class FileSecretProvider implements SecretProvider {

  static final String PROVIDER_NAME = "FILE";
  static final String PATH = "path";
  static final String ENCODING = "encoding";

  @Override
  public String getName() {
    return PROVIDER_NAME;
  }

  @Override
  public Secret getSecret(Properties properties) throws SecretException {

    final Path path = Optional.ofNullable(properties.getProperty(PATH))
        .map(Paths::get).orElseThrow(() -> new IllegalArgumentException(
            "`" + PATH + "` property is required"));

    final Charset encoding = Optional.ofNullable(properties.getProperty(ENCODING))
        .map(Charset::forName).orElse(StandardCharsets.UTF_8);

    if (!Files.exists(path)) {
      throw new NoSuchSecretException(path + " does not exist");
    }

    try {
      try (final InputStream inputStream = new FileInputStream(path.toFile())) {
        return new ByteArraySecret(InputStreamHelper.toByteArray(inputStream),
            encoding);
      }
    }
    catch (IOException ex) {
      throw new SecretException(ex.getMessage(), ex);
    }
  }

}
