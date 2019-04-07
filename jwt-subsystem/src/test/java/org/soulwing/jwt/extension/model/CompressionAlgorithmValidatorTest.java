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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.util.List;
import java.util.stream.Collectors;

import org.jboss.as.controller.OperationFailedException;
import org.jboss.dmr.ModelNode;
import org.junit.Test;
import org.soulwing.jwt.api.JWE;

/**
 * Unit tests for {@link CompressionAlgorithmValidator}.
 *
 * @author Carl Harris
 */
public class CompressionAlgorithmValidatorTest {

  @Test
  public void testValidAlgorithms() throws Exception {
    for (final JWE.CompressionAlgorithm algorithm : 
        JWE.CompressionAlgorithm.values()) {
      CompressionAlgorithmValidator.INSTANCE.validateParameter(
          Constants.COMPRESSION_ALGORITHM, new ModelNode(algorithm.toToken()));
      CompressionAlgorithmValidator.INSTANCE
          .validateParameter(Constants.ALGORITHM,
              new ModelNode(algorithm.toToken().toLowerCase()));
    }
  }

  @Test(expected = OperationFailedException.class)
  public void testInvalidAlgorithm() throws Exception {
    CompressionAlgorithmValidator.INSTANCE
        .validateParameter(Constants.COMPRESSION_ALGORITHM,
            new ModelNode("undefined"));
  }

  @Test
  public void testValidateProtectedNode() throws Exception {
    final ModelNode node = new ModelNode(
        JWE.CompressionAlgorithm.DEFLATE.toToken());
    node.protect();
    CompressionAlgorithmValidator.INSTANCE.validateParameter(
        Constants.ALGORITHM, node);
  }

  @Test
  public void testGetAllowedValues() throws Exception {
    final List<String> values =
        CompressionAlgorithmValidator.INSTANCE.getAllowedValues().stream()
            .map(ModelNode::asString)
            .collect(Collectors.toList());
    assertThat(values.contains(
        JWE.CompressionAlgorithm.DEFLATE.toToken()), is(true));
  }

}