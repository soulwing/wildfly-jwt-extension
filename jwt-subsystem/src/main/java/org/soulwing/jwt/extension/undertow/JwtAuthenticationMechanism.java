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

import java.util.Optional;
import java.util.function.Supplier;

import org.soulwing.jwt.extension.service.AuthenticationException;
import org.soulwing.jwt.extension.service.AuthenticationService;
import org.soulwing.jwt.extension.service.Authenticator;
import org.soulwing.jwt.extension.service.Credential;
import io.undertow.security.api.AuthenticationMechanism;
import io.undertow.security.api.SecurityContext;
import io.undertow.security.idm.Account;
import io.undertow.server.HttpServerExchange;

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

  private final Supplier<AuthenticationService> authenticationService;
  
  JwtAuthenticationMechanism(
      Supplier<AuthenticationService> authenticationService) {
    this.authenticationService = authenticationService;
  }

  @Override
  public AuthenticationMechanismOutcome authenticate(
      HttpServerExchange exchange, SecurityContext securityContext) {

    if (!securityContext.isAuthenticationRequired()) {
      return AuthenticationMechanismOutcome.NOT_ATTEMPTED;
    }

    final Optional<String> token = getToken(exchange);
    if (!token.isPresent()) {
      exchange.putAttachment(JwtAttachments.AUTH_FAILED_KEY, 401);
      securityContext.authenticationFailed("No token present", MECHANISM_NAME);
      return AuthenticationMechanismOutcome.NOT_AUTHENTICATED;
    }

    try {
      final Authenticator authenticator =
          authenticationService.get().newAuthenticator();

      exchange.putAttachment(JwtAttachments.AUTHENTICATOR_KEY, authenticator);
      final Credential credential = authenticator.validate(token.get());
      final Account account = authorize(credential, securityContext);

      exchange.putAttachment(JwtAttachments.CREDENTIAL_KEY, credential);

      securityContext.authenticationComplete(account, MECHANISM_NAME, true);
      return AuthenticationMechanismOutcome.AUTHENTICATED;
    }
    catch (AuthorizationException ex) {
      exchange.putAttachment(JwtAttachments.AUTH_FAILED_KEY, 403);
      securityContext.authenticationFailed(ex.getMessage(), MECHANISM_NAME);
      return AuthenticationMechanismOutcome.NOT_AUTHENTICATED;
    }
    catch (AuthenticationException ex) {
      securityContext.setAuthenticationRequired();
      return AuthenticationMechanismOutcome.NOT_AUTHENTICATED;
    }
    catch (Exception ex) {
      LOGGER.error(ex.getMessage(), ex);
      return AuthenticationMechanismOutcome.NOT_AUTHENTICATED;
    }
  }

  private Optional<String> getToken(HttpServerExchange exchange) {
    final String header = exchange.getRequestHeaders().getFirst(AUTH_HEADER);
    if (header == null) return Optional.empty();
    if (!header.startsWith(BEARER_AUTH_SCHEMA + " ")) return Optional.empty();
    return Optional.of(header.substring(BEARER_AUTH_SCHEMA.length()).trim());
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
        
    return new ChallengeResult(true, 401);
  }

  /**
   * Authorizes the user associated with the given assertion credential via
   * the container's identity manager.
   * @param credential the subject user credential
   * @param securityContext security context
   * @return authorized user's account object
   * @throws AuthorizationException if the user is not authorized
   */
  private Account authorize(Credential credential,
      SecurityContext securityContext) throws AuthorizationException {

    String name = credential.getPrincipal().getName();

    Account account = securityContext.getIdentityManager().verify(
        name, credential);
    
    if (account == null) {
      String message = String.format(NOT_AUTHORIZED_MESSAGE, name);
      LOGGER.info(message);
      throw new AuthorizationException(message);
    }

    LOGGER.info("authorization successful: "
        + " user=" + account.getPrincipal().getName()
        + " roles=" + account.getRoles());

    return account;
  }

}
