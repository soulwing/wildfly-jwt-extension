/*
 * File created on Feb 14, 2019
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
package org.soulwing.jwt.jaas;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import java.time.Instant;
import java.util.Date;
import java.util.function.Consumer;

import org.junit.Before;
import org.junit.Test;
import org.soulwing.jwt.api.Claim;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;

/**
 * Unit tests for {@link DelegatingClaim}.
 *
 * @author Carl Harris
 */
public class TestDelegatingClaim {

  private static final String ISSUER = "testIssuer";
  private static final String CLAIM = "claim";

  private Algorithm algorithm;
  private JWTVerifier verifier;

  @Before
  public void setUp() throws Exception {
    algorithm = Algorithm.HMAC256("secret");
    verifier = JWT.require(algorithm)
        .withIssuer(ISSUER)
        .build();
  }

  @Test
  public void testTypeWithStringClaim() throws Exception {
    final DelegatingClaim claim = new DelegatingClaim(
        decodedToken(newToken(b -> b.withClaim(CLAIM, "string")))
            .getClaim(CLAIM));

    assertThat(claim.type(), is(equalTo(Claim.Type.STRING)));
  }

  @Test
  public void testTypeWithBooleanClaim() throws Exception {
    final DelegatingClaim claim = new DelegatingClaim(
        decodedToken(newToken(b -> b.withClaim(CLAIM, true)))
            .getClaim(CLAIM));

    assertThat(claim.type(), is(equalTo(Claim.Type.BOOLEAN)));
  }

  @Test
  public void testTypeWithIntClaim() throws Exception {
    final DelegatingClaim claim = new DelegatingClaim(
        decodedToken(newToken(b -> b.withClaim(CLAIM, 1)))
            .getClaim(CLAIM));

    assertThat(claim.type(), is(equalTo(Claim.Type.NUMBER)));
  }

  @Test
  public void testTypeWithLongClaim() throws Exception {
    final DelegatingClaim claim = new DelegatingClaim(
        decodedToken(newToken(b -> b.withClaim(CLAIM, 1L)))
            .getClaim(CLAIM));

    assertThat(claim.type(), is(equalTo(Claim.Type.NUMBER)));
  }

  @Test
  public void testTypeWithDoubleClaim() throws Exception {
    final DelegatingClaim claim = new DelegatingClaim(
        decodedToken(newToken(b -> b.withClaim(CLAIM, 1.0)))
            .getClaim(CLAIM));

    assertThat(claim.type(), is(equalTo(Claim.Type.NUMBER)));
  }

  @Test
  public void testTypeWithStringArrayClaim() throws Exception {
    final DelegatingClaim claim = new DelegatingClaim(
        decodedToken(newToken(b -> b.withArrayClaim(CLAIM, new String[]{ })))
            .getClaim(CLAIM));

    assertThat(claim.type(), is(equalTo(Claim.Type.ARRAY)));
  }

  @Test
  public void testTypeWithIntArrayClaim() throws Exception {
    final DelegatingClaim claim = new DelegatingClaim(
        decodedToken(newToken(b -> b.withArrayClaim(CLAIM, new Integer[]{ })))
            .getClaim(CLAIM));

    assertThat(claim.type(), is(equalTo(Claim.Type.ARRAY)));
  }

  @Test
  public void testTypeWithLongArrayClaim() throws Exception {
    final DelegatingClaim claim = new DelegatingClaim(
        decodedToken(newToken(b -> b.withArrayClaim(CLAIM, new Long[]{})))
            .getClaim(CLAIM));

    assertThat(claim.type(), is(equalTo(Claim.Type.ARRAY)));
  }

  @Test
  public void testIsNullWithNullClaim() throws Exception {
    final DelegatingClaim claim = new DelegatingClaim(decodedToken(newToken())
            .getClaim(CLAIM));

    assertThat(claim.isNull(), is(true));
  }

  @Test
  public void testIsNullWithNonNullClaim() throws Exception {
    final DelegatingClaim claim = new DelegatingClaim(
        decodedToken(newToken(b -> b.withClaim(CLAIM, "string")))
        .getClaim(CLAIM));

    assertThat(claim.isNull(), is(false));
  }

  @Test
  public void testAsStringWithNullClaim() throws Exception {
    final DelegatingClaim claim = new DelegatingClaim(decodedToken(newToken())
        .getClaim(CLAIM));

    assertThat(claim.asString(), is(nullValue()));
  }

