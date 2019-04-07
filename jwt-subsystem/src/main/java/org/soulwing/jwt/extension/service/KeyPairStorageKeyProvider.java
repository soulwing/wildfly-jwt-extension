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
import java.util.Optional;

import org.soulwing.jwt.api.KeyInfo;
import org.soulwing.jwt.api.KeyProvider;
import org.soulwing.jwt.api.exceptions.KeyProviderException;
import org.soulwing.s2ks.KeyPairStorage;
import org.soulwing.s2ks.KeyStorageException;
import org.soulwing.s2ks.NoSuchKeyException;

/**
 * A {@link KeyProvider} that retrieves a specified key pair from a
 * {@link KeyPairStorage} instance.
 *
 * @author Carl Harris
 */
public class KeyPairStorageKeyProvider implements KeyProvider {

  private final KeyPairStorage keyPairStorage;

  KeyPairStorageKeyProvider(KeyPairStorage keyPairStorage) {
    this.keyPairStorage = keyPairStorage;
  }

  @Override
  public KeyInfo currentKey() {
    throw new UnsupportedOperationException("has no current key");
  }

  @Override
  public Optional<Key> retrieveKey(String id) throws KeyProviderException {
    try {
      return Optional.of(keyPairStorage.retrieveKeyPair(id).getPrivateKey());
    }
    catch (NoSuchKeyException ex) {
      return Optional.empty();
    }
    catch (KeyStorageException ex) {
      throw new KeyProviderException(ex);
    }
  }

}
