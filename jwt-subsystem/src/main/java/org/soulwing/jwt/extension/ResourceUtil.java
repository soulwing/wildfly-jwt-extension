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

import org.jboss.as.controller.descriptions.StandardResourceDescriptionResolver;

/**
 * A utility for accessing the resource bundle.
 *
 * @author Carl Harris
 */
class ResourceUtil {

  static final String RESOURCE_NAME = String.format("%s.LocalDescriptions",
      SubsystemExtension.class.getPackage().getName());

  public static StandardResourceDescriptionResolver getResolver(
      String... segments) {
    StringBuilder sb = new StringBuilder();
    sb.append(Names.SUBSYSTEM_NAME);
    for (String segment : segments) {
      sb.append('.');
      sb.append(segment);
    }

    String key = sb.toString();
    return new StandardResourceDescriptionResolver(key,
        RESOURCE_NAME, SubsystemExtension.class.getClassLoader(), true, false);
  }

}
