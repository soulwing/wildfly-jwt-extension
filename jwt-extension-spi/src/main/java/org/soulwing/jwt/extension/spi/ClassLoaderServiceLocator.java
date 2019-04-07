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

import org.jboss.modules.ModuleLoadException;

/**
 * A {@link ServiceLocator} implemented using JBoss Modules.
 *
 * @author Carl Harris
 */
public class ClassLoaderServiceLocator implements ServiceLocator {

  public static final ClassLoaderServiceLocator INSTANCE =
      new ClassLoaderServiceLocator();

  private ClassLoaderServiceLocator() {}

  @Override
  public <T extends ServiceProvider> T locate(
      Class<T> type, String provider, String module)
      throws NoSuchServiceProviderException, ModuleLoadException {

    final ServiceLoader<T> serviceLoader = ServiceLoader.load(type);
    return StreamSupport.stream(
        serviceLoader.spliterator(), false)
        .filter(p -> p.getName().equals(provider))
        .findFirst()
        .orElseThrow(() -> new NoSuchServiceProviderException(type, provider));
  }

}
