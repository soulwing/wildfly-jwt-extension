/*
 * File created on Feb 24, 2019
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
package org.soulwing.jwt.service;

/**
 * An exception thrown when an authentication attempt fails because the key
 * provided for MAC or signature verification had an invalid bit strength.
 *
 * @author Carl Harris
 */
public class InvalidKeyLengthException extends AuthenticationException {

  private static final long serialVersionUID = -4749539724370810640L;

  /**
   * Constructs a new instance.
   * @param message
   */
  public InvalidKeyLengthException(String message) {
    super(message);
  }

  /**
   * Constructs a new instance.
   * @param message
   * @param cause
   */
  public InvalidKeyLengthException(String message, Throwable cause) {
    super(message, cause);
  }

}
