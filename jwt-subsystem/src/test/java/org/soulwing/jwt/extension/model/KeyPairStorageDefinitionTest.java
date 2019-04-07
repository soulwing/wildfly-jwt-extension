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
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.startsWith;

import org.junit.Test;

/**
 * Unit tests for {@link KeyPairStorageDefinition}.
 *
 * @author Carl Harris
 */
public class KeyPairStorageDefinitionTest {

  @Test
  public void testGetAttributes() throws Exception {
    assertThat(KeyPairStorageDefinition.INSTANCE.getAttributes(),
        is(not(empty())));
  }

  @Test
  public void testCapability() throws Exception {
    assertThat(KeyPairStorageDefinition.KEY_PAIR_STORAGE_CAPABILITY
        .getCapabilityServiceValueType(),
            is(equalTo(KeyPairStorageService.class)));
    assertThat(KeyPairStorageDefinition.KEY_PAIR_STORAGE_CAPABILITY
            .getCapabilityServiceName().getCanonicalName(),
        startsWith(Capabilities.CAPABILITY_KEY_PAIR_STORAGE));
  }


}