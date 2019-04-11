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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Function;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.soulwing.jwt.extension.spi.NoSuchSecretKeyException;
import org.soulwing.jwt.extension.spi.SecretException;
import org.soulwing.jwt.extension.spi.SecretKeyProvider;

/**
 * A {@link SecretKeyProvider} that reads a secret key whose byte-encoded from
 * has been stored in a file.
 *
 * @author Carl Harris
 */
public class FileSecretKeyProvider implements SecretKeyProvider {

  enum Encoding {
    RAW,
    BASE64;
  }

  private static final Map<Encoding, Function<byte[], byte[]>> DECODERS =
      new HashMap<>();

  static {
    DECODERS.put(Encoding.RAW, b -> b);
    DECODERS.put(Encoding.BASE64, b -> Base64.getDecoder().decode(b));
  }

  static final String PROVIDER_NAME = "FILE";
  static final String PATH = "path";
  static final String ENCODING = "encoding";

  @Override
  public String getName() {
    return PROVIDER_NAME;
  }

  @Override
  public SecretKey getSecretKey(String type, int length, Properties properties)
      throws SecretException {

    final Path path = Optional.ofNullable(properties.getProperty(PATH))
        .map(Paths::get).orElseThrow(() -> new IllegalArgumentException(
            "`" + PATH + "` property is required"));

    final Encoding encoding = Optional.ofNullable(properties.getProperty(ENCODING))
        .map(String::toUpperCase)
        .map(Encoding::valueOf)
        .orElse(Encoding.RAW);

    if (!Files.exists(path)) {
      throw new NoSuchSecretKeyException(path + " does not exist");
    }

    try {
      try (final InputStream inputStream = new FileInputStream(path.toFile())) {
        final byte[] bytes = DECODERS.get(encoding).apply(
            InputStreamHelper.toByteArray(inputStream));

        if (bytes.length*Byte.SIZE < length) {
          throw new SecretException("file is smaller than specified bit length");
        }
        return new SecretKeySpec(bytes, type);
      }
    }
    catch (IOException ex) {
      throw new SecretException(ex.getMessage(), ex);
    }
  }

}
