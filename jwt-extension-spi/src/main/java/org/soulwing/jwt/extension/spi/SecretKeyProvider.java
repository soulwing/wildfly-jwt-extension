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
package org.soulwing.jwt.extension.spi;

import java.util.Properties;
import javax.crypto.SecretKey;

/**
 * A provider of {@link SecretKey} objects.
 *
 * @author Carl Harris
 */
public interface SecretKeyProvider extends ServiceProvider {

  /**
   * Retrieves a secret key.
   * @param type key type
   * @param length key length in bits
   * @param properties properties describing the secret key to be retrieved.
   * @return secret key instance
   * @throws NoSuchSecretKeyException if the specified secret key does not exist
   * @throws SecretException if an exception occurs in retrieving the secret key
   */
  SecretKey getSecretKey(String type, int length, Properties properties);

}
