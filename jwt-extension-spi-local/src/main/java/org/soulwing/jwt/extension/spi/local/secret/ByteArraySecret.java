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

import java.io.ByteArrayInputStream;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.security.SecureRandom;

import org.soulwing.jwt.extension.spi.Secret;

/**
 * A {@link Secret} backed by a byte array.
 *
 * @author Carl Harris
 */
class ByteArraySecret implements Secret {

  private final SecureRandom random = new SecureRandom();

  private final Charset encoding;

  private byte[] secret;

  ByteArraySecret(byte[] secret, Charset encoding) {
    if (secret == null) {
      throw new IllegalArgumentException("secret is required");
    }
    if (encoding == null) {
      throw new IllegalArgumentException("encoding is required");
    }
    this.secret = secret;
    this.encoding = encoding;
  }

  @Override
  public String asString() {
    if (isDestroyed()) throw new IllegalStateException("secret is destroyed");
    return new String(secret, encoding);
  }

  @Override
  public char[] asCharArray() {
    if (isDestroyed()) throw new IllegalStateException("secret is destroyed");
    try {
      final Reader reader = new InputStreamReader(
          new ByteArrayInputStream(secret), encoding);
      final CharArrayWriter writer = new CharArrayWriter();
      int c = reader.read();
      while (c != -1) {
        writer.write(c);
        c = reader.read();
      }
      writer.flush();
      return writer.toCharArray();
    }
    catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }

  @Override
  public byte[] asByteArray() {
    if (isDestroyed()) throw new IllegalStateException("secret is destroyed");
    return secret;
  }

  @Override
  public boolean isDestroyed() {
    return secret == null;
  }

  @Override
  public void destroy() {
    random.nextBytes(secret);
    secret = null;
  }

}
