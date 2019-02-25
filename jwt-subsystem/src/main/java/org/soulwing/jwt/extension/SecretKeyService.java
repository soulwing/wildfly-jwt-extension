/*
 * File created on Feb 22, 2019
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
package org.soulwing.jwt.extension;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.function.Supplier;

import org.jboss.msc.service.Service;
import org.jboss.msc.service.StartContext;
import org.jboss.msc.service.StartException;
import org.jboss.msc.service.StopContext;

/**
 * A service for a secret key component.

 * @author Carl Harris
 */
class SecretKeyService implements Service<SecretKeyService> {

  private final String kid;
  private final String secret;
  private final SecretKeyEncoding encoding;

  private Supplier<ProfileService> profileService;

  public SecretKeyService(String kid, String secret,
      SecretKeyEncoding encoding) {
    this.kid = kid;
    this.secret = secret;
    this.encoding = encoding;
  }

  void setProfileService(Supplier<ProfileService> profileService) {
    this.profileService = profileService;
  }

  @Override
  public SecretKeyService getValue() throws IllegalStateException,
      IllegalArgumentException {
    return this;
  }

  @Override
  public void start(StartContext startContext) throws StartException {
    profileService.get().addSecretKeyService(kid, this);
  }

  @Override
  public void stop(StopContext stopContext) {
    profileService.get().removeSecretKeyService(kid, this);
  }

  public byte[] getSecretKey() {
    switch (encoding) {
      case UTF8:
        return secret.getBytes(StandardCharsets.UTF_8);
      case BASE64:
        return Base64.getDecoder().decode(secret);
      default:
        throw new IllegalArgumentException("unrecognized encoding");
    }
  }

}
