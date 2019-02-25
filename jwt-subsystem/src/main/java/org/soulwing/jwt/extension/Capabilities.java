/*
 * File created on Feb 22, 2019
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

/**
 * Capability names used with the service controller.
 *
 * @author Carl Harris
 */
public interface Capabilities {

  String CAPABILITY_JWT_PROFILE = "org.soulwing.jwt.profile";
  String CAPABILITY_SECRET_KEY = "org.soulwing.jwt.secret-key";
  String CAPABILITY_PUBLIC_KEY = "org.soulwing.jwt.public-key";
  String CAPABILITY_CLAIM_ASSERTION = "org.soulwing.jwt.claim-assertion";
  String CAPABILITY_CLAIM_TRANSFORM = "org.soulwing.jwt.claim-transform";
  String CAPABILITY_PREDICATE = "org.soulwing.jwt.predicate";
  String CAPABILITY_TRANSFORMER = "org.soulwing.jwt.transformer";
  String REF_PATH_MANAGER = "org.wildfly.management.path-manager";

  String PROFILE_SERVICE_ALIAS = "org.soulwing.jwt.profile-alias";

}
