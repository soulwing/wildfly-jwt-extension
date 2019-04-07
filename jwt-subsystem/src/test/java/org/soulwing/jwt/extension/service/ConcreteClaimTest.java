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
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.soulwing.jwt.extension.api.Claim;

/**
 * Unit tests for {@link ConcreteClaim}.
 * @author Carl Harris
 */
public class ConcreteClaimTest {

  private static final String NAME = "name";
  private static final String STRING_VALUE = "value";
  private static final int INT_VALUE = 42;
  private static final Boolean BOOLEAN_VALUE = Boolean.TRUE;

  @Test(expected = IllegalArgumentException.class)
  public void testWithUnsupportedType() throws Exception {
    new ConcreteClaim(NAME, new Object(), v -> v);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testWithUnsupportedNumericType() throws Exception {
    new ConcreteClaim(NAME, BigDecimal.valueOf(42.0), v -> v);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testWithMapType() throws Exception {
    new ConcreteClaim(NAME, Collections.singletonMap("key", "value"), v -> v);
  }

  @Test
  public void testWithSetType() throws Exception {
    assertThat(
        new ConcreteClaim(NAME, Collections.singleton(STRING_VALUE),
            v -> v).asSet(),
        is(equalTo(Collections.singleton(STRING_VALUE))));
  }

  @Test
  public void testWithArrayType() throws Exception {
    assertThat(
        new ConcreteClaim(NAME, new String[] { STRING_VALUE },
            v -> v).asArray(String.class),
        is(equalTo(new String[] { STRING_VALUE })));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testWithListContainingNonScalarElement() throws Exception {
    new ConcreteClaim(NAME, Collections.singletonList(new Object()), v -> v);
  }

  @Test
  public void testStringWithStringValue() throws Exception {
    final ConcreteClaim claim = new ConcreteClaim(NAME, STRING_VALUE,
        v -> v);
    assertThat(claim.getName(), is(equalTo(NAME)));
    assertThat(claim.getType(), is(equalTo(Claim.Type.STRING)));
    assertThat(claim.getValue(), is(equalTo(STRING_VALUE)));
    assertThat(claim.isNull(), is(false));
    assertThat(claim.asString(), is(equalTo(STRING_VALUE)));
    assertThat(claim.toString(), is(equalTo(STRING_VALUE)));
  }

  @Test
  public void testStringWithNull() throws Exception {
    assertThat(new ConcreteClaim(NAME, null, v -> v).asString(), is(nullValue()));
  }

  @Test(expected = ClassCastException.class)
  public void testStringWithOtherValue() throws Exception {
    new ConcreteClaim(NAME, BOOLEAN_VALUE, v -> v).asString();
  }

  @Test
  public void testBooleanWithBooleanValue() throws Exception {
    final ConcreteClaim claim = new ConcreteClaim(NAME, BOOLEAN_VALUE, v -> v);
    assertThat(claim.getName(), is(equalTo(NAME)));
    assertThat(claim.getType(), is(equalTo(Claim.Type.BOOLEAN)));
    assertThat(claim.getValue(), is(equalTo(BOOLEAN_VALUE)));
    assertThat(claim.isNull(), is(false));
    assertThat(claim.asBoolean(), is(equalTo(BOOLEAN_VALUE)));
    assertThat(claim.toString(), is(equalTo(BOOLEAN_VALUE.toString())));
  }

  @Test
  public void testBooleanWithNull() throws Exception {
    assertThat(new ConcreteClaim(NAME, null, v -> v).asBoolean(), is(nullValue()));
  }

  @Test(expected = ClassCastException.class)
  public void testBooleanWithOtherValue() throws Exception {
    new ConcreteClaim(NAME, "", v -> v).asBoolean();
  }

  @Test
  public void testIntegerWithIntegerValue() throws Exception {
    final int value = INT_VALUE;
    final ConcreteClaim claim = new ConcreteClaim(NAME, value, v -> v);
    assertThat(claim.getName(), is(equalTo(NAME)));
    assertThat(claim.getType(), is(equalTo(Claim.Type.NUMBER)));
    assertThat(claim.getValue(), is(equalTo(INT_VALUE)));
    assertThat(claim.isNull(), is(false));
    assertThat(claim.asInt(), is(equalTo(value)));
    assertThat(claim.asLong(), is(equalTo((long) value)));
    assertThat(claim.asDouble(), is(equalTo((double) value)));
    assertThat(claim.toString(), is(equalTo(Integer.toString(value))));
  }

  @Test
  public void testIntegerWithNull() throws Exception {
    assertThat(new ConcreteClaim(NAME, null, v -> v).asInt(), is(nullValue()));
  }

  @Test(expected = ClassCastException.class)
  public void testIntegerWithOtherValue() throws Exception {
    new ConcreteClaim(NAME, "", v -> v).asInt();
  }

  @Test
  public void testLongWithLongValue() throws Exception {
    final long value = INT_VALUE;
    final ConcreteClaim claim = new ConcreteClaim(NAME, value, v -> v);
    assertThat(claim.getName(), is(equalTo(NAME)));
    assertThat(claim.getType(), is(equalTo(Claim.Type.NUMBER)));
    assertThat(claim.isNull(), is(false));
    assertThat(claim.asInt(), is(equalTo((int) value)));
    assertThat(claim.asLong(), is(equalTo(value)));
    assertThat(claim.asDouble(), is(equalTo((double) value)));
    assertThat(claim.toString(), is(equalTo(Long.toString(value))));
  }

  @Test
  public void testLongWithNull() throws Exception {
    assertThat(new ConcreteClaim(NAME, null, v -> v).asLong(), is(nullValue()));
  }

  @Test(expected = ClassCastException.class)
  public void testLongWithOtherValue() throws Exception {
    new ConcreteClaim(NAME, "", v -> v).asLong();
  }

  @Test
  public void testDoubleWithDoubleValue() throws Exception {
    final double value = 42.0;
    final ConcreteClaim claim = new ConcreteClaim(NAME, value, v -> v);
    assertThat(claim.getName(), is(equalTo(NAME)));
    assertThat(claim.getType(), is(equalTo(Claim.Type.NUMBER)));
    assertThat(claim.isNull(), is(false));
    assertThat(claim.asInt(), is(equalTo((int) value)));
    assertThat(claim.asLong(), is(equalTo((long) value)));
    assertThat(claim.asDouble(), is(equalTo(value)));
    assertThat(claim.toString(), is(equalTo(Double.toString(value))));
  }

  @Test
  public void testDoubleWithNull() throws Exception {
    assertThat(new ConcreteClaim(NAME, null, v -> v).asDouble(), is(nullValue()));
  }

  @Test(expected = ClassCastException.class)
  public void testDoubleWithOtherValue() throws Exception {
    new ConcreteClaim(NAME, "", v -> v).asDouble();
  }

  @Test
  public void testInstantOfEpochSecond() throws Exception {
    final ConcreteClaim claim = new ConcreteClaim(NAME, (long) INT_VALUE, v -> v);
    assertThat(claim.asInstant(),
        is(equalTo(Instant.ofEpochSecond((long) INT_VALUE))));
  }

  @Test
  public void testInstantOfEpochMilli() throws Exception {
    final ConcreteClaim claim = new ConcreteClaim(NAME, (long) INT_VALUE, v -> v);
    assertThat(claim.asInstant(ChronoUnit.MILLIS),
        is(equalTo(Instant.ofEpochMilli((long) INT_VALUE))));
  }

  @Test
  public void testInstantWithNull() throws Exception {
    final ConcreteClaim claim = new ConcreteClaim(NAME, null, v -> v);
    assertThat(claim.asInstant(), is(nullValue()));
  }

  @Test(expected = ClassCastException.class)
  public void testInstantWithOtherValue() throws Exception {
    new ConcreteClaim(NAME, "", v -> v).asInstant();
  }

  @Test
  public void testAsListWithScalarValue() throws Exception {
    final ConcreteClaim claim = new ConcreteClaim(NAME, STRING_VALUE, v -> v);
    assertThat(claim.asList(),
        is(equalTo(Collections.singletonList(STRING_VALUE))));
  }

  @Test
  public void testAsListWithNullValue() throws Exception {
    final ConcreteClaim claim = new ConcreteClaim(NAME, null, v -> v);
    assertThat(claim.asList(), is(nullValue()));
  }

  @Test
  public void testAsListWithListValue() throws Exception {
    final List<String> list = Collections.singletonList(STRING_VALUE);
    final ConcreteClaim claim = new ConcreteClaim(NAME, list, v -> v);
    assertThat(claim.getName(), is(equalTo(NAME)));
    assertThat(claim.getType(), is(equalTo(Claim.Type.ARRAY)));
    assertThat(claim.getValue(), is(equalTo(list)));
    assertThat(claim.asList(), is(equalTo(list)));
    assertThat(claim.asList(), is(not(sameInstance(list))));
  }

  @Test
  public void testAsListOfTypeStringWithString() throws Exception {
    final List<String> list = Collections.singletonList(STRING_VALUE);
    final ConcreteClaim claim = new ConcreteClaim(NAME, list, v -> v);
    assertThat(claim.getName(), is(equalTo(NAME)));
    assertThat(claim.getType(), is(equalTo(Claim.Type.ARRAY)));
    assertThat(claim.asList(String.class), is(equalTo(list)));
    assertThat(claim.asList(), is(not(sameInstance(list))));
  }

  @Test(expected = ClassCastException.class)
  public void testAsListOfTypeStringWithNumber() throws Exception {
    final List<Integer> list = Collections.singletonList(INT_VALUE);
    new ConcreteClaim(NAME, list, v -> v).asList(String.class);
  }

  @Test
  public void testAsSetWithStringValue() throws Exception {
    assertThat(new ConcreteClaim(NAME, Collections.singletonList(STRING_VALUE),
        v -> v).asSet(), is(equalTo(Collections.singleton(STRING_VALUE))));
  }

  @Test
  public void testAsSetWithNullValue() throws Exception {
    assertThat(new ConcreteClaim(NAME, null, v -> v).asSet(), is(nullValue()));
  }

  @Test
  public void testAsSetOfTypeStringWithString() throws Exception {
    assertThat(new ConcreteClaim(NAME,
        Collections.singletonList(STRING_VALUE), v -> v).asSet(String.class),
        is(equalTo(Collections.singleton(STRING_VALUE))));
  }

  @Test(expected = ClassCastException.class)
  public void testAsSetOfTypeStringWithNumber() throws Exception {
    new ConcreteClaim(NAME, Collections.singletonList(INT_VALUE), v -> v)
        .asSet(String.class);
  }

  @Test
  public void testAsSetOfTypeStringWithNull() throws Exception {
    assertThat(new ConcreteClaim(NAME, null, v -> v).asSet(String.class),
        is(nullValue()));
  }

  @Test
  public void testAsArrayOfTypeStringWithString() throws Exception {
    assertThat(new ConcreteClaim(NAME, Collections.singletonList(STRING_VALUE),
        v -> v).asArray(String.class),
        is(equalTo(new String[] { STRING_VALUE })));
  }

  @Test(expected = ClassCastException.class)
  public void testAsArrayOfTypeStringWithNumber() throws Exception {
    new ConcreteClaim(NAME, Collections.singletonList(INT_VALUE), v -> v)
        .asArray(String.class);
  }

  @Test
  public void testAsArrayOfTypeStringWithNull() throws Exception {
    assertThat(new ConcreteClaim(NAME, null, v -> v).asArray(String.class),
        is(nullValue()));
  }

}