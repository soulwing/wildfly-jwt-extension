package org.soulwing.jwt.extension.undertow;

import java.net.URI;

import io.undertow.server.HttpServerExchange;

/**
 * An authentication challenge.
 *
 * @author Carl Harris
 */
interface AuthenticationChallenge {

  /**
   * A builder that produces an authentication challenge.
   */
  interface Builder {

    /**
     * Specifies the status code.
     * @param statusCode status code
     * @return this builder
     */
    Builder statusCode(int statusCode);

    /**
     * Specifies the message for the challenge.
     * @param message message
     * @return this builder
     */
    Builder message(String message);

    /**
     * Specifies the token issuer URL.
     * @param issuerUrl token issuer URL
     * @return this builder
     */
    Builder issuerUrl(URI issuerUrl);

    /**
     * Builds the challenge.
     * @return challenge
     */
    AuthenticationChallenge build();

  }

  /**
   * Sends this challenge via the specified exchange.
   * @param exchange
   */
  void send(HttpServerExchange exchange);

}
