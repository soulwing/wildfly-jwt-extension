/*
 * File created on Feb 22, 2019
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
package org.soulwing.jwt.extension;

import java.util.Properties;
import java.util.function.Supplier;

import org.jboss.msc.service.Service;
import org.jboss.msc.service.StartContext;
import org.jboss.msc.service.StartException;
import org.jboss.msc.service.StopContext;
import org.soulwing.jwt.api.Transformer;
import org.soulwing.jwt.function.FunctionFactory;
import org.soulwing.jwt.function.FunctionLoadException;
import org.soulwing.jwt.transformer.ReplacePatternTransformer;

/**
 * A service that represents a transformer resource.
 *
 * @author Carl Harris
 */
class TransformerService implements Service<TransformerService> {

  private static final FunctionFactory<Transformer> TRANSFORMER_FACTORY =
      new FunctionFactory<>(Transformer.class,
          ReplacePatternTransformer.class.getPackage());

  private final String code;
  private final String module;
  private final Properties options;

  TransformerService(String code, String module, Properties options) {
    this.code = code;
    this.module = module;
    this.options = options;
  }

  private Supplier<ClaimTransformService> claimTransformService;

  void setClaimTransformService(
      Supplier<ClaimTransformService> claimTransformService) {
    this.claimTransformService = claimTransformService;
  }

  @Override
  public TransformerService getValue()
      throws IllegalStateException, IllegalArgumentException {
    return this;
  }

  @Override
  public void start(StartContext startContext) throws StartException {
    claimTransformService.get().addTransformerService(code, this);
  }

  @Override
  public void stop(StopContext stopContext) {
    claimTransformService.get().removeTransformerService(code, this);
  }

  public Transformer getTransformer() throws FunctionLoadException {
    return TRANSFORMER_FACTORY.getFunction(code, module, options);
  }

}
