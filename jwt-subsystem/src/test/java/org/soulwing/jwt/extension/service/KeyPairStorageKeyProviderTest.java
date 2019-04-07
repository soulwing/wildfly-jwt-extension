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
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;

import java.security.KeyPair;
import java.security.KeyPairGenerator;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.soulwing.jwt.api.exceptions.KeyProviderException;
import org.soulwing.s2ks.KeyPairInfo;
import org.soulwing.s2ks.KeyPairStorage;
import org.soulwing.s2ks.KeyStorageException;
import org.soulwing.s2ks.NoSuchKeyException;

/**
 * Unit tests for {@link KeyPairStorageKeyProvider}.
 *
 * @author Carl Harris
 */
public class KeyPairStorageKeyProviderTest {

  private static final String KEY_ID = "keyId";

  private static KeyPair keyPair;

  @Rule
  public final JUnitRuleMockery context = new JUnitRuleMockery();

  @Rule
  public final ExpectedException expectedException = ExpectedException.none();

  @Mock
  private KeyPairStorage keyPairStorage;

  private KeyPairInfo keyPairInfo;

  private KeyPairStorageKeyProvider keyProvider;

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
    final KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
    kpg.initialize(2048);
    keyPair = kpg.generateKeyPair();
  }

  @Before
  public void setUp() throws Exception {
    keyProvider = new KeyPairStorageKeyProvider(keyPairStorage);
    keyPairInfo = KeyPairInfo.builder()
        .id(KEY_ID)
        .privateKey(keyPair.getPrivate())
        .build();
  }

  @Test(expected = UnsupportedOperationException.class)
  public void testCurrentKey() throws Exception {
    keyProvider.currentKey();
  }

  @Test
  public void testRetrieveKey() throws Exception {
    context.checking(new Expectations() {
      {
        oneOf(keyPairStorage).retrieveKeyPair(KEY_ID);
        will(returnValue(keyPairInfo));
      }
    });

    assertThat(keyProvider.retrieveKey(KEY_ID).orElse(null),
        is(sameInstance(keyPair.getPrivate())));
  }

  @Test
  public void testRetrieveKeyWhenNotFound() throws Exception {
    context.checking(new Expectations() {
      {
        oneOf(keyPairStorage).retrieveKeyPair(KEY_ID);
        will(throwException(new NoSuchKeyException(KEY_ID)));
      }
    });

    assertThat(keyProvider.retrieveKey(KEY_ID).isPresent(), is(false));
  }

  @Test
  public void testRetrieveKeyWhenKeyStorageException() throws Exception {
    final KeyStorageException ex = new KeyStorageException("error");
    context.checking(new Expectations() {
      {
        oneOf(keyPairStorage).retrieveKeyPair(KEY_ID);
        will(throwException(ex));
      }
    });

    expectedException.expect(KeyProviderException.class);
    expectedException.expectCause(is(sameInstance(ex)));
    keyProvider.retrieveKey(KEY_ID);
  }

}