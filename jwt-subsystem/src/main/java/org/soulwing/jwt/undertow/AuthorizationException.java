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
package org.soulwing.jwt.undertow;

/**
 * An exception thrown to indicate that authorization of an authentic user
 * has failed.
 *
 * @author Carl Harris
 */
class AuthorizationException extends Exception {

  private static final long serialVersionUID = -6641198835927858443L;

  public AuthorizationException(String message) {
    super(message);
  }
  
}
