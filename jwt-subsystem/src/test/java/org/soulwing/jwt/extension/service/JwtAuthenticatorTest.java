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
package org.soulwing.jwt.extension.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.Optional;
import java.util.function.Function;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.soulwing.jwt.api.Claims;
import org.soulwing.jwt.api.JWTValidator;
import org.soulwing.jwt.api.exceptions.ExpirationAssertionException;
import org.soulwing.jwt.api.exceptions.JWTConfigurationException;
import org.soulwing.jwt.api.exceptions.JWTEncryptionException;
import org.soulwing.jwt.api.exceptions.JWTSignatureException;
import org.soulwing.jwt.api.exceptions.JWTValidationException;
import org.soulwing.jwt.extension.api.UserPrincipal;

/**
 * Unit tests for {@link JwtAuthenticator}.
 *
 * @author Carl Harris
 */
public class JwtAuthenticatorTest {

  private static final String BEARER_TOKEN = "bearerToken";
  private static final String CLAIM_NAME = "claimName";
  private static final String CLAIM_VALUE = "claimValue";
  private static final String TRANSFORMED_CLAIM_NAME = "transformedClaimName";
  private static final String TRANSFORMED_CLAIM_VALUE = "transformedClaimValue";

  @Rule
  public final JUnitRuleMockery context = new JUnitRuleMockery();

  @Rule
  public final ExpectedException expectedException = ExpectedException.none();
  @Mock
  private JWTValidator validator;

  @Mock
  private Configuration configuration;

  @Mock
  private TransformConfiguration transformer;

  @Mock
  private Claims claims;

  private JwtAuthenticator authenticator;

  @Before
  public void setUp() throws Exception {
    authenticator = new JwtAuthenticator(validator, configuration);
  }

  @Test
  public void testValidate() throws Exception {
    context.checking(new Expectations() {
      {
        oneOf(configuration).getTransforms();
        will(returnValue(Collections.singletonList(transformer)));
        allowing(transformer).getClaimName();
        will(returnValue(TRANSFORMED_CLAIM_NAME));
        allowing(transformer).getTransformer();
        will(returnValue((Function<Object, Object>)
            v -> TRANSFORMED_CLAIM_VALUE));
        oneOf(validator).validate(BEARER_TOKEN);
        will(returnValue(claims));
      }
    });

    final Credential credential = authenticator.validate(BEARER_TOKEN);
    assertThat(credential, is(instanceOf(JwtCredential.class)));

    context.checking(new Expectations() {
      {
        allowing(claims).claim(CLAIM_NAME, Object.class);
        will(returnValue(Optional.of(CLAIM_VALUE)));
        allowing(claims).claim(TRANSFORMED_CLAIM_NAME, Object.class);
        will(returnValue(Optional.of(CLAIM_VALUE)));
      }
    });

    final UserPrincipal principal = credential.getPrincipal();
    assertThat(principal, is(instanceOf(DelegatingUserPrincipal.class)));
    assertThat(principal.getClaim(CLAIM_NAME).asString(),
        is(equalTo(CLAIM_VALUE)));
    assertThat(principal.getClaim(TRANSFORMED_CLAIM_NAME).asString(),
        is(equalTo(TRANSFORMED_CLAIM_VALUE)));
  }

  @Test
  public void testValidateWhenExpiredToken() throws Exception {
    context.checking(new Expectations() {
      {
        oneOf(validator).validate(BEARER_TOKEN);
        will(throwException(new ExpirationAssertionException(
            Instant.EPOCH, Instant.EPOCH, Duration.ZERO)));
      }
    });

    expectedException.expect(AuthenticationException.class);
    expectedException.expectMessage("expiration");
    authenticator.validate(BEARER_TOKEN);
  }


  @Test
  public void testValidateWhenConfigurationError() throws Exception {
    context.checking(new Expectations() {
      {
        oneOf(validator).validate(BEARER_TOKEN);
        will(throwException(new JWTConfigurationException("configuration error")));
      }
    });

    expectedException.expect(AuthenticationException.class);
    expectedException.expectMessage("configuration error");
    authenticator.validate(BEARER_TOKEN);
  }

  @Test
  public void testValidateWhenValidationError() throws Exception {
    context.checking(new Expectations() {
      {
        oneOf(validator).validate(BEARER_TOKEN);
        will(throwException(new JWTValidationException("validation error")));
      }
    });

    expectedException.expect(AuthenticationException.class);
    expectedException.expectMessage("validation error");
    authenticator.validate(BEARER_TOKEN);
  }

  @Test
  public void testValidateWhenSignatureError() throws Exception {
    context.checking(new Expectations() {
      {
        oneOf(validator).validate(BEARER_TOKEN);
        will(throwException(new JWTSignatureException("signature error")));
      }
    });

    expectedException.expect(AuthenticationException.class);
    expectedException.expectMessage("signature error");
    authenticator.validate(BEARER_TOKEN);
  }

  @Test
  public void testValidateWhenEncryptionError() throws Exception {
    context.checking(new Expectations() {
      {
        oneOf(validator).validate(BEARER_TOKEN);
        will(throwException(new JWTEncryptionException("encryption error")));
      }
    });

    expectedException.expect(AuthenticationException.class);
    expectedException.expectMessage("encryption error");
    authenticator.validate(BEARER_TOKEN);
  }

  @Test
  public void testValidateWhenParseError() throws Exception {
    context.checking(new Expectations() {
      {
        oneOf(validator).validate(BEARER_TOKEN);
        will(throwException(new JWTEncryptionException("parse error")));
      }
    });

    expectedException.expect(AuthenticationException.class);
    expectedException.expectMessage("parse error");
    authenticator.validate(BEARER_TOKEN);
  }

}