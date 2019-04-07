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
package org.soulwing.jwt.extension.spi.local;

import org.soulwing.jwt.extension.spi.Secret;

/**
 * A {@link Secret} backed by a string.
 *
 * @author Carl Harris
 */
public class StringSecret implements Secret {

  private String secret;

  public StringSecret(String secret) {
    if (secret == null) {
      throw new IllegalArgumentException("secret is required");
    }
    this.secret = secret;
  }

  @Override
  public String asString() {
    return secret;
  }

  @Override
  public char[] asCharArray() {
    return secret.toCharArray();
  }

  @Override
  public byte[] asByteArray() {
    return secret.getBytes();
  }

  @Override
  public boolean isDestroyed() {
    return secret == null;
  }

  @Override
  public void destroy() {
    secret = null;
  }

}
