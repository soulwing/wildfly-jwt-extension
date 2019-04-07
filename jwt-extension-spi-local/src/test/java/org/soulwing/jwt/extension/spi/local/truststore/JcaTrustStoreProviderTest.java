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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Properties;

import org.junit.Test;
import org.soulwing.jwt.extension.spi.ClassLoaderServiceLocator;
import org.soulwing.jwt.extension.spi.TrustStoreProvider;
import org.soulwing.jwt.extension.spi.local.ResourceHelper;
import org.soulwing.jwt.extension.spi.local.StringSecret;

/**
 * Unit tests for {@link JcaTrustStoreProvider}.
 *
 * @author Carl Harris
 */
public class JcaTrustStoreProviderTest {

  @Test
  public void testGetTrustStore() throws Exception {
    final TrustStoreProvider provider = ClassLoaderServiceLocator.INSTANCE
        .locate(TrustStoreProvider.class,
            JcaTrustStoreProvider.PROVIDER_NAME, null);

    final Properties properties = new Properties();
    properties.setProperty(JcaTrustStoreProvider.TYPE_PROPERTY, "JKS");
    final KeyStore trustStore = provider.getTrustStore(
        ResourceHelper.getResource("truststore.jks").getPath(),
        new StringSecret("changeit"), properties);

    final ArrayList<String> aliases = Collections.list(trustStore.aliases());
    assertThat(aliases.isEmpty(), is(false));

    final Certificate certificate = trustStore.getCertificate(aliases.get(0));
    assertThat(certificate, is(instanceOf(X509Certificate.class)));
  }


}
