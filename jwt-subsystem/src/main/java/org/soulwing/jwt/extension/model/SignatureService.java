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

import java.net.URI;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.jboss.msc.service.Service;
import org.jboss.msc.service.StartContext;
import org.jboss.msc.service.StartException;
import org.jboss.msc.service.StopContext;
import org.soulwing.jwt.api.JWS;
import org.soulwing.jwt.extension.service.SecretKeyConfiguration;
import org.soulwing.jwt.extension.service.SignatureConfiguration;

/**
 * A service that provides a configuration for JWT signature verification.
 *
 * @author Carl Harris
 */
class SignatureService implements Service<SignatureService> {

  private JWS.Algorithm algorithm;
  private String certificateSubjectName;
  private boolean checkCertificateExpiration;
  private boolean checkCertificateRevocation;
  private boolean checkSubjectCertificateOnly;

  private Supplier<TrustStoreService> trustStoreService;
  private List<Supplier<SecretKeyService>> secretKeyServices = new ArrayList<>();

  private SignatureService() {}

  static class Builder {

    private final SignatureService service = new SignatureService();

    private Builder() {}

    Builder algorithm(JWS.Algorithm algorithm) {
      service.algorithm = algorithm;
      return this;
    }

    Builder certificateSubjectName(String subjectName) {
      service.certificateSubjectName = subjectName;
      return this;
    }

    Builder checkCertificateExpiration(boolean state) {
      service.checkCertificateExpiration = state;
      return this;
    }

    Builder checkCertificateRevocation(boolean state) {
      service.checkCertificateRevocation = state;
      return this;
    }

    Builder checkSubjectCertificateOnly(boolean state) {
      service.checkSubjectCertificateOnly = state;
      return this;
    }

    SignatureService build() {
      if (service.algorithm == null) {
        throw new IllegalArgumentException("algorithm is required");
      }
      return service;
    }

  }

  static Builder builder() {
    return new Builder();
  }

  @Override
  public void start(StartContext startContext) throws StartException {
    LOGGER.debug(startContext.getController().getName() + " started");
  }

  @Override
  public void stop(StopContext stopContext) {
    LOGGER.debug(stopContext.getController().getName() + " stopped");
  }

  @Override
  public SignatureService getValue()
      throws IllegalStateException, IllegalArgumentException {
    return this;
  }

  JWS.Algorithm getAlgorithm() {
    return algorithm;
  }

  String getCertificateSubjectName() {
    return certificateSubjectName;
  }

  boolean isCheckCertificateExpiration() {
    return checkCertificateExpiration;
  }

  boolean isCheckCertificateRevocation() {
    return checkCertificateRevocation;
  }

  boolean isCheckSubjectCertificateOnly() {
    return checkSubjectCertificateOnly;
  }

  Supplier<TrustStoreService> getTrustStoreService() {
    return trustStoreService;
  }

  void setTrustStoreService(Supplier<TrustStoreService> trustStoreService) {
    this.trustStoreService = trustStoreService;
  }

  List<Supplier<SecretKeyService>> getSecretKeyServices() {
    return secretKeyServices;
  }

  void setSecretKeyServices(List<Supplier<SecretKeyService>> secretKeyServices) {
    this.secretKeyServices.addAll(secretKeyServices);
  }

  SignatureConfiguration getConfiguration(URI issuerUrl) {
    return new InnerSignatureConfiguration(issuerUrl);
  }

  private class InnerSignatureConfiguration implements SignatureConfiguration {

    private final URI issuerUrl;

    public InnerSignatureConfiguration(URI issuerUrl) {
      this.issuerUrl = issuerUrl;
    }

    @Override
    public JWS.Algorithm getAlgorithm() {
      return algorithm;
    }

    @Override
    public URI getIssuerUrl() {
      return issuerUrl;
    }

    @Override
    public String getCertificateSubjectName() {
      return certificateSubjectName;
    }

    @Override
    public boolean isCheckCertificateExpiration() {
      return checkCertificateExpiration;
    }

    @Override
    public boolean isCheckCertificateRevocation() {
      return checkCertificateRevocation;
    }

    @Override
    public boolean isCheckSubjectCertificateOnly() {
      return checkSubjectCertificateOnly;
    }

    @Override
    public List<SecretKeyConfiguration> getSecretKeys() {
      return secretKeyServices.stream()
          .map(Supplier::get)
          .map(SecretKeyService::getSecretKey)
          .collect(Collectors.toList());
    }

    @Override
    public KeyStore getTrustStore() {
      return trustStoreService.get().getTrustStore();
    }

  }
}
