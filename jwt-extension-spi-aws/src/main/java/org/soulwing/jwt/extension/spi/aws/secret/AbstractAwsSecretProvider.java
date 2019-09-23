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

import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.function.Function;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

import org.soulwing.jwt.extension.spi.NoSuchSecretException;
import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.AWSSecretsManagerClientBuilder;
import com.amazonaws.services.secretsmanager.model.GetSecretValueRequest;
import com.amazonaws.services.secretsmanager.model.GetSecretValueResult;

/**
 * An abstract base for providers that work with secrets stored in AWS Secrets
 * Manager.
 *
 * @author Carl Harris
 */
abstract class AbstractAwsSecretProvider {

  static final String PROVIDER_NAME = "AWS";

  static final String SECRET_ID = "secret-id";

  static final String DATA_KEY = "data";
  static final String ENCODING_KEY = "encoding";

  enum Encoding {
    UTF8,
    BASE64
  }

  private static final Map<Encoding, Function<String, byte[]>> DECODERS =
      new HashMap<>();

  static {
    DECODERS.put(Encoding.UTF8, b -> b.getBytes(StandardCharsets.UTF_8));
    DECODERS.put(Encoding.BASE64, b -> Base64.getDecoder().decode(b));
  }

  public String getName() {
    return PROVIDER_NAME;
  }

  byte[] retrieveSecret(Properties properties) throws NoSuchSecretException {
    final AWSSecretsManager secretsManager =
        AWSSecretsManagerClientBuilder.defaultClient();

    final GetSecretValueRequest request = new GetSecretValueRequest();
    request.setSecretId(properties.getProperty(SECRET_ID));
    final GetSecretValueResult result = secretsManager.getSecretValue(request);
    try (final JsonReader reader = Json.createReader(
          new StringReader(result.getSecretString()))) {
      final JsonObject secret = reader.readObject();
      final Encoding encoding =
          Encoding.valueOf(secret.getString(ENCODING_KEY, Encoding.UTF8.name()));
      byte[] bytes = DECODERS.get(encoding).apply(secret.getString(DATA_KEY));
      return bytes;
    }
  }


}
