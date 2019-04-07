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

import java.security.Key;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.soulwing.jwt.api.KeyInfo;
import org.soulwing.jwt.api.KeyProvider;

/**
 * A {@link KeyProvider} backed by a list of {@link SecretKeyConfiguration}
 * objects.
 *
 * @author Carl Harris
 */
public class ListSecretKeyProvider implements KeyProvider {

  private final List<SecretKeyConfiguration> configs = new ArrayList<>();

  public ListSecretKeyProvider(List<SecretKeyConfiguration> configs) {
    this.configs.addAll(configs);
  }

  @Override
  public KeyInfo currentKey() {
    throw new IllegalStateException("has no current key");
  }

  @Override
  public Optional<Key> retrieveKey(String id) {
    return configs.stream().filter(c -> id.equals(c.getId()))
        .findFirst()
        .map(SecretKeyConfiguration::getSecretKey);
  }

}
