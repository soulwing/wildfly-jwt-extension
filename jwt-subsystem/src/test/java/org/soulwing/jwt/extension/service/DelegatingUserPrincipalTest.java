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
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.soulwing.jwt.api.Claims;
import org.soulwing.jwt.extension.api.Claim;

/**
 * Unit tests for {@link DelegatingUserPrincipal}.
 *
 * @author Carl Harris
 */
public class DelegatingUserPrincipalTest {

  private static final String SUBJECT = "subject";
  private static final String CLAIM_NAME = "claimName";
  private static final String CLAIM_VALUE = "claimValue";
  private static final String TRANSFORMED_CLAIM_NAME = "transformedClaimName";
  private static final String TRANSFORMED_CLAIM_VALUE = "transformedClaimValue";
  @Rule
  public final JUnitRuleMockery context = new JUnitRuleMockery();

  @Mock
  private Claims claims;

  @Mock
  private TransformConfiguration transformConfiguration;

  private DelegatingUserPrincipal principal;

  @Before
  public void setUp() throws Exception {
    context.checking(new Expectations() {
      {
        oneOf(transformConfiguration).getClaimName();
        will(returnValue(TRANSFORMED_CLAIM_NAME));
        oneOf(transformConfiguration).getTransformer();
        will(returnValue((Function<Object, Object>)
            v -> TRANSFORMED_CLAIM_VALUE));
      }
    });
    principal = DelegatingUserPrincipal.newInstance(claims,
        Collections.singletonList(transformConfiguration));
  }

  @Test
  public void testGetName() throws Exception {
    context.checking(new Expectations() {
      {
        oneOf(claims).getSubject();
        will(returnValue(SUBJECT));
      }
    });

    assertThat(principal.getName(), is(equalTo(SUBJECT)));
  }

  @Test
  public void testGetClaim() throws Exception {
    context.checking(new Expectations() {
      {
        oneOf(claims).claim(CLAIM_NAME, Object.class);
        will(returnValue(Optional.of(CLAIM_VALUE)));
      }
    });

    assertThat(principal.getClaim(CLAIM_NAME).getValue(),
        is(equalTo(CLAIM_VALUE)));
  }

  @Test
  public void testGetClaimWhenNotPresent() throws Exception {
    context.checking(new Expectations() {
      {
        oneOf(claims).claim(CLAIM_NAME, Object.class);
        will(returnValue(Optional.empty()));
      }
    });

    final Claim claim = principal.getClaim(CLAIM_NAME);
    assertThat(claim.isNull(), is(true));
    assertThat(claim.getValue(), is(nullValue()));
  }


  @Test
  public void testHasClaim() throws Exception {
    context.checking(new Expectations() {
      {
        allowing(claims).names();
        will(returnValue(Collections.singleton(CLAIM_NAME)));
      }
    });

    assertThat(principal.hasClaim(CLAIM_NAME), is(true));
    assertThat(principal.hasClaim("other" + CLAIM_NAME), is(false));
  }

  @Test
  public void testGetClaims() throws Exception {
    context.checking(new Expectations() {
      {
        allowing(claims).names();
        will(returnValue(Collections.singleton(CLAIM_NAME)));
        oneOf(claims).claim(CLAIM_NAME, Object.class);
        will(returnValue(Optional.of(CLAIM_VALUE)));
      }
    });

    final Map<String, Claim> claims = principal.getClaims();
    assertThat(claims.size(), is(equalTo(1)));
    assertThat(claims.containsKey(CLAIM_NAME), is(true));
    assertThat(claims.get(CLAIM_NAME).getValue(), is(equalTo(CLAIM_VALUE)));
  }

  @Test
  public void testGetClaimWithTransformer() throws Exception {
    context.checking(new Expectations() {
      {
        oneOf(claims).claim(TRANSFORMED_CLAIM_NAME, Object.class);
        will(returnValue(Optional.of(CLAIM_VALUE)));
      }
    });

    assertThat(principal.getClaim(TRANSFORMED_CLAIM_NAME).getValue(),
        is(equalTo(TRANSFORMED_CLAIM_VALUE)));
  }

}