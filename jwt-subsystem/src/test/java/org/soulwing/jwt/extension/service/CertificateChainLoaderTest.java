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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assume.assumeFalse;

import java.io.FileNotFoundException;
import java.net.URI;
import java.net.URL;

import org.junit.Test;

/**
 * Unit tests for {@link CertificateChainLoader}.
 *
 * @author Carl Harris
 */
public class CertificateChainLoaderTest {

  private static final String HTTP_SKIP_PROPERTY =
      CertificateChainLoaderTest.class.getSimpleName() + ".skipHttp";

  private static final boolean skipHttp;

  static {
    String skip = System.getProperty(HTTP_SKIP_PROPERTY);
    skipHttp = skip != null && (skip.isEmpty() || Boolean.parseBoolean(skip));
  }

  private static final URI HTTP_URI =
      URI.create("https://www.amazontrust.com/repository/AmazonRootCA1.pem");

  @Test
  public void testLoadFromRelativeFileUrl() throws Exception {
    final URI baseUrl = getResourceUri("");
    assertThat(baseUrl.getPath(), endsWith("/"));
    assertThat(baseUrl.getAuthority(), is(nullValue()));

    final String s = baseUrl.toString();
    final URI baseUrlWithoutTrailingSlash
        = URI.create(s.substring(0, s.length() - 1));

    assertThat(new CertificateChainLoader(baseUrl).load(URI.create("cert.pem")),
        is(not(empty())));

    assertThat(new CertificateChainLoader(
            baseUrlWithoutTrailingSlash).load(URI.create("cert.pem")),
        is(not(empty())));

    assertThat(new CertificateChainLoader(
            baseUrlWithoutTrailingSlash).load(URI.create("/cert.pem")),
        is(not(empty())));
  }

  @Test
  public void testLoadFromAbsoluteFileUrl() throws Exception {
    assertThat(new CertificateChainLoader(URI.create("notUsed"))
        .load(getResourceUri("cert.pem")), is(not(empty())));
  }

  private URI getResourceUri(String name) throws Exception {
    final URL resource = getClass().getClassLoader().getResource(name);
    if (resource == null) throw new FileNotFoundException();
    return resource.toURI();
  }

  @Test
  public void testLoadFromRelativeHttpUrl() throws Exception {
    assumeFalse("HTTP tests skipped", skipHttp);

    final String s = HTTP_URI.toString();
    final int i = s.lastIndexOf("/");

    final URI baseUrl = URI.create(s.substring(0, i));
    final URI certUrl = URI.create(s.substring(i));

    assertThat(new CertificateChainLoader(baseUrl)
        .load(certUrl), is(not(empty())));
  }

  @Test
  public void testLoadFromAbsoluteHttpUrl() throws Exception {
    assumeFalse("HTTP tests skipped", skipHttp);
    assertThat(new CertificateChainLoader(URI.create("notused"))
        .load(HTTP_URI), is(not(empty())));
  }

}