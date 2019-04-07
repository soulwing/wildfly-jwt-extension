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

import org.jboss.as.controller.PathElement;

/**
 * Constants used in model definitions.
 *
 * @author Carl Harris
 */
public interface Constants {

  String ALGORITHM = "algorithm";
  String ASSERTIONS = "assertions";
  String AUDIENCE = "audience";
  String CERT_SUBJECT_NAME = "certificate-subject-name";
  String CHECK_CERT_EXPIRATION = "check-certificate-expiration";
  String CHECK_CERT_REVOCATION = "check-certificate-revocation";
  String CHECK_SUBJECT_CERT_ONLY = "check-subject-certificate-only";
  String CLAIM = "claim";
  String CLAIM_ASSERTION = "claim-assertion";
  String CLAIM_TRANSFORM = "claim-transform";
  String COMPRESSION_ALGORITHM = "compression-algorithm";
  String CONTENT_ENCRYPTION_ALGORITHM = "content-encryption-algorithm";
  String ENCRYPTION = "encryption";
  String EXPIRATION_TOLERANCE = "expiration-tolerance";
  String ID = "id";
  String ISSUER = "issuer";
  String ISSUER_URL = "issuer-url";
  String KEY_MANAGEMENT_ALGORITHM = "key-management-algorithm";
  String KEY_PAIR_STORAGE = "key-pair-storage";
  String LENGTH = "length";
  String PROVIDER = "provider";
  String MODULE = "module";
  String PASSWORD_SECRET = "password-secret";
  String PATH = "path";
  String PROPERTIES = "properties";
  String RELATIVE_TO = "relative-to";
  String STATISTICS_ENABLED = "statistics-enabled";
  String SECRET = "secret";
  String SECRET_KEY = "secret-key";
  String SECRET_KEYS = "secret-keys";
  String SIGNATURE = "signature";
  String TRANSFORMER = "transformer";
  String TRANSFORMERS = "transformers";
  String TRANSFORMS = "transforms";
  String TRUST_STORE = "trust-store";
  String TYPE = "type";
  String VALIDATOR = "validator";

  PathElement ENCRYPTION_PATH = PathElement.pathElement(ENCRYPTION);
  PathElement CLAIM_ASSERTION_PATH = PathElement.pathElement(CLAIM_ASSERTION);
  PathElement CLAIM_TRANSFORM_PATH = PathElement.pathElement(CLAIM_TRANSFORM);
  PathElement KEY_PAIR_STORAGE_PATH = PathElement.pathElement(KEY_PAIR_STORAGE);
  PathElement SECRET_PATH = PathElement.pathElement(SECRET);
  PathElement SECRET_KEY_PATH = PathElement.pathElement(SECRET_KEY);
  PathElement SIGNATURE_PATH = PathElement.pathElement(SIGNATURE);
  PathElement TRANSFORMER_PATH = PathElement.pathElement(TRANSFORMER);
  PathElement TRUST_STORE_PATH = PathElement.pathElement(TRUST_STORE);
  PathElement VALIDATOR_PATH = PathElement.pathElement(VALIDATOR);

}
