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

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;

/**
 * A {@link ByteArrayLoader} for PEM-encoded content.
 */
class PemContentLoader implements ByteArrayLoader.Strategy {

  @Override
  public byte[] getContent(Path path) throws IOException {
    try (final PemReader reader = new PemReader(
        new InputStreamReader(new FileInputStream(path.toFile()),
            StandardCharsets.UTF_8))) {
      final PemObject pemObject = reader.readPemObject();
      if (pemObject == null) {
        throw new IllegalArgumentException(path + ": not PEM encoded");
      }
      return pemObject.getContent();
    }
  }

}