  @Test
  public void testAsStringWithStringClaim() throws Exception {
    final DelegatingClaim claim = new DelegatingClaim(
        decodedToken(newToken(b -> b.withClaim(CLAIM, "string")))
            .getClaim(CLAIM));

    assertThat(claim.asString(), is(equalTo("string")));
  }

  @Test
  public void testAsStringWithBooleanClaim() throws Exception {
    final DelegatingClaim claim = new DelegatingClaim(
        decodedToken(newToken(b -> b.withClaim(CLAIM, true)))
            .getClaim(CLAIM));

    assertThat(claim.asString(), is(equalTo("true")));
  }

  @Test
  public void testAsStringWithIntClaim() throws Exception {
    final DelegatingClaim claim = new DelegatingClaim(
        decodedToken(newToken(b -> b.withClaim(CLAIM, 1)))
            .getClaim(CLAIM));

    assertThat(claim.asString(), is(equalTo("1")));
  }

  @Test
  public void testAsStringWithLongClaim() throws Exception {
    final DelegatingClaim claim = new DelegatingClaim(
        decodedToken(newToken(b -> b.withClaim(CLAIM, 1L)))
            .getClaim(CLAIM));

    assertThat(claim.asString(), is(equalTo("1")));
  }

  @Test
  public void testAsStringWithDoubleClaim() throws Exception {
    final DelegatingClaim claim = new DelegatingClaim(
        decodedToken(newToken(b -> b.withClaim(CLAIM, 2.718281828)))
            .getClaim(CLAIM));

    assertThat(claim.asString(), is(equalTo("2.718281828")));
  }

  @Test(expected = ClassCastException.class)
  public void testAsStringWithArrayClaim() throws Exception {
    final DelegatingClaim claim = new DelegatingClaim(
        decodedToken(newToken(b -> b.withArrayClaim(CLAIM, new String[]{ })))
            .getClaim(CLAIM));
    claim.asString();
  }

  @Test
  public void testAsBooleanWithNullClaim() throws Exception {
    final DelegatingClaim claim = new DelegatingClaim(decodedToken(newToken())
        .getClaim(CLAIM));

    assertThat(claim.asBoolean(), is(nullValue()));
  }

  @Test
  public void testAsBooleanWithBooleanClaim() throws Exception {
    final DelegatingClaim claim = new DelegatingClaim(
        decodedToken(newToken(b -> b.withClaim(CLAIM, true)))
            .getClaim(CLAIM));

    assertThat(claim.asBoolean(), is(true));
  }

  @Test(expected = ClassCastException.class)
  public void testAsBooleanWithOtherClaim() throws Exception {
    final DelegatingClaim claim = new DelegatingClaim(
        decodedToken(newToken(b -> b.withClaim(CLAIM, "string")))
            .getClaim(CLAIM));
    claim.asBoolean();
  }

  @Test
  public void testAsIntWithNullClaim() throws Exception {
    final DelegatingClaim claim = new DelegatingClaim(decodedToken(newToken())
        .getClaim(CLAIM));

    assertThat(claim.asInt(), is(nullValue()));
  }

  @Test
  public void testAsIntWithIntClaim() throws Exception {
    final DelegatingClaim claim = new DelegatingClaim(
        decodedToken(newToken(b -> b.withClaim(CLAIM, 1)))
            .getClaim(CLAIM));

    assertThat(claim.asInt(), is(1));
  }

  @Test(expected = ClassCastException.class)
  public void testAsIntWithOtherClaim() throws Exception {
    final DelegatingClaim claim = new DelegatingClaim(
        decodedToken(newToken(b -> b.withClaim(CLAIM, "string")))
            .getClaim(CLAIM));
    claim.asInt();
  }

  @Test
  public void testAsLongWithNullClaim() throws Exception {
    final DelegatingClaim claim = new DelegatingClaim(decodedToken(newToken())
        .getClaim(CLAIM));

    assertThat(claim.asLong(), is(nullValue()));
  }

  @Test
  public void testAsLongWithLongClaim() throws Exception {
    final DelegatingClaim claim = new DelegatingClaim(
        decodedToken(newToken(b -> b.withClaim(CLAIM, 1L)))
            .getClaim(CLAIM));

    assertThat(claim.asLong(), is(1L));
  }

