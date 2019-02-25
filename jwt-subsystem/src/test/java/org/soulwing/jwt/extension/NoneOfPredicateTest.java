/*
 * File created on Feb 24, 2019
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
package org.soulwing.jwt.extension;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Test;

/**
 * Unit tests for {@link ClaimAssertionService.NoneOfPredicate}
 * @author Carl Harris
 */
public class NoneOfPredicateTest {

  @Test
  public void testWhenAnyTrue() throws Exception {
    assertThat(new ClaimAssertionService.NoneOfPredicate(
      Arrays.asList(o -> false, o -> true)).test(new Object()), is(false));
  }

  @Test
  public void testWhenNoneTrue() throws Exception {
    assertThat(new ClaimAssertionService.NoneOfPredicate(
        Arrays.asList(o -> false, o -> false)).test(new Object()), is(true));
  }

  @Test
  public void testWhenEmpty() throws Exception {
    assertThat(new ClaimAssertionService.NoneOfPredicate(
        Collections.emptyList()).test(new Object()), is(true));
  }

}
