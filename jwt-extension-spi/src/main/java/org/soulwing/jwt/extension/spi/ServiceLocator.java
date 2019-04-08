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

/**
 * A locator for service providers.
 *
 * @author Carl Harris
 */
public interface ServiceLocator {

  /**
   * Locates a service provider.
   * @param type service provider type
   * @param provider provider name
   * @param module optional module name
   * @param <T> service provider type
   * @return service provider instance
   * @throws NoSuchServiceProviderException if there exists no service provider
   *    with the given name
   * @throws ServiceLocatorException if an error occurs in obtaining ta service
   *    provider instance
   */
  <T extends ServiceProvider> T locate(Class<T> type, String provider,
      String module) throws NoSuchServiceProviderException, ServiceLocatorException;

  /**
   * Gets a service loader for the specified provider type
   * @param type provider type
   * @param module optional module name
   * @param <T> service provider type
   * @return service loader instance
   * @throws ServiceLocatorException if an error occurs in obtaining a service
   *    loader
   */
  <T> ServiceLoader<T> getLoader(Class<T> type, String module)
      throws ServiceLocatorException;

}