  @Test(expected = ClassCastException.class)
  public void testAsLongWithOtherClaim() throws Exception {
    final DelegatingClaim claim = new DelegatingClaim(
        decodedToken(newToken(b -> b.withClaim(CLAIM, "string")))
            .getClaim(CLAIM));
    claim.asLong();
  }

  @Test
  public void testAsDoubleWithNullClaim() throws Exception {
    final DelegatingClaim claim = new DelegatingClaim(decodedToken(newToken())
        .getClaim(CLAIM));

    assertThat(claim.asDouble(), is(nullValue()));
  }

  @Test
  public void testAsDoubleWithDoubleClaim() throws Exception {
    final DelegatingClaim claim = new DelegatingClaim(
        decodedToken(newToken(b -> b.withClaim(CLAIM, 1.0)))
            .getClaim(CLAIM));

    assertThat(claim.asDouble(), is(1.0));
  }

  @Test(expected = ClassCastException.class)
  public void testAsDoubleWithOtherClaim() throws Exception {
    final DelegatingClaim claim = new DelegatingClaim(
        decodedToken(newToken(b -> b.withClaim(CLAIM, "string")))
            .getClaim(CLAIM));
    claim.asDouble();
  }

  @Test
  public void testAsInstantWithNullClaim() throws Exception {
    final DelegatingClaim claim = new DelegatingClaim(decodedToken(newToken())
        .getClaim(CLAIM));

    assertThat(claim.asInstant(), is(nullValue()));
  }

  @Test
  public void testAsInstantWithEpochClaim() throws Exception {
    final long v = Instant.now().getEpochSecond();
    final DelegatingClaim claim = new DelegatingClaim(
        decodedToken(newToken(b -> b.withClaim(CLAIM, v)))
            .getClaim(CLAIM));

    assertThat(claim.asInstant(), is(equalTo(Instant.ofEpochSecond(v))));
  }

  @Test(expected = ClassCastException.class)
  public void testAsInstantWithOtherClaim() throws Exception {
    final DelegatingClaim claim = new DelegatingClaim(
        decodedToken(newToken(b -> b.withClaim(CLAIM, "string")))
            .getClaim(CLAIM));
    claim.asInstant();
  }

  @Test
  public void testAsDateWithNullClaim() throws Exception {
    final DelegatingClaim claim = new DelegatingClaim(decodedToken(newToken())
        .getClaim(CLAIM));

    assertThat(claim.asInstant(), is(nullValue()));
  }

  @Test
  public void testAsDateWithEpochClaim() throws Exception {
    final long v = Instant.now().getEpochSecond();
    final DelegatingClaim claim = new DelegatingClaim(
        decodedToken(newToken(b -> b.withClaim(CLAIM, v)))
            .getClaim(CLAIM));

    assertThat(claim.asDate(),
        is(equalTo(new Date(Instant.ofEpochSecond(v).toEpochMilli()))));
  }

  @Test(expected = ClassCastException.class)
  public void testAsDateWithOtherClaim() throws Exception {
    final DelegatingClaim claim = new DelegatingClaim(
        decodedToken(newToken(b -> b.withClaim(CLAIM, "string")))
            .getClaim(CLAIM));
    claim.asDate();
  }

  @Test
  public void testAsLArrayOfStringsWithNullClaim() throws Exception {
    final DelegatingClaim claim = new DelegatingClaim(decodedToken(newToken())
        .getClaim(CLAIM));

    assertThat(claim.asArray(String.class), is(nullValue()));
  }

  @Test
  public void testAsArrayOfStringsWithStrings() throws Exception {
    final DelegatingClaim claim = new DelegatingClaim(
        decodedToken(newToken(b ->
            b.withArrayClaim(CLAIM, new String[] { "a", "b", "c" })))
            .getClaim(CLAIM));
    assertThat(claim.asArray(String.class), arrayContaining("a", "b", "c"));
  }

  @Test
  public void testAsArrayOfStringsWithInts() throws Exception {
    final DelegatingClaim claim = new DelegatingClaim(
        decodedToken(newToken(b ->
            b.withArrayClaim(CLAIM, new Integer[] { 0, 1, 2 })))
            .getClaim(CLAIM));
    assertThat(claim.asArray(String.class), arrayContaining("0", "1", "2"));
  }

