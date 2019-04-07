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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;

import org.apache.commons.io.IOUtils;

/**
 * Utility methods for accessing class loader resources.
 *
 * @author Carl Harris
 */
@SuppressWarnings("SameParameterValue")
class ResourceAccessor {

  static URL getResource(String name) throws FileNotFoundException {
    final URL url = ResourceAccessor.class.getClassLoader().getResource(name);
    if (url == null) throw new FileNotFoundException(name);
    return url;
  }

  static String toString(String name) throws IOException {
    return IOUtils.toString(ResourceAccessor.getResource(name));
  }

}
