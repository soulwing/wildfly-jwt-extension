/*
 * File created on Apr 7, 2019
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
package org.soulwing.jwt.extension.spi.local.secret;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Static utility methods for reading input streams.
 *
 * @author Carl Harris
 */
class InputStreamHelper {

  static byte[] toByteArray(InputStream inputStream) throws IOException {
    final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    final byte[] buf = new byte[8192];
    int numRead = inputStream.read(buf);
    while (numRead != -1) {
      outputStream.write(buf, 0, numRead);
      numRead = inputStream.read(buf);
    }
    return outputStream.toByteArray();
  }

}