  @Test
  public void testAsArrayOfStringsWithLongs() throws Exception {
    final DelegatingClaim claim = new DelegatingClaim(
        decodedToken(newToken(b ->
            b.withArrayClaim(CLAIM, new Long[] { 0L, 1L, 2L })))
            .getClaim(CLAIM));
    assertThat(claim.asArray(String.class), arrayContaining("0", "1", "2"));
  }

  @Test
  public void testAsArrayOfIntsWithInts() throws Exception {
    final DelegatingClaim claim = new DelegatingClaim(
        decodedToken(newToken(b ->
            b.withArrayClaim(CLAIM, new Integer[] { 0, 1, 2 })))
            .getClaim(CLAIM));
    assertThat(claim.asArray(Integer.class), arrayContaining(0, 1, 2));
  }

  @Test
  public void testAsArrayOfIntsWithLongs() throws Exception {
    final DelegatingClaim claim = new DelegatingClaim(
        decodedToken(newToken(b ->
            b.withArrayClaim(CLAIM, new Long[] { 0L, 1L, 2L })))
            .getClaim(CLAIM));
    assertThat(claim.asArray(Integer.class), arrayContaining(0, 1, 2));
  }

  @Test(expected = ClassCastException.class)
  public void testAsArrayOfIntsWithStrings() throws Exception {
    final DelegatingClaim claim = new DelegatingClaim(
        decodedToken(newToken(b ->
            b.withArrayClaim(CLAIM, new String[] { "a", "b", "c" })))
            .getClaim(CLAIM));
    claim.asArray(Integer.class);
  }

  @Test
  public void testAsListWithNullClaim() throws Exception {
    final DelegatingClaim claim = new DelegatingClaim(decodedToken(newToken())
        .getClaim(CLAIM));

    assertThat(claim.asList(), is(nullValue()));
  }

  @Test
  public void testAsListOfStringWithNullClaim() throws Exception {
    final DelegatingClaim claim = new DelegatingClaim(decodedToken(newToken())
        .getClaim(CLAIM));

    assertThat(claim.asList(String.class), is(nullValue()));
  }

  @Test
  public void testAsListWithStrings() throws Exception {
    final DelegatingClaim claim = new DelegatingClaim(
        decodedToken(newToken(b ->
                b.withArrayClaim(CLAIM, new String[] { "a", "b", "c" })))
            .getClaim(CLAIM));
    assertThat(claim.asList(), contains("a", "b", "c"));
  }

  @Test
  public void testAsListWithInts() throws Exception {
    final DelegatingClaim claim = new DelegatingClaim(
        decodedToken(newToken(b ->
            b.withArrayClaim(CLAIM, new Integer[] { 0, 1, 2 })))
            .getClaim(CLAIM));
    assertThat(claim.asList(), contains(0, 1, 2));
  }

  @Test
  public void testAsListWithLongs() throws Exception {
    final DelegatingClaim claim = new DelegatingClaim(
        decodedToken(newToken(b ->
            b.withArrayClaim(CLAIM, new Long[] { 0L, 1L, 2L })))
            .getClaim(CLAIM));
    assertThat(claim.asList(), contains(0, 1, 2)); // the returned list will contain ints
  }

  @Test
  public void testAsListOfStringsWithStrings() throws Exception {
    final DelegatingClaim claim = new DelegatingClaim(
        decodedToken(newToken(b ->
            b.withArrayClaim(CLAIM, new String[] { "a", "b", "c" })))
            .getClaim(CLAIM));
    assertThat(claim.asList(String.class), contains("a", "b", "c"));
  }

  @Test
  public void testAsListOfStringsWithInts() throws Exception {
    final DelegatingClaim claim = new DelegatingClaim(
        decodedToken(newToken(b ->
            b.withArrayClaim(CLAIM, new Integer[] { 0, 1, 2 })))
            .getClaim(CLAIM));
    assertThat(claim.asList(String.class), contains("0", "1", "2"));
  }

  @Test
  public void testAsListOfStringsWithLongs() throws Exception {
    final DelegatingClaim claim = new DelegatingClaim(
        decodedToken(newToken(b ->
            b.withArrayClaim(CLAIM, new Long[] { 0L, 1L, 2L })))
            .getClaim(CLAIM));
    assertThat(claim.asList(String.class), contains("0", "1", "2"));
  }

