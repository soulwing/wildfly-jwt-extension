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
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.OP;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.OP_ADDR;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.SUBSYSTEM;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.jboss.as.controller.PersistentResourceXMLDescription;
import org.jboss.as.subsystem.test.AbstractSubsystemTest;
import org.jboss.dmr.ModelNode;
import org.jboss.dmr.Property;
import org.junit.Test;
import org.soulwing.jwt.api.JWE;
import org.soulwing.jwt.api.JWS;

/**
 * Unit tests for {@link JwtSubsystemParser_1_0}.
 * q
 * @author Carl Harris
 */
public class JwtSubsystemParser_1_0Test extends AbstractSubsystemTest  {

  public JwtSubsystemParser_1_0Test() {
    super(JwtExtension.SUBSYSTEM_NAME, new JwtExtension());
  }

  @Test
  public void testParse() throws Exception {

    final PersistentResourceXMLDescription parser =
        new JwtSubsystemParser_1_0().getParserDescription();
    assertThat(parser, is(not(nullValue())));

    final List<ModelNode> ops = super.parse(
        ResourceAccessor.toString("model_1_0.xml"));

    System.out.println(ops);

    final Iterator<ModelNode> i = ops.iterator();
    validateJwtResource(i.next());
    validateSecretResource(i.next());
    validateSecretKeyResource(i.next());
    validateTrustStoreResource(i.next());
    validateKeyPairStorageResource(i.next());
    validateTransformerResource(i.next());
    validateClaimTransformResource(i.next());
    validateClaimAssertionResource(i.next());
    validateSignatureResource(i.next());
    validateEncryptionResource(i.next());
    validateValidatorResource(i.next());
    assertThat(i.hasNext(), is(false));
  }

  private void validateJwtResource(ModelNode op) {
    validateIsAdd(op);
    validateAddress(op);
    assertThat(op.get(Constants.STATISTICS_ENABLED).asBoolean(), is(true));
  }

  private void validateSecretResource(ModelNode op) {
    validateIsAdd(op);
    validateAddress(op, Constants.SECRET, "secret-name");
    assertThat(op.get(Constants.PROVIDER).asString(), is(equalTo("provider-name")));
    assertThat(op.get(Constants.MODULE).asString(), is(equalTo("module-name")));
    final Property property = op.get(Constants.PROPERTIES).asPropertyList().get(0);
    assertThat(property.getName(), is(equalTo("property-name")));
    assertThat(property.getValue().asString(), is(equalTo("property-value")));
  }

  private void validateSecretKeyResource(ModelNode op) {
    validateIsAdd(op);
    validateAddress(op, Constants.SECRET_KEY, "secret-key-name");
    assertThat(op.get(Constants.ID).asString(), is(equalTo("secret-id")));
    assertThat(op.get(Constants.TYPE).asString(), is(equalTo("type-name")));
    assertThat(op.get(Constants.LENGTH).asInt(), is(equalTo(128)));
    assertThat(op.get(Constants.PROVIDER).asString(), is(equalTo("provider-name")));
    assertThat(op.get(Constants.MODULE).asString(), is(equalTo("module-name")));
    final Property property = op.get(Constants.PROPERTIES).asPropertyList().get(0);
    assertThat(property.getName(), is(equalTo("property-name")));
    assertThat(property.getValue().asString(), is(equalTo("property-value")));
  }

  private void validateTrustStoreResource(ModelNode op) {
    validateIsAdd(op);
    validateAddress(op, Constants.TRUST_STORE, "trust-store-name");
    assertThat(op.get(Constants.PATH).asString(),
        is(equalTo("path-name")));
    assertThat(op.get(Constants.RELATIVE_TO).asString(),
        is(equalTo("relative-to-name")));
    assertThat(op.get(Constants.PASSWORD_SECRET).asString(),
        is(equalTo("password-secret-name")));
    assertThat(op.get(Constants.PROVIDER).asString(), is(equalTo("provider-name")));
    assertThat(op.get(Constants.MODULE).asString(), is(equalTo("module-name")));
    final Property property = op.get(Constants.PROPERTIES).asPropertyList().get(0);
    assertThat(property.getName(), is(equalTo("property-name")));
    assertThat(property.getValue().asString(), is(equalTo("property-value")));
  }

  private void validateKeyPairStorageResource(ModelNode op) {
    validateIsAdd(op);
    validateAddress(op, Constants.SECRET, "key-pair-storage-name");
    assertThat(op.get(Constants.PROVIDER).asString(), is(equalTo("provider-name")));
    assertThat(op.get(Constants.MODULE).asString(), is(equalTo("module-name")));
    final Property property = op.get(Constants.PROPERTIES).asPropertyList().get(0);
    assertThat(property.getName(), is(equalTo("property-name")));
    assertThat(property.getValue().asString(), is(equalTo("property-value")));
  }

  private void validateTransformerResource(ModelNode op) {
    validateIsAdd(op);
    validateAddress(op, Constants.TRANSFORMER, "transformer-name");
    assertThat(op.get(Constants.PROVIDER).asString(), is(equalTo("provider-name")));
    assertThat(op.get(Constants.MODULE).asString(), is(equalTo("module-name")));
    final Property property = op.get(Constants.PROPERTIES).asPropertyList().get(0);
    assertThat(property.getName(), is(equalTo("property-name")));
    assertThat(property.getValue().asString(), is(equalTo("property-value")));
  }

