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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import org.jboss.msc.service.Service;
import org.jboss.msc.service.StartContext;
import org.jboss.msc.service.StartException;
import org.jboss.msc.service.StopContext;
import org.soulwing.jwt.api.Transformer;
import org.soulwing.jwt.function.FunctionLoadException;

/**
 * A service that represents the collection of transformers for a named
 * claim.
 *
 * @author Carl Harris
 */
class ClaimTransformService implements Service<ClaimTransformService> {

  private final Map<String, TransformerService> transformerServices =
      new LinkedHashMap<>();

  private final String name;

  private Supplier<ProfileService> profileService;

  public ClaimTransformService(String name) {
    this.name = name;
  }

  void setProfileService(Supplier<ProfileService> profileService) {
    this.profileService = profileService;
  }

  @Override
  public ClaimTransformService getValue()
      throws IllegalStateException, IllegalArgumentException {
    return this;
  }

  @Override
  public void start(StartContext startContext) throws StartException {
    profileService.get().addClaimTransformService(name, this);
  }

  @Override
  public void stop(StopContext stopContext) {
    profileService.get().removeClaimTransformService(name, this);
  }

  void addTransformerService(String name, TransformerService service) {
    transformerServices.put(name, service);
  }

  void removeTransformerService(String name, TransformerService service) {
    transformerServices.remove(name, service);
  }

  public Transformer getTransform() throws FunctionLoadException {
    final List<Transformer> transformers = new ArrayList<>();
    for (final String key : transformerServices.keySet()) {
      transformers.add(transformerServices.get(key).getTransformer());
    }
    if (transformers.size() == 1) return transformers.get(0);
    return new ComposedTransformer(transformers);

  }

  private static class ComposedTransformer implements Transformer {

    private final List<Transformer> transformers;

    ComposedTransformer(List<Transformer> transformers) {
      this.transformers = transformers;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object transform(Object value) {
      Object t = value;
      for (final Transformer transformer : transformers) {
        t = transformer.transform(t);
      }
      return t;
    }

    @Override
    public String toString() {
      return "compose(" + transformers + ")";
    }

  }

}
