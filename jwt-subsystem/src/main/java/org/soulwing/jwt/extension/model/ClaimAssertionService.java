/*
 * File created on Apr 5, 2019
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

import java.util.Properties;
import java.util.function.Function;
import java.util.function.Predicate;

import org.jboss.modules.ModuleLoadException;
import org.jboss.msc.service.Service;
import org.jboss.msc.service.StartContext;
import org.jboss.msc.service.StartException;
import org.jboss.msc.service.StopContext;
import org.soulwing.jwt.api.Claims;
import org.soulwing.jwt.api.exceptions.JWTAssertionFailedException;
import org.soulwing.jwt.extension.service.AssertionConfiguration;
import org.soulwing.jwt.extension.spi.Assertion;
import org.soulwing.jwt.extension.spi.ModuleServiceLocator;
import org.soulwing.jwt.extension.spi.NoSuchServiceProviderException;
import org.soulwing.jwt.extension.spi.ServiceLocator;

/**
 * A service that provides a predicate to assert about the claims given in
 * a JWT.
 *
 * @author Carl Harris
 */
class ClaimAssertionService implements Service<ClaimAssertionService> {

  private ServiceLocator serviceLocator = ModuleServiceLocator.INSTANCE;

  private String provider;
  private String module;
  private Properties properties = new Properties();

  private AssertionConfiguration configuration;

  private ClaimAssertionService() {}

  static class Builder {

    private final ClaimAssertionService service = new ClaimAssertionService();

    private Builder() {}

    Builder provider(String provider) {
      service.provider = provider;
      return this;
    }

    Builder module(String module) {
      service.module = module;
      return this;
    }

    Builder properties(Properties properties) {
      service.properties.putAll(properties);
      return this;
    }

    Builder serviceLocator(ServiceLocator serviceLocator) {
      service.serviceLocator = serviceLocator;
      return this;
    }

    ClaimAssertionService build() {
      if (service.provider == null) {
        throw new IllegalArgumentException("provider is required");
      }
      return service;
    }

  }

  static Builder builder() {
    return new Builder();
  }

  @Override
  public void start(StartContext startContext) throws StartException {
    try {
      final Assertion assertion =
          serviceLocator.locate(Assertion.class, provider, module);
      assertion.initialize(properties);
      configuration = new InnerConfiguration(assertion, properties);
      LOGGER.info(startContext.getController().getName() + " started");
    }
    catch (NoSuchServiceProviderException ex) {
      LOGGER.error("assertion provider " + provider + " not found"
          + (module != null ? " in module " + module : ""));
      throw new StartException(ex);
    }
    catch (ModuleLoadException ex) {
      LOGGER.error("error loading module " + module + ": " +
          ex.getMessage());
      throw new StartException(ex);
    }
  }

  @Override
  public void stop(StopContext stopContext) {
    LOGGER.info(stopContext.getController().getName() + " stop");
  }

  @Override
  public ClaimAssertionService getValue() throws IllegalStateException,
      IllegalArgumentException {
    return this;
  }

  String getProvider() {
    return provider;
  }

  String getModule() {
    return module;
  }

  Properties getProperties() {
    return properties;
  }

  AssertionConfiguration getConfiguration() {
    return configuration;
  }

  private static class InnerConfiguration implements AssertionConfiguration {

    private final Assertion assertion;
    private final Properties properties;

    InnerConfiguration(Assertion assertion, Properties properties) {
      this.assertion = assertion;
      this.properties = properties;
    }

    @Override
    public String getName() {
      return assertion.getName();
    }

    @Override
    public Predicate<Claims> getPredicate() {
      return assertion;
    }

    @Override
    public Function<Claims, JWTAssertionFailedException> getErrorSupplier() {
      return (claims) -> new JWTAssertionFailedException("assertion "
          + assertion.getName() + " failed" + "; properties=" + properties);
    }

  }

}
