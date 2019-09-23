/*
 * File created on Sep 23, 2019
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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assume.assumeTrue;

import java.io.StringWriter;
import java.util.Properties;
import java.util.UUID;
import javax.json.Json;
import javax.json.JsonObject;

import org.junit.Test;
import org.soulwing.jwt.extension.spi.Secret;
import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.AWSSecretsManagerClientBuilder;
import com.amazonaws.services.secretsmanager.model.CreateSecretRequest;
import com.amazonaws.services.secretsmanager.model.DeleteSecretRequest;

/**
 * Unit tests for {@link AwsSecretProvider}.
 *
 * @author Carl Harris
 */
public class AwsSecretProviderTest {

  private static final String SECRET_ID = "test/" + UUID.randomUUID().toString();

  private static final String SECRET = "this is a secret";

  private AwsSecretProvider provider = new AwsSecretProvider();

  @Test
  public void testGetSecret() throws Exception {
    assumeTrue("AWS profile is unavailable",
        (System.getProperty("aws.profile") != null
            || System.getenv("AWS_PROFILE") != null));

    final AWSSecretsManager secretsManager =
        AWSSecretsManagerClientBuilder.standard().build();

    final JsonObject object = Json.createObjectBuilder()
        .add("data", SECRET).build();

    final StringWriter writer = new StringWriter();
    Json.createWriter(writer).writeObject(object);
    final String data = writer.toString();

    final CreateSecretRequest createRequest = new CreateSecretRequest();
    createRequest.setName(SECRET_ID);
    createRequest.setSecretString(data);

    secretsManager.createSecret(createRequest);

    final Properties properties = new Properties();
    properties.setProperty(AwsSecretProvider.SECRET_ID, SECRET_ID);
    final Secret secret = provider.getSecret(properties);
    assertThat(secret.asString(), is(equalTo(SECRET)));

    final DeleteSecretRequest deleteRequest = new DeleteSecretRequest();
    deleteRequest.setSecretId(SECRET_ID);
    secretsManager.deleteSecret(deleteRequest);

  }

}