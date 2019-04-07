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
package org.soulwing.jwt.extension.function;

import static org.soulwing.jwt.extension.function.FunctionLogger.LOGGER;

import java.util.Properties;

import org.jboss.modules.Module;
import org.jboss.modules.ModuleLoadException;
import org.soulwing.jwt.extension.spi.Configurable;

/**
 * A factory that dynamically creates instances of functions to be used
 * as extension components.
 *
 * @author Carl Harris
 */
public class FunctionFactory<F> {

  private final Class<? extends F> functionType;
  private final Package defaultPackage;
  private final String nameSuffix;

  public FunctionFactory(Class<? extends F> functionType,
      Package defaultPackage) {
    this.functionType = functionType;
    this.defaultPackage = defaultPackage;
    this.nameSuffix = functionType.getSimpleName();
  }

  @SuppressWarnings("unchecked")
  public F getFunction(String code, String moduleSpec, Properties properties)
      throws FunctionLoadException {

    if (code == null) {
      throw new FunctionLoadException("function class name is required");
    }
    Class<?> functionClass = null;
    String moduleId = moduleId(moduleSpec);
    try {
      functionClass = loadFunctionClass(code, moduleId);
      Object function = functionClass.newInstance();
      if (function instanceof Configurable) {
        ((Configurable) function).initialize(properties);
      }
      else if (!functionClass.isInstance(function)) {
        throw new IllegalArgumentException("not a " + functionType.getSimpleName());
      }
      LOGGER.debug("loaded " + functionClass);
      return (F) function;
    }
    catch (IllegalAccessException | InstantiationException ex) {
      throw new FunctionLoadException(
          "cannot instantiate function class " + functionClass, ex);
    }
    catch (ClassNotFoundException ex) {
      throw new FunctionLoadException("cannot load function class "
          + ex.getMessage() + " from module " + moduleId, ex);
    }
    catch (ModuleLoadException ex) {
      throw new FunctionLoadException("cannot load module " +
          moduleId, ex);
    }
  }

  private Class<?> loadFunctionClass(String className,
      String moduleId) throws ClassNotFoundException,
      ModuleLoadException {
    try {
      return Module.loadClassFromCallerModuleLoader(moduleId, className);
    }
    catch (ClassNotFoundException ex) {
      if (!isSimpleJavaIdentifier(className)) {
        throw ex;
      }
      return Module.loadClassFromCallerModuleLoader(moduleId,
          qualifiedFunctionName(className));
    }
  }

  private String qualifiedFunctionName(String className) {
    StringBuilder sb = new StringBuilder();
    sb.append(defaultPackage.getName());
    sb.append('.');
    sb.append(className);
    if (!className.endsWith(nameSuffix)) {
      sb.append(nameSuffix);
    }
    return sb.toString();
  }

  private String moduleId(String moduleSpec) {
    if (moduleSpec == null) {
      return Module.getCallerModule().getName();
    }
    return moduleSpec;
  }

  private boolean isSimpleJavaIdentifier(final String className) {
    assert className.length() > 0: "className must be non-empty";
    if (!Character.isJavaIdentifierStart(className.charAt(0))) {
      return false;
    }
    for (int i = 1, max = className.length(); i < max; i++) {
      if (!Character.isJavaIdentifierPart(className.charAt(i))) {
        return false;
      }
    }
    return true;
  }

}
