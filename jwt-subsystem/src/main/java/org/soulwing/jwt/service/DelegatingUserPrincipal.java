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
package org.soulwing.jwt.service;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.soulwing.jwt.api.Claim;
import org.soulwing.jwt.api.UserPrincipal;
import com.auth0.jwt.interfaces.DecodedJWT;

/**
 * A {@link UserPrincipal} that delegates to a decoded JWT.
 *
 * @author Carl Harris
 */
public class DelegatingUserPrincipal implements UserPrincipal {

  private final DecodedJWT delegate;

  public DelegatingUserPrincipal(DecodedJWT delegate) {
    this.delegate = delegate;
  }

  @Override
  public String getName() {
    return Optional.ofNullable(delegate.getSubject()).orElse("ANONYMOUS");
  }

  @Override
  public Claim getClaim(String name) {
    return new DelegatingClaim(delegate.getClaim(name));
  }

  @Override
  public boolean hasClaim(String name) {
    return !delegate.getClaim(name).isNull();
  }

  @Override
  public Map<String, Claim> getClaims() {
    final Map<String, com.auth0.jwt.interfaces.Claim> claims =
        delegate.getClaims();

    return claims.keySet().stream().collect(Collectors
        .toMap(k -> k, k -> new DelegatingClaim(claims.get(k))));
  }

}
