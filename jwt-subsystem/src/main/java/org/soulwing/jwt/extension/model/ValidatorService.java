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
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.jboss.msc.service.Service;
import org.jboss.msc.service.StartContext;
import org.jboss.msc.service.StopContext;
import org.soulwing.jwt.api.JWTProvider;
import org.soulwing.jwt.api.JWTProviderLocator;
import org.soulwing.jwt.extension.service.AssertionConfiguration;
import org.soulwing.jwt.extension.service.AuthenticationService;
import org.soulwing.jwt.extension.service.Authenticator;
import org.soulwing.jwt.extension.service.AuthenticatorFactory;
import org.soulwing.jwt.extension.service.Configuration;
import org.soulwing.jwt.extension.service.DefaultAuthenticatorFactory;
import org.soulwing.jwt.extension.service.EncryptionConfiguration;
import org.soulwing.jwt.extension.service.SignatureConfiguration;
import org.soulwing.jwt.extension.service.TransformConfiguration;

/**
 * A service that provides a configuration for JWT bearer token validation.
 *
 * @author Carl Harris
 */
class ValidatorService
    implements Service<ValidatorService>, AuthenticationService {

  private AuthenticatorFactory authenticatorFactory =
      DefaultAuthenticatorFactory.INSTANCE;

  private String issuer;
  private URI issuerUrl;
  private String audience;
  private long expirationTolerance;
  private Supplier<SignatureService> signatureService;
  private Supplier<EncryptionService> encryptionService;
  private List<Supplier<ClaimTransformService>> transformServices =
      new ArrayList<>();
  private List<Supplier<ClaimAssertionService>> assertionServices =
      new ArrayList<>();

  private Configuration configuration;

  private ValidatorService() {}

  static class Builder {

    private final ValidatorService service = new ValidatorService();

    private Builder() {}

    Builder issuer(String issuer) {
      service.issuer = issuer;
      return this;
    }

    Builder issuerUrl(URI issuerUrl) {
      service.issuerUrl = issuerUrl;
      return this;
    }

    Builder audience(String audience) {
      service.audience = audience;
      return this;
    }

    Builder expirationTolerance(long tolerance) {
      service.expirationTolerance = tolerance;
      return this;
    }

    Builder authenticatorFactory(AuthenticatorFactory authenticatorFactory) {
      service.authenticatorFactory = authenticatorFactory;
      return this;
    }

    ValidatorService build() {
      if (service.issuer == null) {
        throw new IllegalArgumentException("issuer is required");
      }
      if (service.issuerUrl == null) {
        throw new IllegalArgumentException("issuerUrl is required");
      }
      return service;
    }

  }

  static Builder builder() {
    return new Builder();
  }

  @Override
  public void start(StartContext startContext) {
    final JWTProvider provider = JWTProviderLocator.getProvider();
    configuration = new InnerConfiguration(provider);
    LOGGER.info(startContext.getController().getName() + " started");
  }

  @Override
  public void stop(StopContext stopContext) {
    LOGGER.info(stopContext.getController().getName() + " stopped");
  }

  @Override
  public ValidatorService getValue()
      throws IllegalStateException, IllegalArgumentException {
    return this;
  }

  String getIssuer() {
    return issuer;
  }

  URI getIssuerUrl() {
    return issuerUrl;
  }

  String getAudience() {
    return audience;
  }

  long getExpirationTolerance() {
    return expirationTolerance;
  }

  Supplier<SignatureService> getSignatureService() {
    return signatureService;
  }

  void setSignatureService(Supplier<SignatureService> signatureService) {
    this.signatureService = signatureService;
  }

  Supplier<EncryptionService> getEncryptionService() {
    return encryptionService;
  }

  void setEncryptionService(Supplier<EncryptionService> encryptionService) {
    this.encryptionService = encryptionService;
  }

  List<Supplier<ClaimTransformService>> getTransformServices() {
    return transformServices;
  }

  void setTransformServices(
      List<Supplier<ClaimTransformService>> transformServices) {
    this.transformServices.addAll(transformServices);
  }

  List<Supplier<ClaimAssertionService>> getAssertionServices() {
    return assertionServices;
  }

  void setAssertionServices(
      List<Supplier<ClaimAssertionService>> assertionServices) {
    this.assertionServices.addAll(assertionServices);
  }

  Configuration getConfiguration() {
    return configuration;
  }

  @Override
  public Authenticator newAuthenticator() throws Exception {
    return authenticatorFactory.newInstance(getConfiguration());
  }

  private class InnerConfiguration implements Configuration {

    private final JWTProvider provider;

    InnerConfiguration(JWTProvider provider) {
      this.provider = provider;
    }

    @Override
    public JWTProvider getProvider() {
      return provider;
    }

    @Override
    public String getIssuer() {
      return issuer;
    }

    @Override
    public URI getIssuerUrl() {
      return issuerUrl;
    }

    @Override
    public Duration getExpirationTolerance() {
      return Duration.ofSeconds(expirationTolerance);
    }

    @Override
    public String getAudience() {
      return audience;
    }

    @Override
    public SignatureConfiguration getSignatureConfiguration() {
      return signatureService.get().getConfiguration(issuerUrl);
    }

    @Override
    public EncryptionConfiguration getEncryptionConfiguration() {
      if (encryptionService == null) return null;
      return encryptionService.get().getConfiguration();
    }

    @Override
    public List<AssertionConfiguration> getAssertions() {
      return assertionServices.stream()
          .map(Supplier::get)
          .map(ClaimAssertionService::getConfiguration)
          .collect(Collectors.toList());
    }

    @Override
    public List<TransformConfiguration> getTransforms() {
      return transformServices.stream()
          .map(Supplier::get)
          .map(ClaimTransformService::getConfiguration)
          .collect(Collectors.toList());
    }

  }

}
