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
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.jboss.msc.service.Service;
import org.jboss.msc.service.StartContext;
import org.jboss.msc.service.StartException;
import org.jboss.msc.service.StopContext;
import org.soulwing.jwt.function.FunctionLoadException;

/**
 * A service that represents the collection of predicates to assert for a named
 * claim.
 *
 * @author Carl Harris
 */
class ClaimAssertionService implements Service<ClaimAssertionService> {

  private final Map<String, PredicateService> predicateServices =
      new LinkedHashMap<>();

  private final String name;
  private final ClaimAssertionMode mode;

  private Supplier<ProfileService> profileService;

  ClaimAssertionService(String name, ClaimAssertionMode mode) {
    this.name = name;
    this.mode = mode;
  }

  void setProfileService(Supplier<ProfileService> profileService) {
    this.profileService = profileService;
  }

  @Override
  public ClaimAssertionService getValue()
      throws IllegalStateException, IllegalArgumentException {
    return this;
  }

  @Override
  public void start(StartContext startContext) throws StartException {
    profileService.get().addClaimAssertionService(name, this);
  }

  @Override
  public void stop(StopContext stopContext) {
    profileService.get().removeClaimAssertionService(name, this);
  }

  void addPredicateService(String name, PredicateService service) {
    predicateServices.put(name, service);
  }

  void removePredicateService(String name, PredicateService service) {
    predicateServices.remove(name, service);
  }

  public Predicate getAssertion() throws FunctionLoadException {
    final List<Predicate> predicates = new ArrayList<>();
    for (final String key : predicateServices.keySet()) {
      predicates.add(predicateServices.get(key).getPredicate());
    }

    switch (mode) {
      case ALL:
        return new AllOfPredicate(predicates);
      case ANY:
        return new AnyOfPredicate(predicates);
      case NONE:
        return new NoneOfPredicate(predicates);
      default:
        throw new IllegalArgumentException("unrecognized mode");
    }
  }

  static class AllOfPredicate implements Predicate {

    private final List<Predicate> predicates;

    AllOfPredicate(List<Predicate> predicates) {
      this.predicates = predicates;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean test(Object o) {
      for (final Predicate predicate : predicates) {
        if (!predicate.test(o)) return false;
      }
      return true;
    }

    @Override
    public String toString() {
      return "allOf(" + predicates + ")";
    }
  }

  static class AnyOfPredicate implements Predicate {

    private final List<Predicate> predicates;

    AnyOfPredicate(List<Predicate> predicates) {
      this.predicates = predicates;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean test(Object o) {
      if (predicates.isEmpty()) return true;
      for (final Predicate predicate : predicates) {
        if (predicate.test(o)) return true;
      }
      return false;
    }

    @Override
    public String toString() {
      return "anyOf(" + predicates + ")";
    }
  }

  static class NoneOfPredicate implements Predicate {

    private final List<Predicate> predicates;

    NoneOfPredicate(List<Predicate> predicates) {
      this.predicates = predicates;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean test(Object o) {
      for (final Predicate predicate : predicates) {
        if (predicate.test(o)) return false;
      }
      return true;
    }

    @Override
    public String toString() {
      return "noneOf(" + predicates + ")";
    }
  }


}
