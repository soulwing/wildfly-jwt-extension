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
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.jboss.msc.service.Service;
import org.jboss.msc.service.StartContext;
import org.jboss.msc.service.StartException;
import org.jboss.msc.service.StopContext;
import org.soulwing.jwt.assertion.EqualsPredicate;
import org.soulwing.jwt.function.FunctionFactory;
import org.soulwing.jwt.function.FunctionLoadException;

/**
 * A service that represents a predicate resource.
 *
 * @author Carl Harris
 */
class PredicateService implements Service<PredicateService> {

  private static final FunctionFactory<Predicate> PREDICATE_FACTORY =
      new FunctionFactory<>(Predicate.class,
          EqualsPredicate.class.getPackage());

  private final String code;
  private final String module;
  private final Properties options;

  private Supplier<ClaimAssertionService> claimAssertionService;


  PredicateService(String code, String module, Properties options) {
    this.code = code;
    this.module = module;
    this.options = options;
  }

  void setClaimAssertionService(
      Supplier<ClaimAssertionService> claimAssertionService) {
    this.claimAssertionService = claimAssertionService;
  }

  @Override
  public PredicateService getValue()
      throws IllegalStateException, IllegalArgumentException {
    return this;
  }

  @Override
  public void start(StartContext startContext) throws StartException {
    claimAssertionService.get().addPredicateService(code, this);
  }

  @Override
  public void stop(StopContext stopContext) {
    claimAssertionService.get().removePredicateService(code, this);
  }

  public Predicate getPredicate() throws FunctionLoadException {
    return PREDICATE_FACTORY.getFunction(code, module, options);
  }

}
