/*
 * File created on Feb 19, 2019
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
 * Configuration names.
 *
 * @author Carl Harris
 */
public interface Names {

  String SUBSYSTEM_NAME = "jwt";
  
  int VERSION_MAJOR = 1;

  int VERSION_MINOR = 0;

  String NAMESPACE = String.format("urn:soulwing.org:%s:%d.%d",
      SUBSYSTEM_NAME, VERSION_MAJOR, VERSION_MINOR);  

  String NAME = "name";
  String URL = "url";

  String PROFILE = "profile";
  String ALGORITHM = "algorithm";
  String CLOCK_SKEW_TOLERANCE = "clock-skew-tolerance";

  String SECRET_KEY = "secret-key";
  String KID = "kid";
  String SECRET = "secret";
  String ENCODING = "encoding";

  String PUBLIC_KEY = "public-key";
  String TYPE = "type";
  String FORMAT = "format";
  String PATH = "path";
  String RELATIVE_TO = "relative-to";

  String CLAIM_ASSERTION = "claim-assertion";
  String PREDICATE = "predicate";
  String MODE = "mode";

  String CLAIM_TRANSFORM = "claim-transform";
  String TRANSFORMER = "transformer";

  String CODE = "code";
  String MODULE = "module";
  String OPTIONS = "options";
  String OPTION = "option";
  String KEY = "key";
  
  String ADD_API_DEPENDENCIES = "add-api-dependencies";

}
