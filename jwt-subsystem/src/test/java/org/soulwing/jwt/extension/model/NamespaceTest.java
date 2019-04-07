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
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

import org.junit.Test;

/**
 * Unit tests for {@link Namespace}.
 *
 * @author Carl Harris
 */
public class NamespaceTest {

  @Test
  public void testUnknown() throws Exception {
    assertThat(Namespace.UNKNOWN.getUri(), is(nullValue()));
    assertThat(Namespace.forUri("unknown"), is(equalTo(Namespace.UNKNOWN)));
  }


  @Test
  public void testCurrent() throws Exception {
    assertThat(Namespace.CURRENT.name(), is(not(equalTo(Namespace.UNKNOWN))));
  }

  @Test
  public void test1_0() throws Exception {
    assertThat(Namespace.VERSION_1_0.getUri(), containsString("1.0"));
    assertThat(Namespace.forUri(Namespace.VERSION_1_0.getUri()),
        is(equalTo(Namespace.VERSION_1_0)));
  }

}