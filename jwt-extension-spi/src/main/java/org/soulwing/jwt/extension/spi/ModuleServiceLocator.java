/*
 * File created on Apr 7, 2019
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
package org.soulwing.jwt.extension.spi;

import java.util.ServiceLoader;
import java.util.stream.StreamSupport;

import org.jboss.modules.Module;
import org.jboss.modules.ModuleLoadException;

/**
 * A {@link ServiceLocator} implemented using JBoss Modules.
 *
 * @author Carl Harris
 */
public class ModuleServiceLocator implements ServiceLocator {

  public static final ModuleServiceLocator INSTANCE = new ModuleServiceLocator();

  private ModuleServiceLocator() {}

  @Override
  public <T extends ServiceProvider> T locate(
      Class<T> type, String provider, String module)
      throws ServiceLocatorException {

    final ServiceLoader<T> serviceLoader = getLoader(type, module);
    return StreamSupport.stream(
        serviceLoader.spliterator(), false)
        .filter(p -> p.getName().equals(provider))
        .findFirst()
        .orElseThrow(() -> new NoSuchServiceProviderException(type, provider));
  }

  public <T> ServiceLoader<T> getLoader(Class<T> type, String module)
      throws ServiceLocatorException {

    try {
      final Module serviceModule = Module.getCallerModule();
      if (serviceModule == null) {
        return ServiceLoader.load(type);
      }

      final Module providerModule = module != null ?
          serviceModule.getModule(module) : serviceModule;

      return providerModule.loadService(type);
    }
    catch (ModuleLoadException ex) {
      throw new ServiceLocatorException(ex.getMessage(), ex);
    }
  }

}