  @Test
  public void testAsListOfIntsWithInts() throws Exception {
    final DelegatingClaim claim = new DelegatingClaim(
        decodedToken(newToken(b ->
            b.withArrayClaim(CLAIM, new Integer[] { 0, 1, 2 })))
            .getClaim(CLAIM));
    assertThat(claim.asList(Integer.class), contains(0, 1, 2));
  }

  @Test
  public void testAsListOfIntsWithLongs() throws Exception {
    final DelegatingClaim claim = new DelegatingClaim(
        decodedToken(newToken(b ->
            b.withArrayClaim(CLAIM, new Long[] { 0L, 1L, 2L })))
            .getClaim(CLAIM));
    assertThat(claim.asList(Integer.class), contains(0, 1, 2));
  }

  @Test(expected = ClassCastException.class)
  public void testAsListOfIntsWithStrings() throws Exception {
    final DelegatingClaim claim = new DelegatingClaim(
        decodedToken(newToken(b ->
            b.withArrayClaim(CLAIM, new String[] { "a", "b", "c" })))
            .getClaim(CLAIM));
    claim.asList(Integer.class);
  }

  @Test
  public void testAsSetWithNullClaim() throws Exception {
    final DelegatingClaim claim = new DelegatingClaim(decodedToken(newToken())
        .getClaim(CLAIM));

    assertThat(claim.asList(), is(nullValue()));
  }

  @Test
  public void testAsSetOfStringWithNullClaim() throws Exception {
    final DelegatingClaim claim = new DelegatingClaim(decodedToken(newToken())
        .getClaim(CLAIM));

    assertThat(claim.asList(String.class), is(nullValue()));
  }

  @Test
  public void testAsSetWithStrings() throws Exception {
    final DelegatingClaim claim = new DelegatingClaim(
        decodedToken(newToken(b ->
            b.withArrayClaim(CLAIM, new String[] { "a", "b", "c" })))
            .getClaim(CLAIM));
    assertThat(claim.asSet(), contains("a", "b", "c"));
  }

  @Test
  public void testAsSetWithInts() throws Exception {
    final DelegatingClaim claim = new DelegatingClaim(
        decodedToken(newToken(b ->
            b.withArrayClaim(CLAIM, new Integer[] { 0, 1, 2 })))
            .getClaim(CLAIM));
    assertThat(claim.asSet(), contains(0, 1, 2));
  }

  @Test
  public void testAsSetWithLongs() throws Exception {
    final DelegatingClaim claim = new DelegatingClaim(
        decodedToken(newToken(b ->
            b.withArrayClaim(CLAIM, new Long[] { 0L, 1L, 2L })))
            .getClaim(CLAIM));
    assertThat(claim.asSet(), contains(0, 1, 2)); // the returned list will contain ints
  }

  @Test
  public void testAsSetOfStringsWithStrings() throws Exception {
    final DelegatingClaim claim = new DelegatingClaim(
        decodedToken(newToken(b ->
            b.withArrayClaim(CLAIM, new String[] { "a", "b", "c" })))
            .getClaim(CLAIM));
    assertThat(claim.asSet(String.class), contains("a", "b", "c"));
  }

  @Test
  public void testAsSetOfStringsWithInts() throws Exception {
    final DelegatingClaim claim = new DelegatingClaim(
        decodedToken(newToken(b ->
            b.withArrayClaim(CLAIM, new Integer[] { 0, 1, 2 })))
            .getClaim(CLAIM));
    assertThat(claim.asSet(String.class), contains("0", "1", "2"));
  }

  @Test
  public void testAsSetOfStringsWithLongs() throws Exception {
    final DelegatingClaim claim = new DelegatingClaim(
        decodedToken(newToken(b ->
            b.withArrayClaim(CLAIM, new Long[] { 0L, 1L, 2L })))
            .getClaim(CLAIM));
    assertThat(claim.asSet(String.class), contains("0", "1", "2"));
  }

  @Test
  public void testAsSetOfIntsWithInts() throws Exception {
    final DelegatingClaim claim = new DelegatingClaim(
        decodedToken(newToken(b ->
            b.withArrayClaim(CLAIM, new Integer[] { 0, 1, 2 })))
            .getClaim(CLAIM));
    assertThat(claim.asSet(Integer.class), contains(0, 1, 2));
  }

