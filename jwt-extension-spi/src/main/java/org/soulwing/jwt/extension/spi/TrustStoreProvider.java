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

import java.io.FileNotFoundException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.util.Properties;


/**
 * A provider of {@link KeyStore} instances containing trusted CA certificates.
 *
 * @author Carl Harris
 */
public interface TrustStoreProvider extends ServiceProvider {

  /**
   * Gets a trust store.
   * @param path path to the trust store data
   * @param secret optional secret for trust stores that are password protected
   *    (may be {@code null})
   * @param properties configuration properties for the provider
   * @return trust store
   * @throws FileNotFoundException if there is no trust store at the specified
   *    path
   * @throws KeyStoreException if an error occurs in loading the trust store
   */
  KeyStore getTrustStore(String path, Secret secret, Properties properties)
      throws FileNotFoundException, KeyStoreException;

}
