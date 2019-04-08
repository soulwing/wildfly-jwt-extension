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
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;

import java.util.Properties;

import org.jboss.msc.service.ServiceController;
import org.jboss.msc.service.ServiceName;
import org.jboss.msc.service.StartContext;
import org.jboss.msc.service.StartException;
import org.jboss.msc.service.StopContext;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;
import org.soulwing.jwt.api.exceptions.JWTAssertionFailedException;
import org.soulwing.jwt.extension.service.AssertionConfiguration;
import org.soulwing.jwt.extension.spi.Assertion;
import org.soulwing.jwt.extension.spi.NoSuchServiceProviderException;
import org.soulwing.jwt.extension.spi.ServiceLocator;
import org.soulwing.jwt.extension.spi.ServiceLocatorException;

/**
 * Unit tests for {@link ClaimAssertionService}.
 *
 * @author Carl Harris
 */
public class ClaimAssertionServiceTest {

  private static final String PROVIDER = "provider";
  private static final String MODULE = "module";
  private static final Properties PROPERTIES = new Properties();
  private static final String PROPERTY_NAME = "propertyName";
  private static final String PROPERTY_VALUE = "propertyValue";
  private static final ServiceName SERVICE_NAME = ServiceName.of("test");
  private static final String PROVIDER_NAME = "providerName";

  static {
    PROPERTIES.setProperty(PROPERTY_NAME, PROPERTY_VALUE);
  }

  @Rule
  public final JUnitRuleMockery context = new JUnitRuleMockery();

  @Mock
  private StartContext startContext;

  @Mock
  private StopContext stopContext;

  @Mock
  private ServiceController<?> serviceController;

  @Mock
  private ServiceLocator serviceLocator;

  @Mock
  private Assertion assertion;

  @Test
  public void testSuccessfulBuild() throws Exception {
    final ClaimAssertionService service = serviceBuilder().build();
    assertThat(service.getProvider(), is(equalTo(PROVIDER)));
    assertThat(service.getModule(), is(equalTo(MODULE)));
    assertThat(service.getProperties().getProperty(PROPERTY_NAME),
        is(equalTo(PROPERTY_VALUE)));
    assertThat(service.getValue(), is(sameInstance(service)));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testBuildWithNoProvider() throws Exception {
    final ClaimAssertionService.Builder builder = serviceBuilder();
    builder.provider(null).build();
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

        oneOf(serviceLocator).locate(Assertion.class, PROVIDER, MODULE);
        will(returnValue(assertion));
        oneOf(assertion).initialize(PROPERTIES);
        allowing(assertion).getName();
        will(returnValue(PROVIDER_NAME));
      }
    });

    final ClaimAssertionService service = serviceBuilder().build();
    service.start(startContext);

    final AssertionConfiguration config = service.getConfiguration();
    assertThat(config, is(not(nullValue())));
    assertThat(config.getName(), is(equalTo(PROVIDER_NAME)));
    assertThat(config.getPredicate(), is(equalTo(assertion)));
    final JWTAssertionFailedException ex = config.getErrorSupplier().apply(null);
    assertThat(ex.getMessage(), containsString(PROVIDER_NAME));
    assertThat(ex.getMessage(), containsString(PROPERTY_NAME));
    assertThat(ex.getMessage(), containsString(PROPERTY_VALUE));

    service.stop(stopContext);
  }

  @Test(expected = StartException.class)
  public void testStartWhenNoSuchServiceProviderException()
      throws Exception {
    context.checking(new Expectations() {
      {
        oneOf(serviceLocator).locate(Assertion.class, PROVIDER, MODULE);
        will(throwException(new NoSuchServiceProviderException()));
      }
    });

    serviceBuilder().build().start(startContext);
  }

  @Test(expected = StartException.class)
  public void testStartWhenServiceLocatorException()
      throws Exception {
    context.checking(new Expectations() {
      {
        oneOf(serviceLocator).locate(Assertion.class, PROVIDER, MODULE);
        will(throwException(new ServiceLocatorException()));
      }
    });

    serviceBuilder().build().start(startContext);
  }


  private ClaimAssertionService.Builder serviceBuilder() {
    return ClaimAssertionService.builder()
        .serviceLocator(serviceLocator)
        .provider(PROVIDER)
        .module(MODULE)
        .properties(PROPERTIES);
  }

}