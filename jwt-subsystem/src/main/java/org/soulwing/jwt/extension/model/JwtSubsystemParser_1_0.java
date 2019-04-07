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

import static org.jboss.as.controller.PersistentResourceXMLDescription.builder;

import org.jboss.as.controller.PersistentResourceXMLDescription;
import org.jboss.as.controller.PersistentResourceXMLParser;

/**
 * A parser for the JWT subsystem model, version 1.0.
 *
 * @author Carl Harris
 */
class JwtSubsystemParser_1_0 extends PersistentResourceXMLParser {

  private final PersistentResourceXMLDescription xmlDescription;

  JwtSubsystemParser_1_0() {
    this.xmlDescription = builder(
          JwtSubsystemDefinition.INSTANCE.getPathElement(),
          Namespace.VERSION_1_0.getUri())
        .addAttributes(
            JwtSubsystemDefinition.STATISTICS_ENABLED)
        .addChild(builder(SecretDefinition.INSTANCE.getPathElement())
            .addAttributes(
                ServiceProviderAttributes.PROVIDER,
                ServiceProviderAttributes.MODULE,
                ServiceProviderAttributes.PROPERTIES))
        .addChild(builder(SecretKeyDefinition.INSTANCE.getPathElement())
            .addAttributes(
                SecretKeyDefinition.ID,
                SecretKeyDefinition.TYPE,
                SecretKeyDefinition.LENGTH,
                ServiceProviderAttributes.PROVIDER,
                ServiceProviderAttributes.MODULE,
                ServiceProviderAttributes.PROPERTIES))
        .addChild(builder(TrustStoreDefinition.INSTANCE.getPathElement())
            .addAttributes(
                TrustStoreDefinition.PATH,
                TrustStoreDefinition.RELATIVE_TO,
                TrustStoreDefinition.PASSWORD_SECRET,
                ServiceProviderAttributes.PROVIDER,
                ServiceProviderAttributes.MODULE,
                ServiceProviderAttributes.PROPERTIES))
        .addChild(builder(KeyPairStorageDefinition.INSTANCE.getPathElement())
            .addAttributes(
                KeyPairStorageDefinition.PROVIDER,
                KeyPairStorageDefinition.MODULE,
                KeyPairStorageDefinition.PROPERTIES))
        .addChild(builder(TransformerDefinition.INSTANCE.getPathElement())
            .addAttributes(
                ServiceProviderAttributes.PROVIDER,
                ServiceProviderAttributes.MODULE,
                ServiceProviderAttributes.PROPERTIES))
        .addChild(builder(ClaimTransformDefinition.INSTANCE.getPathElement())
            .addAttributes(
                ClaimTransformDefinition.CLAIM,
                ClaimTransformDefinition.TRANSFORMERS))
        .addChild(builder(ClaimAssertionDefinition.INSTANCE.getPathElement())
            .addAttributes(
                ServiceProviderAttributes.PROVIDER,
                ServiceProviderAttributes.MODULE,
                ServiceProviderAttributes.PROPERTIES))
        .addChild(builder(SignatureDefinition.INSTANCE.getPathElement())
            .addAttributes(
                SignatureDefinition.ALGORITHM,
                SignatureDefinition.TRUST_STORE,
                SignatureDefinition.SECRET_KEYS,
                SignatureDefinition.CERT_SUBJECT_NAME,
                SignatureDefinition.CHECK_CERT_EXPIRATION,
                SignatureDefinition.CHECK_CERT_REVOCATION,
                SignatureDefinition.CHECK_SUBJECT_CERT_ONLY))
        .addChild(builder(EncryptionDefinition.INSTANCE.getPathElement())
            .addAttributes(
                EncryptionDefinition.KEY_MANAGEMENT_ALGORITHM,
                EncryptionDefinition.CONTENT_ENCRYPTION_ALGORITHM,
                EncryptionDefinition.COMPRESSION_ALGORITHM,
                EncryptionDefinition.KEY_PAIR_STORAGE,
                EncryptionDefinition.SECRET_KEYS))
        .addChild(builder(ValidatorDefinition.INSTANCE.getPathElement())
            .addAttributes(
                ValidatorDefinition.ISSUER,
                ValidatorDefinition.ISSUER_URL,
                ValidatorDefinition.AUDIENCE,
                ValidatorDefinition.EXPIRATION_TOLERANCE,
                ValidatorDefinition.SIGNATURE,
                ValidatorDefinition.ENCRYPTION,
                ValidatorDefinition.TRANSFORMS,
                ValidatorDefinition.ASSERTIONS))
        .build();
  }

  @Override
  public PersistentResourceXMLDescription getParserDescription() {
    return xmlDescription;
  }

}
