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
package org.soulwing.jwt.extension.jaas;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;

import java.io.IOException;
import java.security.Principal;
import java.security.acl.Group;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginException;

import org.jboss.security.auth.callback.ObjectCallback;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.soulwing.jwt.extension.api.Claim;
import org.soulwing.jwt.extension.api.UserPrincipal;
import org.soulwing.jwt.extension.service.Credential;

/**
 * Unit tests for {@link JwtLoginModule}.
 * @author Carl Harris
 */
public class JwtLoginModuleTest {

  private static final String CLAIM_NAME = "claimName";
  private static final String CLAIM_VALUE = "claimValue";
  private static final String PRINCIPAL_NAME = "principalName";

  @Rule
  public final JUnitRuleMockery context = new JUnitRuleMockery();

  @Rule
  public final ExpectedException expectedException = ExpectedException.none();

  private Subject subject = new Subject();

  @Mock
  private Credential credential;

  @Mock
  private UserPrincipal principal;

  @Mock
  private Claim claim;

  private MockCallbackHandler callbackHandler;

  private JwtLoginModule module = new JwtLoginModule();

  private Map<String, Object> sharedState = new HashMap<>();

  private Map<String, Object> options = new HashMap<>();

  @Before
  public void setUp() throws Exception {
    callbackHandler = new MockCallbackHandler(credential);
  }

  @Test
  public void testLogin() throws Exception {
    module.initialize(subject, callbackHandler, sharedState, options);
    assertThat(module.login(), is(true));

    context.checking(new Expectations() {
      {
        oneOf(credential).getPrincipal();
        will(returnValue(principal));
        allowing(principal).getName();
        will(returnValue(PRINCIPAL_NAME));
      }
    });

    assertThat(module.getIdentity(), is(sameInstance(principal)));
  }

  @Test
  public void testLoginWhenCallbackReturnsNonCredential() throws Exception {
    module.initialize(subject, new MockCallbackHandler(new Object()),
        sharedState, options);
    assertThat(module.login(), is(false));
  }

  @Test
  public void testLoginWhenUnsupportedCallback() throws Exception {
    final CallbackHandler handler = context.mock(CallbackHandler.class);
    module.initialize(subject, handler, sharedState, options);

    context.checking(new Expectations() {
      {
        oneOf(handler).handle(with(any(Callback[].class)));
        will(throwException(new UnsupportedCallbackException(
            new ObjectCallback("callback"))));
      }
    });

    expectedException.expect(LoginException.class);
    expectedException.expectMessage("not supported");
    module.login();
  }

  @Test
  public void testLoginWhenIOException() throws Exception {
    final CallbackHandler handler = context.mock(CallbackHandler.class);
    module.initialize(subject, handler, sharedState, options);

    context.checking(new Expectations() {
      {
        oneOf(handler).handle(with(any(Callback[].class)));
        will(throwException(new IOException("error")));
      }
    });

    expectedException.expect(LoginException.class);
    expectedException.expectMessage("I/O");
    module.login();
  }

  @Test
  public void testGetRoleSets() throws Exception {
    options.put(JwtLoginModule.ROLE_CLAIMS, CLAIM_NAME);
    module.initialize(subject, callbackHandler, sharedState, options);
    assertThat(module.login(), is(true));
    context.checking(claimsExpectations());

    final Group[] groups = module.getRoleSets();
    assertThat(groups.length, is(equalTo(1)));
    assertThat(Collections.list(groups[0].members()),
        contains(hasProperty("name", is(equalTo(CLAIM_VALUE)))));
  }

  @Test
  public void testGetRoleSetsWhenNoClaims() throws Exception {
    module.initialize(subject, callbackHandler, sharedState, options);
    assertThat(module.login(), is(true));
    context.checking(new Expectations() {
      {
        oneOf(credential).getPrincipal();
        will(returnValue(principal));
      }
    });

    final Group[] groups = module.getRoleSets();
    assertThat(groups.length, is(equalTo(0)));
  }

  @Test
  public void testGetRoleSetsWhenRuntimeException() throws Exception {
    final RuntimeException ex = new RuntimeException("error");
    module.initialize(subject, callbackHandler, sharedState, options);
    assertThat(module.login(), is(true));
    context.checking(new Expectations() {
      {
        oneOf(credential).getPrincipal();
        will(throwException(ex));
      }
    });

    expectedException.expect(is(sameInstance(ex)));
    module.getRoleSets();
  }


  @Test
  public void testGetRoles() throws Exception {
    options.put(JwtLoginModule.ROLE_CLAIMS, CLAIM_NAME);
    module.initialize(subject, callbackHandler, sharedState, options);
    assertThat(module.login(), is(true));
    context.checking(claimsExpectations());

    final Set<Principal> roles = module.getRoles();
    assertThat(roles, contains(hasProperty("name", is(equalTo(CLAIM_VALUE)))));
  }

  @Test
  public void testGetRolesWhenClaimsNotPresent() throws Exception {
    options.put(JwtLoginModule.ROLE_CLAIMS, "one, two");
    module.initialize(subject, callbackHandler, sharedState, options);
    assertThat(module.login(), is(true));

    context.checking(new Expectations() {
      {
        oneOf(credential).getPrincipal();
        will(returnValue(principal));
        oneOf(principal).getClaim("one");
        will(returnValue(claim));
        oneOf(principal).getClaim("two");
        will(returnValue(claim));
        allowing(claim).isNull();
        will(returnValue(true));
      }
    });

    assertThat(module.getRoles(), is(empty()));
  }

  private Expectations claimsExpectations() {
    return new Expectations() {
      {
        oneOf(credential).getPrincipal();
        will(returnValue(principal));
        oneOf(principal).getClaim(CLAIM_NAME);
        will(returnValue(claim));
        oneOf(claim).isNull();
        will(returnValue(false));
        oneOf(claim).asList(String.class);
        will(returnValue(Collections.singletonList(CLAIM_VALUE)));
      }
    };
  }


  private static class MockCallbackHandler implements CallbackHandler {

    private final Object credential;

    MockCallbackHandler(Object credential) {
      this.credential = credential;
    }

    @Override
    public void handle(Callback[] callbacks)
        throws UnsupportedCallbackException {
      for (final Callback callback : callbacks) {
        try {
          ((ObjectCallback) callback).setCredential(credential);
        }
        catch (ClassCastException ex) {
          throw new UnsupportedCallbackException(callback);
        }
      }
    }

  }

}