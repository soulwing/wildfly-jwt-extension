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

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import org.soulwing.jwt.api.locator.PemCertificateChainLoader;

/**
 * A certificate chain loader that prepends a base URL to the certificate
 * chain URL when needed.
 *
 * @author Carl Harris
 */
public class CertificateChainLoader extends PemCertificateChainLoader {

  private final URI baseUrl;

  public CertificateChainLoader(URI baseUrl) {
    this.baseUrl = baseUrl;
  }

  @Override
  protected InputStream openStream(URI url) throws IOException {
    if (url.getScheme() == null && url.getAuthority() == null) {
      final StringBuilder sb = new StringBuilder();
      if (baseUrl.getScheme() != null) {
        sb.append(baseUrl.getScheme()).append(":");
      }
      if (baseUrl.getAuthority() != null) {
        sb.append("//").append(baseUrl.getAuthority());
      }

      final String basePath = baseUrl.getPath();
      if (basePath != null) {
        if (basePath.endsWith("/")) {
          sb.append(basePath, 0, basePath.length() - 1);
        }
        else {
          sb.append(basePath);
        }
      }

      if (!url.getPath().startsWith("/")) {
        sb.append("/");
      }

      sb.append(url.getPath());
      url = URI.create(sb.toString());
    }
    return url.toURL().openStream();
  }
}