  private void validateClaimTransformResource(ModelNode op) {
    validateIsAdd(op);
    validateAddress(op, Constants.CLAIM_TRANSFORM, "claim-transform-name");
    assertThat(op.get(Constants.CLAIM).asString(), is(equalTo("claim-name")));
    assertThat(op.get(Constants.TRANSFORMERS).asListOrEmpty()
        .stream().map(ModelNode::asString).collect(Collectors.toList()),
        is(equalTo(Collections.singletonList("transformer-name"))));
  }

  private void validateClaimAssertionResource(ModelNode op) {
    validateIsAdd(op);
    validateAddress(op, Constants.CLAIM_ASSERTION, "claim-assertion-name");
    assertThat(op.get(Constants.PROVIDER).asString(), is(equalTo("provider-name")));
    assertThat(op.get(Constants.MODULE).asString(), is(equalTo("module-name")));
    final Property property = op.get(Constants.PROPERTIES).asPropertyList().get(0);
    assertThat(property.getName(), is(equalTo("property-name")));
    assertThat(property.getValue().asString(), is(equalTo("property-value")));
  }

  private void validateSignatureResource(ModelNode op) {
    validateIsAdd(op);
    validateAddress(op, Constants.SIGNATURE, "signature-name");
    assertThat(op.get(Constants.ALGORITHM).asString(),
        is(equalTo(JWS.Algorithm.HS256.toToken())));
    assertThat(op.get(Constants.TRUST_STORE).asString(),
        is(equalTo("trust-store-name")));
    assertThat(op.get(Constants.SECRET_KEYS).asListOrEmpty()
        .stream().map(ModelNode::asString).collect(Collectors.toList()),
        is(equalTo(Collections.singletonList("secret-key-name"))));
    assertThat(op.get(Constants.CERT_SUBJECT_NAME).asString(),
        is(equalTo("subject-name")));
    assertThat(op.get(Constants.CHECK_CERT_EXPIRATION).asBoolean(), is(true));
    assertThat(op.get(Constants.CHECK_CERT_REVOCATION).asBoolean(), is(true));
    assertThat(op.get(Constants.CHECK_SUBJECT_CERT_ONLY).asBoolean(), is(true));
  }

  private void validateEncryptionResource(ModelNode op) {
    validateIsAdd(op);
    validateAddress(op, Constants.ENCRYPTION, "encryption-name");
    assertThat(op.get(Constants.KEY_MANAGEMENT_ALGORITHM).asString(),
        is(equalTo(JWE.KeyManagementAlgorithm.A256KW.toToken())));
    assertThat(op.get(Constants.CONTENT_ENCRYPTION_ALGORITHM).asString(),
        is(equalTo(JWE.ContentEncryptionAlgorithm.A128CBC_HS256.toToken())));
    assertThat(op.get(Constants.COMPRESSION_ALGORITHM).asString(),
        is(equalTo(JWE.CompressionAlgorithm.DEFLATE.toToken())));
    assertThat(op.get(Constants.KEY_PAIR_STORAGE).asString(),
        is(equalTo("key-pair-storage-name")));
    assertThat(op.get(Constants.SECRET_KEYS).asListOrEmpty()
            .stream().map(ModelNode::asString).collect(Collectors.toList()),
        is(equalTo(Collections.singletonList("secret-key-name"))));
  }

  private void validateValidatorResource(ModelNode op) {
    validateIsAdd(op);
    validateAddress(op, Constants.VALIDATOR, "validator-name");
    assertThat(op.get(Constants.ISSUER).asString(),
        is(equalTo("issuer-name")));
    assertThat(op.get(Constants.ISSUER_URL).asString(),
        is(equalTo("issuer-url-value")));
    assertThat(op.get(Constants.AUDIENCE).asString(),
        is(equalTo("audience-name")));
    assertThat(op.get(Constants.EXPIRATION_TOLERANCE).asLong(),
        is(equalTo(-1L)));
    assertThat(op.get(Constants.SIGNATURE).asString(),
        is(equalTo("signature-name")));
    assertThat(op.get(Constants.ENCRYPTION).asString(),
        is(equalTo("encryption-name")));
    assertThat(op.get(Constants.TRANSFORMS).asListOrEmpty()
            .stream().map(ModelNode::asString).collect(Collectors.toList()),
        is(equalTo(Collections.singletonList("transform-name"))));
    assertThat(op.get(Constants.ASSERTIONS).asListOrEmpty()
            .stream().map(ModelNode::asString).collect(Collectors.toList()),
        is(equalTo(Collections.singletonList("assertion-name"))));
  }

  private void validateIsAdd(ModelNode op) {
    validateOperation(op, "add");
  }

  private void validateOperation(ModelNode op, String name) {
    assertThat(op.get(OP).asString(), is(equalTo(name)));
  }

  private void validateAddress(ModelNode op, String... pathElements) {
    final List<Property> addr = op.get(OP_ADDR).asPropertyList();
    assertThat(addr.get(0).getName(), is(equalTo(SUBSYSTEM)));
    assertThat(addr.get(0).getValue().asString(),
        is(equalTo(JwtExtension.SUBSYSTEM_NAME)));
    for (int i = 1, max = pathElements.length / 2; i < max; i++) {
      assertThat(addr.get(i).getName(), is(equalTo(pathElements[2 * i])));
      assertThat(addr.get(i).getValue().asString(),
          is(equalTo(pathElements[2 * i + 1])));
    }
  }

}