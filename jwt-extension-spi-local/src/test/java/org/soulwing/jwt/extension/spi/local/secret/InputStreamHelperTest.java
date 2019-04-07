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
package org.soulwing.jwt.extension.spi.local.secret;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.io.ByteArrayInputStream;
import java.util.Random;

import org.junit.Test;

/**
 * Unit tests for {@link InputStreamHelper}.
 *
 * @author Carl Harris
 */
public class InputStreamHelperTest {

  @Test
  public void testToByteArray() throws Exception {
    final Random random = new Random();
    final byte[] data = new byte[10000];
    random.nextBytes(data);
    final ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
    final byte[] actual = InputStreamHelper.toByteArray(inputStream);
    assertThat(actual, is(equalTo(data)));
  }

}