  @Test
  public void testAsSetOfIntsWithLongs() throws Exception {
    final DelegatingClaim claim = new DelegatingClaim(
        decodedToken(newToken(b ->
            b.withArrayClaim(CLAIM, new Long[] { 0L, 1L, 2L })))
            .getClaim(CLAIM));
    assertThat(claim.asSet(Integer.class), contains(0, 1, 2));
  }

  @Test(expected = ClassCastException.class)
  public void testAsSetOfIntsWithStrings() throws Exception {
    final DelegatingClaim claim = new DelegatingClaim(
        decodedToken(newToken(b ->
            b.withArrayClaim(CLAIM, new String[] { "a", "b", "c" })))
            .getClaim(CLAIM));
    claim.asSet(Integer.class);
  }

  @Test
  public void testAsTypeWithNullClaim() throws Exception {
    final DelegatingClaim claim = new DelegatingClaim(decodedToken(newToken())
        .getClaim(CLAIM));
    assertThat(claim.as(String.class), is(nullValue()));
  }

  @Test
  public void testAsTypeUsingObject() throws Exception {
    final DelegatingClaim claim = new DelegatingClaim(
        decodedToken(newToken(b -> b.withClaim(CLAIM, "string")))
            .getClaim(CLAIM));

    assertThat(claim.as(Object.class), is(equalTo("string")));
  }

  @Test
  public void testAsTypeUsingStringConstructor() throws Exception {
    final DelegatingClaim claim = new DelegatingClaim(
        decodedToken(newToken(b -> b.withClaim(CLAIM, "string")))
            .getClaim(CLAIM));

    assertThat(claim.as(String.class), is(equalTo("string")));
  }

  @Test
  public void testAsTypeUsingMockStringConstructor() throws Exception {
    final DelegatingClaim claim = new DelegatingClaim(
        decodedToken(newToken(b -> b.withClaim(CLAIM, "string")))
            .getClaim(CLAIM));

    assertThat(claim.as(MockStringValue.class).getValue(),
        is(equalTo("string")));
  }

  @Test
  public void testAsTypeUsingMockIntConstructor() throws Exception {
    final DelegatingClaim claim = new DelegatingClaim(
        decodedToken(newToken(b -> b.withClaim(CLAIM, 0)))
            .getClaim(CLAIM));

    assertThat(claim.as(MockIntegerValue.class).getValue(),
        is(equalTo(0)));
  }

  @Test
  public void testAsTypeUsingStaticMethod() throws Exception {
    final DelegatingClaim claim = new DelegatingClaim(
        decodedToken(newToken(b -> b.withClaim(CLAIM, Claim.Type.ARRAY.name())))
            .getClaim(CLAIM));

    assertThat(claim.as(Claim.Type.class),
        is(equalTo(Claim.Type.ARRAY)));
  }

  @Test
  public void testAsTypeUsingConverterWithNullClaim() throws Exception {
    final DelegatingClaim claim = new DelegatingClaim(decodedToken(newToken())
        .getClaim(CLAIM));
    assertThat(claim.as(Claim.Type::valueOf, String.class), is(nullValue()));
  }

  @Test
  public void testAsTypeUsingConverter() throws Exception {
    final DelegatingClaim claim = new DelegatingClaim(
        decodedToken(newToken(b -> b.withClaim(CLAIM, Claim.Type.ARRAY.name())))
            .getClaim(CLAIM));

    assertThat(claim.as(Claim.Type::valueOf, String.class),
        is(equalTo(Claim.Type.ARRAY)));
  }


  private DecodedJWT decodedToken(String token) {
    return verifier.verify(token);
  }

  private String newToken() {
    return JWT.create().withIssuer(ISSUER).sign(algorithm);
  }

  private String newToken(
      Consumer<JWTCreator.Builder> claimGenerator) {
    final JWTCreator.Builder builder = JWT.create().withIssuer(ISSUER);
    claimGenerator.accept(builder);
    return builder.sign(algorithm);
  }

  public static class MockStringValue {

    private final String value;

    public MockStringValue(String value) {
      this.value = value;
    }

    public String getValue() {
      return value;
    }
  }

  public static class MockIntegerValue {

    private final Integer value;

    public MockIntegerValue(Integer value) {
      this.value = value;
    }

    public Integer getValue() {
      return value;
    }
  }


}
