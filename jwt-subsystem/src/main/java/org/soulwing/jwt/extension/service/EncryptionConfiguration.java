/*
 * File created on Apr 3, 2019
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
package org.soulwing.jwt.extension.service;

import java.util.List;

import org.soulwing.jwt.api.JWE;
import org.soulwing.s2ks.KeyPairStorage;

/**
 *  * A configuration for decrypting JWE payloads of bearer tokens.
 * @author Carl Harris
 */
public interface EncryptionConfiguration {

  /**
   * Gets the key management algorithm.
   * @return algorithm ID or {@code null} if the algorithm specified in the
   *    JOSE header is to be used
   */
  JWE.KeyManagementAlgorithm getKeyManagementAlgorithm();

  /**
   * Gets the content encryption algorithm.
   * @return algorithm ID or {@code null} if the algorithm specified in the
   *    JOSE header is to be used
   */
  JWE.ContentEncryptionAlgorithm getContentEncryptionAlgorithm();

  /**
   * Gets the compression algorithm.
   * @return algorithm ID or {@code null} if the algorithm specified in the
   *    JOSE header is to be used
   */
  JWE.CompressionAlgorithm getCompressionAlgorithm();

  /**
   * Gets a key pair storage instance to be used to retrieve private keys
   * needed for asymmetric decryption operations.
   * @return key pair storage instance or {@code null} if asymmetric decryption
   *    is not needed
   */
  KeyPairStorage getKeyPairStorage();

  /**
   * Gets a collection of secret keys to be used for symmetric decryption
   * operations.
   * @return list of secret keys or {@code null} if symmetric decryption is
   *    not needed
   */
  List<SecretKeyConfiguration> getSecretKeys();

}
