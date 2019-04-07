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

import org.jboss.modules.ModuleLoadException;
import org.jboss.msc.service.Service;
import org.jboss.msc.service.StartContext;
import org.jboss.msc.service.StartException;
import org.jboss.msc.service.StopContext;
import org.soulwing.jwt.extension.spi.ModuleServiceLocator;
import org.soulwing.jwt.extension.spi.NoSuchServiceProviderException;
import org.soulwing.jwt.extension.spi.ServiceLocator;
import org.soulwing.jwt.extension.spi.ServiceProvider;
import org.soulwing.s2ks.KeyPairStorage;

/**
 * A service for obtaining a key pair storage instance.
 *
 * @author Carl Harris
 */
class KeyPairStorageService implements Service<KeyPairStorageService> {

  private ServiceLocator serviceLocator = ModuleServiceLocator.INSTANCE;

  private String provider;
  private String module;
  private Properties properties = new Properties();

  private KeyPairStorage storage;

  private KeyPairStorageService() {}

  static class Builder {

    private KeyPairStorageService service = new KeyPairStorageService();

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

    KeyPairStorageService build() {
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
      storage = serviceLocator.locate(Provider.class, provider,
          module).getInstance(properties);
      LOGGER.info(startContext.getController().getName() + " started");
    }
    catch (NoSuchServiceProviderException ex) {
      LOGGER.error("key storage provider " + provider + " not found"
          + (module != null ? " in module " + module : ""));
      throw new StartException(ex);
    }
    catch (ModuleLoadException ex) {
      LOGGER.error("error loading module " + module + ": " +
          ex.getMessage());
      throw new StartException(ex);
    }
    catch (Exception ex) {
      LOGGER.error("error obtaining key storage instance: " + ex.getMessage());
      throw new StartException(ex);
    }
  }

  @Override
  public void stop(StopContext stopContext) {
    LOGGER.info(stopContext.getController().getName() + " stop");
  }

  @Override
  public KeyPairStorageService getValue() throws IllegalStateException,
      IllegalArgumentException {
    return this;
  }

  public String getProvider() {
    return provider;
  }

  public String getModule() {
    return module;
  }

  public Properties getProperties() {
    return properties;
  }

  KeyPairStorage getKeyPairStorage() {
    return storage;
  }

  interface Provider
      extends org.soulwing.s2ks.spi.KeyPairStorageProvider, ServiceProvider {
  }

}
