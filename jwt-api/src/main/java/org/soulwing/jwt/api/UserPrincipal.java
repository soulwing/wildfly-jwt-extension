/*
 * File created on Feb 13, 2019
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
package org.soulwing.jwt.api;

import java.io.Serializable;
import java.security.Principal;
import java.util.Map;

/**
 * A {@link java.security.Principal} with additional JWT-specific API.
 * <p>
 * The claims from the payload of the JWT token presented by the user
 * are available through this interface.
 *
 * @author Carl Harris
 */
public interface UserPrincipal extends Principal, Serializable {

  /**
   * Gets the specified claim.
   * @param name name of the the claim to retrieve
   * @return a claim object (never {@code null}; use {@link Claim#isNull}
   *    to check whether a value was specified for the given claim
   */
  Claim getClaim(String name);

  /**
   * Tests whether this principal has the specified claim.
   * <p>
   * This is effectively a convenient alternative to
   * {@link #getClaim(String)}.{@link Claim#isNull}
   * @param name name of the claim to test
   * @return {@code true} if the a claim with given name is present
   *    in this principal object
   */
  boolean hasClaim(String name);

  /**
   * Gets all available claims for this principal.
   * @return map of claims; modifying the returned map has no effect
   *    on this principal
   */
  Map<String, Claim> getClaims();

}
