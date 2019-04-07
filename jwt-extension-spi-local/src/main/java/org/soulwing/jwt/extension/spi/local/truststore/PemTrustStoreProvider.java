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

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.soulwing.jwt.extension.spi.Secret;
import org.soulwing.jwt.extension.spi.TrustStoreProvider;

/**
 * A {@link TrustStoreProvider} that produces a trust store from a file
 * containing concatenated PEM-encoded certificates.
 *
 * @author Carl Harris
 */
public class PemTrustStoreProvider implements TrustStoreProvider {

  static final String PROVIDER_NAME = "PEM";

  private static final String ALIAS_FORMAT = "alias-%d";

  @Override
  public String getName() {
    return PROVIDER_NAME;
  }

  @Override
  public KeyStore getTrustStore(String path,
      @SuppressWarnings("unused") Secret secret,
      @SuppressWarnings("unused") Properties properties)
      throws FileNotFoundException, KeyStoreException {
    try {
      try (final InputStream inputStream = new FileInputStream(path)) {
        final KeyStore keyStore =
            KeyStore.getInstance(KeyStore.getDefaultType());

        keyStore.load(null, null);

        final KeyStore.PasswordProtection password =
            new KeyStore.PasswordProtection(null);

        int count = 0;
        for (final X509Certificate certificate :
            toCertificates(loadPemObjects(inputStream))) {
          keyStore.setEntry(String.format(ALIAS_FORMAT, count++),
              new KeyStore.TrustedCertificateEntry(certificate), password);
        }

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

  private List<PemObject> loadPemObjects(InputStream inputStream)
      throws IOException {
    final PemReader reader = new PemReader(new InputStreamReader(
        inputStream, StandardCharsets.US_ASCII));
    final List<PemObject> objects = new LinkedList<>();
    PemObject object = reader.readPemObject();
    while (object != null) {
      objects.add(object);
      object = reader.readPemObject();
    }
    return objects;
  }

  private List<X509Certificate> toCertificates(List<PemObject> objects)
      throws CertificateException {
    final List<X509Certificate> certificates = new ArrayList<>();
    final CertificateFactory factory = CertificateFactory.getInstance("X.509");
    for (final PemObject object : objects) {
      final ByteArrayInputStream bos =
          new ByteArrayInputStream(object.getContent());
      certificates.add((X509Certificate) factory.generateCertificate(bos));
    }
    return certificates;
  }

}
