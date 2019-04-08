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

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.jboss.msc.service.Service;
import org.jboss.msc.service.StartContext;
import org.jboss.msc.service.StartException;
import org.jboss.msc.service.StopContext;
import org.soulwing.jwt.extension.service.TransformConfiguration;
import org.soulwing.jwt.extension.transformer.TransformerSequence;

/**
 * A service that provides a transform function for values of a named JWT
 * claim.
 *
 * @author Carl Harris
 */
class ClaimTransformService implements Service<ClaimTransformService> {

  private String claim;

  private List<Supplier<TransformerService>> transformerServices =
      new ArrayList<>();

  private ClaimTransformService() {}

  static class Builder {

    private ClaimTransformService service = new ClaimTransformService();

    private Builder() {}

    Builder claim(String claim) {
      service.claim = claim;
      return this;
    }

    ClaimTransformService build() {
      return service;
    }

  }

  static Builder builder() {
    return new Builder();
  }

  @Override
  public void start(StartContext startContext) throws StartException {
    LOGGER.debug(startContext.getController().getName() + " started");
  }

  @Override
  public void stop(StopContext stopContext) {
    LOGGER.debug(stopContext.getController().getName() + " stop");
  }

  @Override
  public ClaimTransformService getValue()
      throws IllegalStateException, IllegalArgumentException {
    return this;
  }

  String getClaim() {
    return claim;
  }

  List<Supplier<TransformerService>> getTransformerServices() {
    return transformerServices;
  }

  void setTransformerServices(
      List<Supplier<TransformerService>> transformerServices) {
    this.transformerServices.addAll(transformerServices);
  }

  TransformConfiguration getConfiguration() {
    return new InnerConfiguration(claim,
        transformerServices.stream()
            .map(Supplier::get)
            .map(TransformerService::getTransformer)
            .collect(Collectors.toList()));
  }

  private static class InnerConfiguration implements TransformConfiguration {

    private final String claim;
    private final Function<Object, Object> transformer;

    InnerConfiguration(String claim, List<Function<?, ?>> transformers) {
      this.claim = claim;
      this.transformer = new TransformerSequence(transformers);
    }

    @Override
    public String getClaimName() {
      return claim;
    }

    @Override
    public Function<Object, Object> getTransformer() {
      return transformer;
    }

  }

}
