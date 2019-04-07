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

/**
 * Capability names used with the service controller.
 *
 * @author Carl Harris
 */
public interface Capabilities {

  String CAPABILITY_JWT = "org.soulwing.jwt";

  String CAPABILITY_CLAIM_ASSERTION = CAPABILITY_JWT + ".claim-assertion";
  String CAPABILITY_CLAIM_TRANSFORM = CAPABILITY_JWT + ".claim-transform";
  String CAPABILITY_ENCRYPTION = CAPABILITY_JWT + ".encryption";
  String CAPABILITY_KEY_PAIR_STORAGE = CAPABILITY_JWT + ".key-pair-storage";
  String CAPABILITY_SECRET = CAPABILITY_JWT + ".secret";
  String CAPABILITY_SECRET_KEY = CAPABILITY_JWT + ".secret-key";
  String CAPABILITY_SIGNATURE = CAPABILITY_JWT + ".signature";
  String CAPABILITY_TRANSFORMER = CAPABILITY_JWT + ".transformer";
  String CAPABILITY_TRUST_STORE = CAPABILITY_JWT + ".trust-store";
  String CAPABILITY_VALIDATOR = CAPABILITY_JWT + ".validator";

  String REF_PATH_MANAGER = "org.wildfly.management.path-manager";

}
