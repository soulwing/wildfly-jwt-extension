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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * A strategy for loading the content of a file as an array of bytes.
 */
class ByteArrayLoader {

  interface Strategy {
    byte[] getContent(Path path) throws IOException;
  }

  private static final Map<PublicKeyFormat, Strategy> strategies =
      new HashMap<>();

  static {
    strategies.put(PublicKeyFormat.PEM, new PemContentLoader());
    strategies.put(PublicKeyFormat.DER, new DerContentLoader());
  }

  private ByteArrayLoader() {
  }

  /**
   * Loads a public key from a file.
   * @param path path to the file to load
   * @param format file format
   * @return public key
   * @throws IllegalArgumentException if the format is unsupported
   *    by the runtime platform, or if the specified path does not exist or
   *    is not readable
   */
  static byte[] getContent(Path path, PublicKeyFormat format)
      throws IOException, IllegalArgumentException {
    if (Files.notExists(path)) {
      throw new IllegalArgumentException(path + ": not found");
    }
    if (!Files.isReadable(path)) {
      throw new IllegalArgumentException(path + ": not readable");
    }

    final ByteArrayLoader.Strategy strategy = strategies.get(format);
    if (strategy == null) {
      throw new IllegalArgumentException(format + ": unsupported key format");
    }

    return strategy.getContent(path);
  }

}
