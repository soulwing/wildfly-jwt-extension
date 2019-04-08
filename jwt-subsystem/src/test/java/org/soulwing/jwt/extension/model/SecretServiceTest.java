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
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
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
import org.soulwing.jwt.extension.spi.NoSuchServiceProviderException;
import org.soulwing.jwt.extension.spi.Secret;
import org.soulwing.jwt.extension.spi.SecretException;
import org.soulwing.jwt.extension.spi.SecretProvider;
import org.soulwing.jwt.extension.spi.ServiceLocator;
import org.soulwing.jwt.extension.spi.ServiceLocatorException;

/**
 * Unit tests for {@link SecretService}.
 *
 * @author Carl Harris
 */
public class SecretServiceTest {

  private static final String PROVIDER = "provider";
  private static final String MODULE = "module";
  private static final Properties PROPERTIES = new Properties();
  private static final String PROPERTY_NAME = "propertyName";
  private static final String PROPERTY_VALUE = "propertyValue";
  private static final ServiceName SERVICE_NAME = ServiceName.of("test");

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
  private SecretProvider secretProvider;

  @Mock
  private Secret secret;

  @Test
  public void testSuccessfulBuild() throws Exception {
    final SecretService service = serviceBuilder().build();
    assertThat(service.getProvider(), is(equalTo(PROVIDER)));
    assertThat(service.getModule(), is(equalTo(MODULE)));
    assertThat(service.getProperties().getProperty(PROPERTY_NAME),
        is(equalTo(PROPERTY_VALUE)));
    assertThat(service.getValue(), is(sameInstance(service)));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testBuildWithNoProvider() throws Exception {
    final SecretService.Builder builder = serviceBuilder();
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

        oneOf(serviceLocator).locate(SecretProvider.class, PROVIDER, MODULE);
        will(returnValue(secretProvider));
      }
    });

    final SecretService service = serviceBuilder().build();
    service.start(startContext);

    context.checking(new Expectations() {
      {
        oneOf(secretProvider).getSecret(PROPERTIES);
        will(returnValue(secret));
      }
    });

    assertThat(service.getSecret(), is(sameInstance(secret)));
    service.stop(stopContext);
  }

  @Test(expected = StartException.class)
  public void testGetSecretWhenServiceLocatorException() throws Exception {
    context.checking(new Expectations() {
      {
        oneOf(serviceLocator).locate(SecretProvider.class, PROVIDER, MODULE);
        will(throwException(new ServiceLocatorException()));
      }
    });

    final SecretService service = serviceBuilder().build();
    service.start(startContext);

    context.checking(new Expectations() {
      {
        oneOf(secretProvider).getSecret(PROPERTIES);
        will(throwException(new SecretException("error")));
      }
    });

    service.getSecret();
  }

  @Test(expected = StartException.class)
  public void testGetSecretWhenNoSuchServiceProviderException() throws Exception {
    context.checking(new Expectations() {
      {
        oneOf(serviceLocator).locate(SecretProvider.class, PROVIDER, MODULE);
        will(throwException(new NoSuchServiceProviderException()));
      }
    });

    final SecretService service = serviceBuilder().build();
    service.start(startContext);

    context.checking(new Expectations() {
      {
        oneOf(secretProvider).getSecret(PROPERTIES);
        will(throwException(new SecretException("error")));
      }
    });

    service.getSecret();
  }

  @Test(expected = SecretException.class)
  public void testGetSecretWhenSecretException() throws Exception {
    context.checking(new Expectations() {
      {
        oneOf(startContext).getController();
        will(returnValue(serviceController));
        allowing(serviceController).getName();
        will(returnValue(SERVICE_NAME));

        oneOf(serviceLocator).locate(SecretProvider.class, PROVIDER, MODULE);
        will(returnValue(secretProvider));
      }
    });

    final SecretService service = serviceBuilder().build();
    service.start(startContext);

    context.checking(new Expectations() {
      {
        oneOf(secretProvider).getSecret(PROPERTIES);
        will(throwException(new SecretException("error")));
      }
    });

    service.getSecret();
  }

  private SecretService.Builder serviceBuilder() {
    return SecretService.builder()
        .serviceLocator(serviceLocator)
        .provider(PROVIDER)
        .module(MODULE)
        .properties(PROPERTIES);
  }

}