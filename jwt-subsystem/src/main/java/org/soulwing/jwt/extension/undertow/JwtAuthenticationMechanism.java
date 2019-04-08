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
package org.soulwing.jwt.extension.undertow;

import static org.soulwing.jwt.extension.undertow.UndertowLogger.LOGGER;

import java.util.function.Supplier;

import org.soulwing.jwt.extension.service.AuthenticationException;
import org.soulwing.jwt.extension.service.AuthenticationService;
import org.soulwing.jwt.extension.service.Authenticator;
import org.soulwing.jwt.extension.service.Credential;
import io.undertow.security.api.AuthenticationMechanism;
import io.undertow.security.api.SecurityContext;
import io.undertow.security.idm.Account;
import io.undertow.security.idm.IdentityManager;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.StatusCodes;

/**
 * An {@link AuthenticationMechanism} that uses the CAS protocol.
 * 
 * @author Carl Harris
 */
public class JwtAuthenticationMechanism implements AuthenticationMechanism {


  static final String MECHANISM_NAME = "JWT";

  private static final String AUTH_HEADER = "Authorization";

  private static final String BEARER_AUTH_SCHEMA = "Bearer";

  private static final String NOT_AUTHORIZED_MESSAGE =
      "identity manager does not recognize user '%s'";

  private final IdentityManager identityManager;
  private final Supplier<AuthenticationService> authenticationService;
  private final Supplier<AuthenticationChallenge.Builder> challengeBuilder;

  JwtAuthenticationMechanism(IdentityManager identityManager,
      Supplier<AuthenticationService> authenticationService) {
    this(identityManager, authenticationService,
        JsonAuthenticationChallenge::builder);
  }

  private JwtAuthenticationMechanism(
      IdentityManager identityManager,
      Supplier<AuthenticationService> authenticationService,
      Supplier<AuthenticationChallenge.Builder> challengeBuilder) {
    this.identityManager = identityManager;
    this.authenticationService = authenticationService;
    this.challengeBuilder = challengeBuilder;
  }

  @Override
  public AuthenticationMechanismOutcome authenticate(
      HttpServerExchange exchange, SecurityContext securityContext) {

    if (!securityContext.isAuthenticationRequired()) {
      return AuthenticationMechanismOutcome.NOT_ATTEMPTED;
    }

    final String token = getToken(exchange);
    if (token == null) {
      exchange.putAttachment(JwtAttachments.AUTH_MESSAGE_KEY,
          "Bearer token authentication is required");
      securityContext.authenticationFailed("No token present", MECHANISM_NAME);
      return AuthenticationMechanismOutcome.NOT_AUTHENTICATED;
    }

    try {
      final Authenticator authenticator =
          authenticationService.get().newAuthenticator();

      exchange.putAttachment(JwtAttachments.AUTHENTICATOR_KEY, authenticator);
      final Credential credential = authenticator.validate(token);
      final Account account = authorize(credential);

      exchange.putAttachment(JwtAttachments.CREDENTIAL_KEY, credential);

      securityContext.authenticationComplete(account, MECHANISM_NAME, true);
      return AuthenticationMechanismOutcome.AUTHENTICATED;
    }
    catch (AuthorizationException ex) {
      exchange.putAttachment(JwtAttachments.AUTH_FAILED_KEY,
          StatusCodes.FORBIDDEN);
      securityContext.authenticationFailed(ex.getMessage(), MECHANISM_NAME);
      return AuthenticationMechanismOutcome.NOT_AUTHENTICATED;
    }
    catch (AuthenticationException ex) {
      exchange.putAttachment(JwtAttachments.AUTH_MESSAGE_KEY,
          ex.getMessage());
      securityContext.setAuthenticationRequired();
      return AuthenticationMechanismOutcome.NOT_AUTHENTICATED;
    }
    catch (Exception ex) {
      LOGGER.error(ex.getMessage(), ex);
      return AuthenticationMechanismOutcome.NOT_AUTHENTICATED;
    }
  }

  private String getToken(HttpServerExchange exchange) {
    final String header = exchange.getRequestHeaders().getFirst(AUTH_HEADER);
    if (header == null) return null;
    if (!header.startsWith(BEARER_AUTH_SCHEMA + " ")) return null;
    return header.substring(BEARER_AUTH_SCHEMA.length()).trim();
  }

  @Override
  public ChallengeResult sendChallenge(HttpServerExchange exchange,
      SecurityContext context) {

    final Integer failedStatus =
        exchange.getAttachment(JwtAttachments.AUTH_FAILED_KEY);
    if (failedStatus != null) {
      exchange.removeAttachment(JwtAttachments.AUTH_FAILED_KEY);
      return new ChallengeResult(false, failedStatus);
    }

    final int status = StatusCodes.UNAUTHORIZED;

    challengeBuilder.get()
        .statusCode(status)
        .issuerUrl(authenticationService.get().getIssuerUrl())
        .message(exchange.getAttachment(JwtAttachments.AUTH_MESSAGE_KEY))
        .build()
        .send(exchange);

    exchange.removeAttachment(JwtAttachments.AUTH_MESSAGE_KEY);

    return new ChallengeResult(true, status);
  }

  /**
   * Authorizes the user associated with the given assertion credential via
   * the container's identity manager.
   * @param credential the subject user credential
   * @return authorized user's account object
   * @throws AuthorizationException if the user is not authorized
   */
  private Account authorize(Credential credential) throws AuthorizationException {

    String name = credential.getPrincipal().getName();

    Account account = identityManager.verify(name, credential);
    
    if (account == null) {
      String message = String.format(NOT_AUTHORIZED_MESSAGE, name);
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug(message);
      }
      throw new AuthorizationException(message);
    }

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("authorization successful:"
          + " user=" + account.getPrincipal().getName()
          + " roles=" + account.getRoles());
    }

    return account;
  }

}
