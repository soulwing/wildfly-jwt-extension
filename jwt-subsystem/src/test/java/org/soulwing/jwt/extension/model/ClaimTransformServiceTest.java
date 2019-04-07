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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;

import java.util.Collections;
import java.util.function.Function;

import org.jboss.msc.service.ServiceController;
import org.jboss.msc.service.ServiceName;
import org.jboss.msc.service.StartContext;
import org.jboss.msc.service.StopContext;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.jmock.lib.concurrent.Synchroniser;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Rule;
import org.junit.Test;
import org.soulwing.jwt.extension.service.TransformConfiguration;

/**
 * Unit tests for {@link ClaimTransformService}.
 *
 * @author Carl Harris
 */
public class ClaimTransformServiceTest {

  private static final String CLAIM = "claim";
  private static final ServiceName SERVICE_NAME = ServiceName.of("test");

  @Rule
  public final JUnitRuleMockery context = new JUnitRuleMockery() {{
    setImposteriser(ClassImposteriser.INSTANCE);
    setThreadingPolicy(new Synchroniser());
  }};

  @Mock
  private StartContext startContext;

  @Mock
  private StopContext stopContext;

  @Mock
  private ServiceController<?> serviceController;

  @Mock
  private TransformerService transformerService;

  private Function<Object, Object> transformer = v -> v;

  @Test
  public void testSuccessfulBuild() throws Exception {
    final ClaimTransformService service = serviceBuilder().build();
    assertThat(service.getClaim(), is(equalTo(CLAIM)));
    assertThat(service.getValue(), is(sameInstance(service)));
  }

  @Test
  public void testStartAndStop() throws Exception {
    context.checking(new Expectations() {
      {
        oneOf(startContext).getController();
        will(returnValue(serviceController));
        oneOf(stopContext).getController();
        will(returnValue(serviceController));
        allowing(serviceController).getName();
        will(returnValue(SERVICE_NAME));
      }
    });

    final ClaimTransformService service = serviceBuilder().build();
    service.start(startContext);
    service.stop(stopContext);
  }

  @Test
  public void testGetConfiguration() throws Exception {
    context.checking(new Expectations() {
      {
        allowing(transformerService).getTransformer();
        will(returnValue(transformer));
      }
    });

    final ClaimTransformService service = serviceBuilder().build();
    service.setTransformerServices(Collections.singletonList(
        () -> transformerService));

    final TransformConfiguration config = service.getConfiguration();
    assertThat(config, is(not(nullValue())));
    assertThat(config.getClaimName(), is(equalTo(CLAIM)));
    assertThat(config.getTransformer().apply("value"), is(equalTo("value")));
  }


  private ClaimTransformService.Builder serviceBuilder() {
    return ClaimTransformService.builder()
        .claim(CLAIM);
  }

}