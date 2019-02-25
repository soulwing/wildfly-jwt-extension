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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

/**
 * A loader for a public key resources.
 *
 * @author Carl Harris
 */
public class PublicKeyLoader {

  /**
   * Loads a public key from a file.
   * @param path path to the file to load
   * @param format file format
   * @param type type of key to expect in the file
   * @return public key
   * @throws IllegalArgumentException if the format or type are unsupported
   *    by the runtime platform, or if the specified path does not exist or
   *    is not readable
   * @throws InvalidKeySpecException if the content of the file cannot be used to
   *    reproduce a key of the specified type
   * @throws IOException if the content of the file cannot be successfully
   *    read
   */
  public static PublicKey loadKey(Path path,
      PublicKeyFormat format, PublicKeyType type)
      throws IllegalArgumentException, IOException, InvalidKeySpecException {

    try {
      final X509EncodedKeySpec spec = new X509EncodedKeySpec(
          ByteArrayLoader.getContent(path, format));
      final KeyFactory keyFactory = KeyFactory.getInstance(type.name());
      return keyFactory.generatePublic(spec);
    }
    catch (NoSuchAlgorithmException ex) {
      throw new IllegalArgumentException(type + ": unsupported key type");
    }
  }

  public static PublicKey loadKeyResource(String resourceName,
      PublicKeyFormat format, PublicKeyType type,
      ClassLoader classLoader) throws IllegalArgumentException, IOException,
      InvalidKeySpecException, URISyntaxException {
    final URL url = classLoader.getResource(resourceName);
    if (url == null) {
      throw new FileNotFoundException(resourceName);
    }
    return PublicKeyLoader.loadKey(Paths.get(url.toURI()),
        format, type);
  }
}
