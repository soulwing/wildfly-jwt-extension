/*
 * File created on Apr 4, 2019
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
package org.soulwing.jwt.extension.model;

import static org.soulwing.jwt.extension.model.ExtensionLogger.LOGGER;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.jboss.msc.service.Service;
import org.jboss.msc.service.StartContext;
import org.jboss.msc.service.StartException;
import org.jboss.msc.service.StopContext;
import org.soulwing.jwt.api.JWE;
import org.soulwing.jwt.extension.service.EncryptionConfiguration;
import org.soulwing.jwt.extension.service.SecretKeyConfiguration;
import org.soulwing.s2ks.KeyPairStorage;

/**
 * A service that provides a configuration for JWT payload decryption.
 *
 * @author Carl Harris
 */
class EncryptionService implements Service<EncryptionService> {

  private JWE.KeyManagementAlgorithm keyManagementAlgorithm;
  private JWE.ContentEncryptionAlgorithm contentEncryptionAlgorithm;
  private JWE.CompressionAlgorithm compressionAlgorithm;

  private Supplier<KeyPairStorageService> keyPairStorageService;

  private List<Supplier<SecretKeyService>> secretKeyServices = new ArrayList<>();

  private EncryptionService() {}

  static class Builder {

    private final EncryptionService service = new EncryptionService();

    private Builder() {}

    Builder keyManagementAlgorithm(JWE.KeyManagementAlgorithm algorithm) {
      service.keyManagementAlgorithm = algorithm;
      return this;
    }

    Builder contentEncryptionAlgorithm(
        JWE.ContentEncryptionAlgorithm algorithm) {
      service.contentEncryptionAlgorithm = algorithm;
      return this;
    }

    Builder compressionAlgorithm(JWE.CompressionAlgorithm algorithm) {
      service.compressionAlgorithm = algorithm;
      return this;
    }

    EncryptionService build() {
      if (service.keyManagementAlgorithm == null) {
        throw new IllegalArgumentException("keyManagementAlgorithm is required");
      }
      if (service.contentEncryptionAlgorithm == null) {
        throw new IllegalArgumentException("contentEncryptionAlgorithm is required");
      }
      return service;
    }

  }

  static EncryptionService.Builder builder() {
    return new EncryptionService.Builder();
  }

  @Override
  public void start(StartContext startContext) throws StartException {
    LOGGER.info(startContext.getController().getName() + " started");
  }

  @Override
  public void stop(StopContext stopContext) {
    LOGGER.info(stopContext.getController().getName() + " stopped");
  }

  @Override
  public EncryptionService getValue()
      throws IllegalStateException, IllegalArgumentException {
    return this;
  }

  JWE.KeyManagementAlgorithm getKeyManagementAlgorithm() {
    return keyManagementAlgorithm;
  }

  JWE.ContentEncryptionAlgorithm getContentEncryptionAlgorithm() {
    return contentEncryptionAlgorithm;
  }

  JWE.CompressionAlgorithm getCompressionAlgorithm() {
    return compressionAlgorithm;
  }

  Supplier<KeyPairStorageService> getKeyPairStorageService() {
    return keyPairStorageService;
  }

  void setKeyPairStorageService(
      Supplier<KeyPairStorageService> keyPairStorageService) {
    this.keyPairStorageService = keyPairStorageService;
  }

  List<Supplier<SecretKeyService>> getSecretKeyServices() {
    return secretKeyServices;
  }

  void setSecretKeyServices(
      List<Supplier<SecretKeyService>> secretKeyServices) {
    this.secretKeyServices = secretKeyServices;
  }

  EncryptionConfiguration getConfiguration() {
    return new InnerConfiguration();
  }

  private class InnerConfiguration implements EncryptionConfiguration {

    @Override
    public JWE.KeyManagementAlgorithm getKeyManagementAlgorithm() {
      return keyManagementAlgorithm;
    }

    @Override
    public JWE.ContentEncryptionAlgorithm getContentEncryptionAlgorithm() {
      return contentEncryptionAlgorithm;
    }

    @Override
    public JWE.CompressionAlgorithm getCompressionAlgorithm() {
      return compressionAlgorithm;
    }

    @Override
    public KeyPairStorage getKeyPairStorage() {
      return keyPairStorageService.get().getKeyPairStorage();
    }

    @Override
    public List<SecretKeyConfiguration> getSecretKeys() {
      return secretKeyServices.stream()
          .map(Supplier::get)
          .map(SecretKeyService::getSecretKey)
          .collect(Collectors.toList());
    }
  }

}
