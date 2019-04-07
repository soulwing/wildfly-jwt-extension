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
package org.soulwing.jwt.extension.model;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import org.jboss.as.controller.registry.ManagementResourceRegistration;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

/**
 * Unit tests for {@link JwtSubsystemDefinition}.
 *
 * @author Carl Harris
 */
public class JwtSubsystemDefinitionTest {

  @Rule
  public final JUnitRuleMockery context = new JUnitRuleMockery();

  @Mock
  private ManagementResourceRegistration registration;

  @Test
  public void testGetAttributes() throws Exception {
    assertThat(JwtSubsystemDefinition.INSTANCE.getAttributes(),
        is(not(empty())));
  }

  @Test
  public void testRegisterChildren() throws Exception {
    context.checking(new Expectations() {
      {
        oneOf(registration).registerSubModel(with(any(SecretDefinition.class)));
        oneOf(registration).registerSubModel(with(any(SecretKeyDefinition.class)));
        oneOf(registration).registerSubModel(with(any(TrustStoreDefinition.class)));
        oneOf(registration).registerSubModel(with(any(KeyPairStorageDefinition.class)));
        oneOf(registration).registerSubModel(with(any(TransformerDefinition.class)));
        oneOf(registration).registerSubModel(with(any(ClaimTransformDefinition.class)));
        oneOf(registration).registerSubModel(with(any(ClaimAssertionDefinition.class)));
        oneOf(registration).registerSubModel(with(any(SignatureDefinition.class)));
        oneOf(registration).registerSubModel(with(any(EncryptionDefinition.class)));
        oneOf(registration).registerSubModel(with(any(ValidatorDefinition.class)));
      }
    });

    JwtSubsystemDefinition.INSTANCE.registerChildren(registration);
  }

}