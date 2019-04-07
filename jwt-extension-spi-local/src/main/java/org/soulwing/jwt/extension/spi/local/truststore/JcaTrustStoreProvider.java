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
package org.soulwing.jwt.extension.spi.local.truststore;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Optional;
import java.util.Properties;

import org.soulwing.jwt.extension.spi.Secret;
import org.soulwing.jwt.extension.spi.TrustStoreProvider;

/**
 * A {@link TrustStoreProvider} that produces key stores using the JCA.
 *
 * @author Carl Harris
 */
public class JcaTrustStoreProvider implements TrustStoreProvider {

  static final String PROVIDER_NAME = "JCA";

  static final String TYPE_PROPERTY = "type";

  @Override
  public String getName() {
    return PROVIDER_NAME;
  }

  @Override
  public KeyStore getTrustStore(String path, Secret secret,
      Properties properties) throws FileNotFoundException, KeyStoreException {
    try {
      final String type =
          Optional.ofNullable(properties.getProperty(TYPE_PROPERTY))
              .orElseThrow(() -> new KeyStoreException("`type` is required"));

      final KeyStore keyStore = KeyStore.getInstance(type);
      try (final InputStream inputStream = new FileInputStream(path)) {
        keyStore.load(inputStream, Optional.ofNullable(secret)
            .map(Secret::asCharArray).orElse(null));
        return keyStore;
      }
    }
    catch (FileNotFoundException ex) {
      throw ex;
    }
    catch (NoSuchAlgorithmException | CertificateException | IOException ex) {
      throw new KeyStoreException(ex.getMessage(), ex);
    }
  }

}
