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
package org.soulwing.jwt.extension.model;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

import org.jboss.msc.service.StartContext;
import org.jboss.msc.service.StopContext;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

/**
 * Unit tests for {@link JwtService}.
 *
 * @author Carl Harris
 */
public class JwtServiceTest {

  @Rule
  public final JUnitRuleMockery context = new JUnitRuleMockery();

  @Mock
  private StartContext startContext;

  @Mock
  private StopContext stopContext;

  @Test
  public void testSuccessfulBuild() throws Exception {
    final JwtService service = serviceBuilder().build();
    assertThat(service.isStatisticsEnabled(), is(true));
  }

  @Test
  public void testStart() throws Exception {
    serviceBuilder().build().start(startContext);
  }

  @Test
  public void testStop() throws Exception {
    serviceBuilder().build().stop(stopContext);
  }

  @Test
  public void testGetService() throws Exception {
    assertThat(serviceBuilder().build().getValue(), is(not(nullValue())));
  }

  private JwtService.Builder serviceBuilder() {
    return JwtService.builder()
          .statisticsEnabled(true);
  }

}