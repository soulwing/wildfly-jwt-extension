/*
 * File created on Apr 8, 2019
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
package org.soulwing.jwt.extension.undertow;

import java.io.StringWriter;
import java.net.URI;
import java.util.Collections;
import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.json.stream.JsonGenerator;

import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.StatusCodes;

/**
 * A JSON authentication challenge.
 *
 * @author Carl Harris
 */
class JsonAuthenticationChallenge implements AuthenticationChallenge {

  private static final String APPLICATION_JSON = "application/json";

  private static final String ISSUER_KEY = "issuer";
  private static final String MESSAGE_KEY = "message";

  private int statusCode = StatusCodes.UNAUTHORIZED;
  private String message;
  private URI issuerUrl;

  private JsonAuthenticationChallenge() {}

  public static class Builder implements AuthenticationChallenge.Builder {

    private final JsonAuthenticationChallenge response =
        new JsonAuthenticationChallenge();

    private Builder() {}

    @Override
    public AuthenticationChallenge.Builder statusCode(int statusCode) {
      response.statusCode = statusCode;
      return this;
    }

    @Override
    public Builder message(String message) {
      response.message = message;
      return this;
    }

    @Override
    public Builder issuerUrl(URI issuerUrl) {
      response.issuerUrl = issuerUrl;
      return this;
    }

    @Override
    public AuthenticationChallenge build() {
      return response;
    }
  }

  static Builder builder() {
    return new Builder();
  }

  @Override
  public void send(HttpServerExchange exchange) {
    exchange.setStatusCode(statusCode);
    exchange.setReasonPhrase(StatusCodes.getReason(statusCode));
    exchange.getResponseHeaders()
        .addFirst(Headers.CONTENT_TYPE, APPLICATION_JSON);
    exchange.getResponseSender().send(body());
  }

  private String body() {
    final JsonObjectBuilder json = Json.createObjectBuilder();
    if (issuerUrl != null) {
      json.add(ISSUER_KEY, issuerUrl.toString());
    }
    if (message != null) {
      json.add(MESSAGE_KEY, message);
    }

    final StringWriter writer = new StringWriter();
    Json.createWriterFactory(
        Collections.singletonMap(JsonGenerator.PRETTY_PRINTING, true))
        .createWriter(writer)
        .writeObject(json.build());

    return writer.toString().trim();
  }

}
