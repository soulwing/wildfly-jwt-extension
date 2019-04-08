/*
 * File created on Apr 8, 2019
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
package org.soulwing.jwt.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

/**
 * A static utility method for retrieving a certificate resource.
 *
 * @author Carl Harris
 */
class CertificateUtil {

  static X509Certificate loadCertificate(String name)
      throws CertificateException, IOException {
    final URL location = CertificateUtil.class.getClassLoader()
        .getResource(name);
    if (location == null) throw new FileNotFoundException(name);
    return loadCertificate(location);
  }

  static X509Certificate loadCertificate(URL location)
      throws CertificateException, IOException {
    try (final InputStream inputStream = location.openStream()) {
      return (X509Certificate) CertificateFactory.getInstance("X.509")
          .generateCertificate(inputStream);
    }
  }

}
