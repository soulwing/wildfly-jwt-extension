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
import javax.crypto.SecretKey;

import org.jboss.modules.ModuleLoadException;
import org.jboss.msc.service.Service;
import org.jboss.msc.service.StartContext;
import org.jboss.msc.service.StartException;
import org.jboss.msc.service.StopContext;
import org.soulwing.jwt.extension.service.SecretKeyConfiguration;
import org.soulwing.jwt.extension.spi.ModuleServiceLocator;
import org.soulwing.jwt.extension.spi.NoSuchServiceProviderException;
import org.soulwing.jwt.extension.spi.SecretException;
import org.soulwing.jwt.extension.spi.SecretKeyProvider;
import org.soulwing.jwt.extension.spi.ServiceLocator;

/**
 * A service that provides a configuration for a secret key.
 *
 * @author Carl Harris
 */
class SecretKeyService implements Service<SecretKeyService> {

  private ServiceLocator serviceLocator = ModuleServiceLocator.INSTANCE;

  private String id;
  private String type;
  private int length;
  private String provider;
  private String module;
  private Properties properties = new Properties();

  private SecretKeyProvider secretKeyProvider;

  private SecretKeyService() {}

  static class Builder {

    private final SecretKeyService service = new SecretKeyService();

    private Builder() {}

    Builder id(String id) {
      service.id = id;
      return this;
    }

    Builder type(String type) {
      service.type = type;
      return this;
    }

    Builder length(int length) {
      service.length = length;
      return this;
    }

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

    SecretKeyService build() {
      if (service.id == null) {
        throw new IllegalArgumentException("id is required");
      }
      if (service.type == null) {
        throw new IllegalArgumentException("type is required");
      }
      if (service.length <= 0) {
        throw new IllegalArgumentException("length must be a positive integer");
      }
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
      secretKeyProvider = serviceLocator.locate(SecretKeyProvider.class,
          provider, module);
      LOGGER.info(startContext.getController().getName() + " started");
    }
    catch (NoSuchServiceProviderException ex) {
      LOGGER.error("secret key provider " + provider + " not found"
          + (module != null ? " in module " + module : ""));
      throw new StartException(ex);
    }
    catch (ModuleLoadException ex) {
      LOGGER.error("error loading module " + getModule() + ": " +
          ex.getMessage());
      throw new StartException(ex);
    }
  }

  @Override
  public void stop(StopContext stopContext) {
    LOGGER.info(stopContext.getController().getName() + " stop");
  }

  @Override
  public SecretKeyService getValue() throws IllegalStateException,
      IllegalArgumentException {
    return this;
  }

  String getId() {
    return id;
  }

  String getType() {
    return type;
  }

  int getLength() {
    return length;
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

  SecretKeyConfiguration getSecretKey() throws SecretException {
    return new InnerConfiguration(
        secretKeyProvider.getSecretKey(type, length, properties));
  }

  private class InnerConfiguration implements SecretKeyConfiguration {

    private final SecretKey key;

    InnerConfiguration(SecretKey key) {
      this.key = key;
    }

    @Override
    public String getId() {
      return id;
    }

    @Override
    public SecretKey getSecretKey() {
      return key;
    }

  }

}
