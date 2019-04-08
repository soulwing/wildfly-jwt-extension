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

import java.io.FileNotFoundException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Supplier;

import org.jboss.as.controller.services.path.PathManager;
import org.jboss.msc.service.Service;
import org.jboss.msc.service.StartContext;
import org.jboss.msc.service.StartException;
import org.jboss.msc.service.StopContext;
import org.soulwing.jwt.extension.spi.ModuleServiceLocator;
import org.soulwing.jwt.extension.spi.NoSuchServiceProviderException;
import org.soulwing.jwt.extension.spi.Secret;
import org.soulwing.jwt.extension.spi.SecretException;
import org.soulwing.jwt.extension.spi.ServiceLocator;
import org.soulwing.jwt.extension.spi.ServiceLocatorException;
import org.soulwing.jwt.extension.spi.TrustStoreProvider;

/**
 * A service for obtaining a trust store containing trusted CA certificates.
 *
 * @author Carl Harris
 */
class TrustStoreService implements Service<TrustStoreService> {

  private ServiceLocator serviceLocator = ModuleServiceLocator.INSTANCE;

  private String path;
  private String relativeTo;
  private String provider;
  private String module;
  private Properties properties = new Properties();

  private Supplier<SecretService> passwordSecretService;
  private Supplier<PathManager> pathManager;

  private KeyStore trustStore;

  private TrustStoreService() {}

  static class Builder {

    private final TrustStoreService service = new TrustStoreService();

    private Builder() {}

    Builder path(String path) {
      service.path = path;
      return this;
    }

    Builder relativeTo(String relativeTo) {
      service.relativeTo = relativeTo;
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

    TrustStoreService build() {
      if (service.path == null) {
        throw new IllegalArgumentException("path is required");
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
    final String resolvedPath = Optional.ofNullable(relativeTo)
        .map(p -> pathManager.get().resolveRelativePathEntry(path, p))
        .orElse(path);

    try {
      final TrustStoreProvider trustStoreProvider = serviceLocator.locate(
          TrustStoreProvider.class, provider, module);

      final Secret secret = Optional.ofNullable(passwordSecretService)
          .map(Supplier::get).map(SecretService::getSecret).orElse(null);

      trustStore =
          trustStoreProvider.getTrustStore(resolvedPath, secret, properties);

      if (secret != null) {
        secret.destroy();
      }
      
      LOGGER.debug("loaded trust store at path " + resolvedPath
          + " using provider " + provider);

      LOGGER.debug(startContext.getController().getName() + " started");
    }
    catch (SecretException ex) {
      LOGGER.error("error retrieving password secret " + ex.getMessage());
      throw new StartException(ex);
    }
    catch (FileNotFoundException ex) {
      LOGGER.error("trust store file not found at path " + resolvedPath);
      throw new StartException(ex);
    }
    catch (KeyStoreException ex) {
      LOGGER.error("error loading trust store: " + ex.getMessage());
      throw new StartException(ex);
    }
    catch (NoSuchServiceProviderException ex) {
      LOGGER.error("trust store provider " + provider + " not found"
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
    LOGGER.debug(stopContext.getController().getName() + " stopped");
  }

  @Override
  public TrustStoreService getValue()
      throws IllegalStateException, IllegalArgumentException {
    return this;
  }

  String getPath() {
    return path;
  }

  String getRelativeTo() {
    return relativeTo;
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

  Supplier<SecretService> getPasswordSecretService() {
    return passwordSecretService;
  }

  void setPasswordSecretService(Supplier<SecretService> passwordSecretService) {
    this.passwordSecretService = passwordSecretService;
  }

  Supplier<PathManager> getPathManager() {
    return pathManager;
  }

  void setPathManager(Supplier<PathManager> pathManager) {
    this.pathManager = pathManager;
  }

  KeyStore getTrustStore() {
    return trustStore;
  }

}
