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

import java.nio.charset.StandardCharsets;
import java.util.Properties;

import org.soulwing.jwt.extension.spi.Secret;
import org.soulwing.jwt.extension.spi.SecretException;
import org.soulwing.jwt.extension.spi.SecretProvider;

/**
 * A provider of secret strings stored in AWS Secrets Manager.
 *
 * @author Carl Harris
 */
public class AwsSecretProvider extends AbstractAwsSecretProvider
    implements SecretProvider  {

  @Override
  public Secret getSecret(Properties properties) throws SecretException {
    return new ByteArraySecret(retrieveSecret(properties), StandardCharsets.UTF_8);
  }

}
