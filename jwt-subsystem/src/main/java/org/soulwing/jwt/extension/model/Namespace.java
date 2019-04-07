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

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * An enumeration of namespaces for this extension.
 *
 * @author Carl Harris
 */
public enum Namespace {

  UNKNOWN(null),
  VERSION_1_0("urn:soulwing.org:jwt:1.0");

  public static final Namespace CURRENT = VERSION_1_0;

  private static final Map<String, Namespace> map =
      Arrays.stream(values())
          .filter(namespace -> namespace.name != null)
          .collect(Collectors.toMap(n -> n.name, n -> n));

  private final String name;

  Namespace(final String name) {
    this.name = name;
  }

  public String getUri() {
    return name;
  }

  public static Namespace forUri(String uri) {
    final Namespace element = map.get(uri);
    return element == null ? UNKNOWN : element;
  }

}