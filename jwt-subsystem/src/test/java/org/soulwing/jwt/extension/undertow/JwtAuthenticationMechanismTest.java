/*
 * File created on Apr 3, 2019
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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.net.URI;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collections;

import io.undertow.security.api.AuthenticationMechanism;
import io.undertow.security.api.SecurityContext;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.soulwing.jwt.extension.api.UserPrincipal;
import org.soulwing.jwt.extension.service.AuthenticationException;
import org.soulwing.jwt.extension.service.AuthenticationService;
import org.soulwing.jwt.extension.service.Authenticator;
import org.soulwing.jwt.extension.service.Credential;

import io.undertow.security.idm.Account;
import io.undertow.security.idm.IdentityManager;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.HttpString;
import io.undertow.util.StatusCodes;

/**
 * Unit tests for {@link JwtAuthenticationMechanism}
 *
 * @author Michael Irwin
 */
public class JwtAuthenticationMechanismTest {

  private static final String PRINCIPAL_NAME = "test-user";
  private static final String TOKEN = "sample-access-token";
  private static final URI ISSUER_URL = URI.create("http://oauth.example.com");
  @Rule
  public final JUnitRuleMockery context = new JUnitRuleMockery();

  @Mock
  private IdentityManager identityManager;

  @Mock
  private AuthenticationService authenticationService;

  @Mock
  private Authenticator authenticator;

  @Mock
  private SecurityContext securityContext;

  @Mock
  private Credential credential;

  @Mock
  private UserPrincipal userPrincipal;

  @Mock
  private Account account;

  private HttpServerExchange exchange;

  private JwtAuthenticationMechanism authMechanism;

  @Before
  public void setUp() {
    authMechanism = new JwtAuthenticationMechanism(
        identityManager,
        () -> authenticationService
    );

    exchange = new HttpServerExchange(null);
  }

  @Test
  public void testAuthenticateWhenAuthNotRequired() {
    context.checking(new Expectations() { {
      allowing(securityContext).isAuthenticationRequired();
      will(returnValue(false));
    } });

    assertThat(
        authMechanism.authenticate(exchange, securityContext),
        is(equalTo(
            AuthenticationMechanism.AuthenticationMechanismOutcome.NOT_ATTEMPTED
        ))
    );
  }

  @Test
  public void testAuthenticateWhenNoTokenSpecified() {
    context.checking(new Expectations() { {
      allowing(securityContext).isAuthenticationRequired();
      will(returnValue(true));

      oneOf(securityContext).authenticationFailed("No token present", "JWT");
    } });

    assertThat(
        authMechanism.authenticate(exchange, securityContext),
        is(equalTo(
            AuthenticationMechanism.AuthenticationMechanismOutcome.NOT_AUTHENTICATED
        ))
    );
  }

  @Test
  public void testAuthenticateWhenTokenAppearsInAuthHeader() throws Exception {
    exchange.getRequestHeaders().put(HttpString.tryFromString("Authorization"), "Bearer " + TOKEN);
    context.checking(authExpectations(credential, account));
    context.checking(new Expectations() { {
      oneOf(securityContext).authenticationComplete(account, "JWT", true);
    } });

    final AuthenticationMechanism.AuthenticationMechanismOutcome outcome =
        authMechanism.authenticate(exchange, securityContext);

    assertThat(outcome, is(equalTo(
        AuthenticationMechanism.AuthenticationMechanismOutcome.AUTHENTICATED
    )));
    assertThat(exchange.getAttachment(JwtAttachments.AUTHENTICATOR_KEY), is(equalTo(authenticator)));
    assertThat(exchange.getAttachment(JwtAttachments.CREDENTIAL_KEY), is(equalTo(credential)));
  }

  @Test
  public void testAuthenticateWhenTokenInQueryParam() throws Exception {
    exchange.getQueryParameters().put("access_token", new ArrayDeque<>(Collections.singleton(TOKEN)));
    context.checking(authExpectations(credential, account));
    context.checking(new Expectations() { {
      oneOf(securityContext).authenticationComplete(account, "JWT", true);
    } });

    final AuthenticationMechanism.AuthenticationMechanismOutcome outcome =
        authMechanism.authenticate(exchange, securityContext);

    assertThat(outcome, is(equalTo(
        AuthenticationMechanism.AuthenticationMechanismOutcome.AUTHENTICATED
    )));
    assertThat(exchange.getAttachment(JwtAttachments.AUTHENTICATOR_KEY), is(equalTo(authenticator)));
    assertThat(exchange.getAttachment(JwtAttachments.CREDENTIAL_KEY), is(equalTo(credential)));
  }

  @Test
  public void testAuthenticateWhenAuthenticatorFailsToAuthenticate() throws Exception {
    exchange.getQueryParameters().put("access_token", new ArrayDeque<>(Collections.singleton(TOKEN)));
    context.checking(authExpectations(null, null));
    context.checking(new Expectations() { {
      oneOf(securityContext).setAuthenticationRequired();
    } });

    final AuthenticationMechanism.AuthenticationMechanismOutcome outcome =
        authMechanism.authenticate(exchange, securityContext);

    assertThat(outcome, is(equalTo(
        AuthenticationMechanism.AuthenticationMechanismOutcome.NOT_AUTHENTICATED
    )));

    assertThat(
        exchange.getAttachment(JwtAttachments.AUTH_MESSAGE_KEY),
        is(equalTo("FAILURE"))
    );
  }

  @Test
  public void testAuthenticateWhenIdentityManagerFailsToVerifyCredential() throws Exception {
    exchange.getQueryParameters().put("access_token", new ArrayDeque<>(Collections.singleton(TOKEN)));
    context.checking(authExpectations(credential, null));
    context.checking(new Expectations() { {
      oneOf(securityContext).authenticationFailed("identity manager does not recognize user 'test-user'", "JWT");
    } });

    final AuthenticationMechanism.AuthenticationMechanismOutcome outcome =
        authMechanism.authenticate(exchange, securityContext);

    assertThat(outcome, is(equalTo(
        AuthenticationMechanism.AuthenticationMechanismOutcome.NOT_AUTHENTICATED
    )));

    assertThat(
        exchange.getAttachment(JwtAttachments.AUTH_FAILED_KEY),
        is(equalTo(StatusCodes.FORBIDDEN))
    );
  }

  private Expectations authExpectations(final Credential credential, final Account account) throws Exception {
    return new Expectations() { {
      allowing(securityContext).isAuthenticationRequired();
      will(returnValue(true));

      oneOf(authenticationService).newAuthenticator();
      will(returnValue(authenticator));
      oneOf(authenticator).validate(TOKEN);
      if (credential == null) {
        will(throwException(new AuthenticationException("FAILURE")));
      }
      else {
        will(returnValue(credential));
        allowing(credential).getPrincipal();
        will(returnValue(userPrincipal));
        allowing(userPrincipal).getName();
        will(returnValue(PRINCIPAL_NAME));
        oneOf(identityManager).verify(PRINCIPAL_NAME, credential);
        will(returnValue(account));

        if (account != null) {
          allowing(account).getPrincipal();
          will(returnValue(userPrincipal));
          allowing(account).getRoles();
          will(returnValue(Collections.emptySet()));
        }
      }
    } };
  }

}