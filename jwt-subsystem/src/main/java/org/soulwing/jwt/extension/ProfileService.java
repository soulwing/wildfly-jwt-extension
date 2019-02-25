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
package org.soulwing.jwt.extension;

import static org.soulwing.jwt.extension.ExtensionLogger.LOGGER;

import java.io.IOException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.jboss.msc.service.Service;
import org.jboss.msc.service.StartContext;
import org.jboss.msc.service.StartException;
import org.jboss.msc.service.StopContext;
import org.soulwing.jwt.function.FunctionLoadException;
import org.soulwing.jwt.service.AuthenticationService;
import org.soulwing.jwt.service.Authenticator;
import org.soulwing.jwt.service.AuthenticatorFactory;
import org.soulwing.jwt.service.Configuration;

/**
 * A service that holds a {@link Configuration}.
 *
 * @author Carl Harris
 */
class ProfileService
    implements AuthenticationService, Service<ProfileService> {

  private final Map<String, SecretKeyService> secretKeyServices =
      new HashMap<>();

  private final Map<String, PublicKeyService> publicKeyServices =
      new HashMap<>();

  private final Map<String, ClaimAssertionService> claimAssertionServices =
      new HashMap<>();

  private final Map<String, ClaimTransformService> claimTransformServices =
      new HashMap<>();

  private final Lock lock = new ReentrantLock();

  private final String name;
  private final String algorithm;
  private final long clockSkewTolerance;

  private volatile Authenticator authenticator;

  public ProfileService(String name, String algorithm, long clockSkewTolerance) {
    this.name = name;
    this.algorithm = algorithm;
    this.clockSkewTolerance = clockSkewTolerance;
  }

  @Override
  public ProfileService getValue() throws IllegalStateException {
    return this;
  }

  @Override
  public void start(StartContext startContext) throws StartException {
    resetAuthenticator();
    LOGGER.info("started " + startContext.getController().getName());
  }

  @Override
  public void stop(StopContext stopContext) {
    resetAuthenticator();
    LOGGER.info("stopped " + stopContext.getController().getName());
  }

  void addSecretKeyService(String kid, SecretKeyService service) {
    resetAuthenticator();
    secretKeyServices.put(kid, service);
  }

  void removeSecretKeyService(String kid, SecretKeyService service) {
    resetAuthenticator();
    secretKeyServices.remove(kid, service);
  }

  void addPublicKeyService(String kid, PublicKeyService service) {
    resetAuthenticator();
    publicKeyServices.put(kid, service);
  }

  void removePublicKeyService(String kid, PublicKeyService service) {
    resetAuthenticator();
    publicKeyServices.remove(kid, service);
  }

  void addClaimAssertionService(String name, ClaimAssertionService service) {
    resetAuthenticator();
    claimAssertionServices.put(name, service);
  }

  void removeClaimAssertionService(String name, ClaimAssertionService service) {
    resetAuthenticator();
    claimAssertionServices.remove(name, service);
  }

  void addClaimTransformService(String name, ClaimTransformService service) {
    resetAuthenticator();
    claimTransformServices.put(name, service);
  }

  void removeClaimTransformService(String name, ClaimTransformService service) {
    resetAuthenticator();
    claimTransformServices.remove(name, service);
  }

  @Override
  public Authenticator newAuthenticator() {
    return getAuthenticator();
  }

  private Authenticator getAuthenticator() {
    if (authenticator == null) {
      lock.lock();
      try {
        if (authenticator == null) {
          final Profile profile = newProfile();
          LOGGER.info(profile);
          authenticator = AuthenticatorFactory.newInstance(profile);
        }
      }
      finally {
        lock.unlock();
      }
    }
    return authenticator;
  }

  private void resetAuthenticator() {
    if (authenticator != null) {
      lock.lock();
      try {
        authenticator = null;
      }
      finally {
        lock.unlock();
      }
    }
  }
  private Profile newProfile() {
    try {
      final Profile profile = new Profile();
      profile.setName(name);
      profile.setAlgorithm(algorithm);
      profile.setClockSkewTolerance(clockSkewTolerance);
      for (final String kid : secretKeyServices.keySet()) {
        profile.putSecretKey(kid, secretKeyServices.get(kid).getSecretKey());
      }
      for (final String kid : publicKeyServices.keySet()) {
        profile.putPublicKey(kid, publicKeyServices.get(kid).getPublicKey());
      }
      for (final String name : claimAssertionServices.keySet()) {
        profile.putClaimAssertion(name,
            claimAssertionServices.get(name).getAssertion());
      }
      for (final String name : claimTransformServices.keySet()) {
        profile.putClaimTransform(name,
            claimTransformServices.get(name).getTransform());
      }

      return profile;
    }
    catch (FunctionLoadException | InvalidKeySpecException | IOException ex) {
      throw new RuntimeException(ex.getMessage(), ex);
    }
  }

}
