/*
 * File created on Apr 4, 2019
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

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import org.jboss.as.controller.ExpressionResolver;
import org.jboss.as.controller.OperationFailedException;
import org.jboss.as.controller.logging.ControllerLogger;
import org.jboss.as.controller.operations.validation.AllowedValuesValidator;
import org.jboss.as.controller.operations.validation.ModelTypeValidator;
import org.jboss.dmr.ModelNode;
import org.jboss.dmr.ModelType;
import org.soulwing.jwt.api.JWS;

/**
 * A {@link ModelTypeValidator} for the {@link JWS.Algorithm} type.
 *
 * @author Carl Harris
 */
class SignatureAlgorithmValidator extends ModelTypeValidator
    implements AllowedValuesValidator {

  private final Set<JWS.Algorithm> allowedValues;
  private final Set<String> allowedTokens;

  public static final SignatureAlgorithmValidator INSTANCE =
      new SignatureAlgorithmValidator();

  private SignatureAlgorithmValidator() {
    super(ModelType.STRING);
    this.allowedValues = Arrays.stream(JWS.Algorithm.values())
        .collect(Collectors.toCollection(LinkedHashSet::new));
    this.allowedTokens = allowedValues.stream()
        .filter(algorithm -> !algorithm.equals(JWS.Algorithm.none))
        .map(JWS.Algorithm::toToken)
        .collect(Collectors.toCollection(LinkedHashSet::new));
  }

  @Override
  public List<ModelNode> getAllowedValues() {
    return allowedTokens.stream()
        .map(ModelNode::new)
        .collect(Collectors.toList());
  }

  @Override
  public void validateParameter(String parameterName, ModelNode value)
      throws OperationFailedException {
    super.validateParameter(parameterName, value);

    final ModelType type = value.getType();
    if (type == ModelType.STRING) {
      final String token =
          ExpressionResolver.SIMPLE.resolveExpressions(value).asString();

      JWS.Algorithm algorithm;
      try {
        algorithm = JWS.Algorithm.of(token);
      }
      catch (IllegalArgumentException iex1) {
        try {
          algorithm = JWS.Algorithm.of(token.toUpperCase(Locale.ENGLISH));
        }
        catch (IllegalArgumentException iex2) {
          throw ControllerLogger.ROOT_LOGGER.invalidEnumValue(token,
              parameterName, allowedTokens);
        }
      }

      try {
        value.set(algorithm.toToken());
      }
      catch (Exception ex) {
        assert true;  // node is protected?
      }
    }

  }

}
