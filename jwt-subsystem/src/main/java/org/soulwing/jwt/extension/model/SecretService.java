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
package org.soulwing.jwt.extension.model;

import static org.soulwing.jwt.extension.model.ExtensionLogger.LOGGER;

import java.util.Properties;

import org.jboss.msc.service.Service;
import org.jboss.msc.service.StartContext;
import org.jboss.msc.service.StartException;
import org.jboss.msc.service.StopContext;
import org.soulwing.jwt.extension.spi.ModuleServiceLocator;
import org.soulwing.jwt.extension.spi.NoSuchServiceProviderException;
import org.soulwing.jwt.extension.spi.Secret;
import org.soulwing.jwt.extension.spi.SecretException;
import org.soulwing.jwt.extension.spi.SecretProvider;
import org.soulwing.jwt.extension.spi.ServiceLocator;
import org.soulwing.jwt.extension.spi.ServiceLocatorException;

/**
 * A model for obtaining a secret (e.g. a password) from a provider.
 *
 * @author Carl Harris
 */
class SecretService implements Service<SecretService> {

  private ServiceLocator serviceLocator = ModuleServiceLocator.INSTANCE;

  private String provider;
  private String module;
  private Properties properties = new Properties();

  private SecretProvider secretProvider;

  private SecretService() {}

  static class Builder {

    private final SecretService service = new SecretService();

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

    SecretService build() {
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
      secretProvider =
          serviceLocator.locate(SecretProvider.class, provider, module);
      LOGGER.debug(startContext.getController().getName() + " started");
    }
    catch (NoSuchServiceProviderException ex) {
      LOGGER.error("secret provider " + provider + " not found"
          + (module != null ? " in module " + module : ""));
      throw new StartException(ex);
    }
    catch (ServiceLocatorException ex) {
      LOGGER.error("error loading module " + module + ": " +
          ex.getMessage());
      throw new StartException(ex);
    }
  }

  @Override
  public void stop(StopContext stopContext) {
    LOGGER.debug(stopContext.getController().getName() + " stop");
  }

  @Override
  public SecretService getValue() throws IllegalStateException,
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

  Secret getSecret() throws SecretException {
    return secretProvider.getSecret(properties);
  }

}
