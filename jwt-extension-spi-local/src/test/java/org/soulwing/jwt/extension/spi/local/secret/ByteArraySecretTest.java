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
package org.soulwing.jwt.extension.spi.local.secret;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for {@link ByteArraySecret}.
 *
 * @author Carl Harris
 */
public class ByteArraySecretTest {

  private static final String SECRET = "secret";
  private static final Charset CHARSET = StandardCharsets.UTF_8;

  private ByteArraySecret secret;

  @Before
  public void setUp() throws Exception {
    secret = new ByteArraySecret(SECRET.getBytes(CHARSET), CHARSET);
  }

  @Test
  public void testAsString() throws Exception {
    assertThat(secret.asString(), is(equalTo(SECRET)));
  }

  @Test
  public void testAsCharArray() throws Exception {
    assertThat(secret.asCharArray(), is(equalTo(SECRET.toCharArray())));
  }

  @Test
  public void testAsByteArray() throws Exception {
    assertThat(secret.asByteArray(), is(equalTo(SECRET.getBytes(CHARSET))));
  }

  @Test(expected = IllegalStateException.class)
  public void testAsStringWhenDestroyed() throws Exception {
    secret.destroy();
    assertThat(secret.isDestroyed(), is(true));
    secret.asString();
  }

  @Test(expected = IllegalStateException.class)
  public void testAsCharArrayWhenDestroyed() throws Exception {
    secret.destroy();
    assertThat(secret.isDestroyed(), is(true));
    secret.asCharArray();
  }

  @Test(expected = IllegalStateException.class)
  public void testAsByteArrayWhenDestroyed() throws Exception {
    secret.destroy();
    assertThat(secret.isDestroyed(), is(true));
    secret.asByteArray();
  }

}