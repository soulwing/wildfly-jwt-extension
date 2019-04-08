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

import org.jboss.msc.service.Service;
import org.jboss.msc.service.StartContext;
import org.jboss.msc.service.StartException;
import org.jboss.msc.service.StopContext;
import org.soulwing.jwt.extension.spi.ModuleServiceLocator;
import org.soulwing.jwt.extension.spi.NoSuchServiceProviderException;
import org.soulwing.jwt.extension.spi.ServiceLocator;
import org.soulwing.jwt.extension.spi.ServiceLocatorException;
import org.soulwing.jwt.extension.spi.Transformer;

/**
 * A model for obtaining a transformer function from a provider.
 *
 * @author Carl Harris
 */
class TransformerService implements Service<TransformerService> {

  private ServiceLocator serviceLocator = ModuleServiceLocator.INSTANCE;

  private String provider;
  private String module;
  private Properties properties = new Properties();

  private Transformer<Object, Object> transformer;

  private TransformerService() {}

  static class Builder {

    private final TransformerService service = new TransformerService();

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

    TransformerService build() {
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
  @SuppressWarnings("unchecked")
  public void start(StartContext startContext) throws StartException {
    try {
      transformer = (Transformer<Object, Object>)
          serviceLocator.locate(Transformer.class, provider, module);
      transformer.initialize(getProperties());
      LOGGER.debug(startContext.getController().getName() + " started");
    }
    catch (NoSuchServiceProviderException ex) {
      LOGGER.error("transformer provider " + provider + " not found"
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
  public TransformerService getValue() throws IllegalStateException,
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

  Function<Object, Object> getTransformer() {
    return transformer;
  }

}
