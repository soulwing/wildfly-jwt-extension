/*
 * File created on Sep 19, 2019
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
package org.soulwing.jwt.extension.spi.aws.secret;

import java.util.Properties;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.soulwing.jwt.extension.spi.SecretException;
import org.soulwing.jwt.extension.spi.SecretKeyProvider;

/**
 *
 * @author Carl Harris
 */
public class AwsSecretKeyProvider extends AbstractAwsSecretProvider
    implements SecretKeyProvider {

  @Override
  public SecretKey getSecretKey(String type, int length, Properties properties) {
    final byte[] secret = retrieveSecret(properties);
    if (secret.length*Byte.SIZE < length) {
      throw new SecretException("data is smaller than specified bit length");
    }
    return new SecretKeySpec(secret, type);
  }

}
