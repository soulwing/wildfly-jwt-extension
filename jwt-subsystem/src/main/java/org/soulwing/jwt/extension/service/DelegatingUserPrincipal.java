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
package org.soulwing.jwt.extension.service;


import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.soulwing.jwt.api.Claims;
import org.soulwing.jwt.extension.api.Claim;
import org.soulwing.jwt.extension.api.UserPrincipal;

/**
 * A {@link UserPrincipal} that delegates to a JWT {@link Claims} instance.
 * @author Carl Harris
 */
public class DelegatingUserPrincipal implements UserPrincipal {

  private final Claims claims;
  private final Map<String, Function<Object, Object>> transformers;

  static DelegatingUserPrincipal newInstance(Claims claims,
      List<TransformConfiguration> transformers) {
   return new DelegatingUserPrincipal(claims,
        transformers.stream().collect(
            Collectors.toMap(TransformConfiguration::getClaimName,
                TransformConfiguration::getTransformer)));
  }

  private DelegatingUserPrincipal(Claims claims,
      Map<String, Function<Object, Object>> transformers) {
    this.claims = claims;
    this.transformers = transformers;
  }

  @Override
  public String getName() {
    return claims.getSubject();
  }

  @Override
  public Claim getClaim(String name) {
    final Function<Object, Object> transformer =
        Optional.ofNullable(transformers.get(name)).orElse(v -> v);
    return new ConcreteClaim(name, claims.claim(name, Object.class).orElse(null),
        transformer);
  }

  @Override
  public boolean hasClaim(String name) {
    return claims.names().contains(name);
  }

  @Override
  public Map<String, Claim> getClaims() {
    return claims.names().stream()
        .map(this::getClaim)
        .collect(Collectors.toMap(Claim::getName, claim -> claim));
  }

}
