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
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;

import java.util.Properties;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.jboss.modules.ModuleLoadException;
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
import org.soulwing.jwt.extension.spi.SecretException;
import org.soulwing.jwt.extension.spi.SecretKeyProvider;
import org.soulwing.jwt.extension.spi.ServiceLocator;

/**
 * Unit tests for {@link SecretService}.
 *
 * @author Carl Harris
 */
public class SecretKeyServiceTest {

  private static final String ID = "id";
  private static final String TYPE = "type";
  private static final int LENGTH = 128;
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
  private SecretKeyProvider secretKeyProvider;

  private SecretKey secretKey = new SecretKeySpec(new byte[16], "AES");

  @Test
  public void testSuccessfulBuild() throws Exception {
    final SecretKeyService service = serviceBuilder().build();
    assertThat(service.getId(), is(equalTo(ID)));
    assertThat(service.getType(), is(equalTo(TYPE)));
    assertThat(service.getLength(), is(equalTo(LENGTH)));
    assertThat(service.getProvider(), is(equalTo(PROVIDER)));
    assertThat(service.getModule(), is(equalTo(MODULE)));
    assertThat(service.getProperties().getProperty(PROPERTY_NAME),
        is(equalTo(PROPERTY_VALUE)));
    assertThat(service.getValue(), is(sameInstance(service)));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testBuildWithNoId() throws Exception {
    final SecretKeyService.Builder builder = serviceBuilder();
    builder.id(null).build();
  }

  @Test(expected = IllegalArgumentException.class)
  public void testBuildWithNoType() throws Exception {
    final SecretKeyService.Builder builder = serviceBuilder();
    builder.type(null).build();
  }

  @Test(expected = IllegalArgumentException.class)
  public void testBuildWithNonPositiveLength() throws Exception {
    final SecretKeyService.Builder builder = serviceBuilder();
    builder.length(0).build();
  }

  @Test(expected = IllegalArgumentException.class)
  public void testBuildWithNoProvider() throws Exception {
    final SecretKeyService.Builder builder = serviceBuilder();
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

        oneOf(serviceLocator).locate(SecretKeyProvider.class, PROVIDER, MODULE);
        will(returnValue(secretKeyProvider));
      }
    });

    final SecretKeyService service = serviceBuilder().build();
    service.start(startContext);

    context.checking(new Expectations() {
      {
        oneOf(secretKeyProvider).getSecretKey(TYPE, LENGTH, PROPERTIES);
        will(returnValue(secretKey));
      }
    });

    assertThat(service.getSecretKey(), is(allOf(
        hasProperty("id", is(equalTo(ID))),
        hasProperty("secretKey", is(sameInstance(secretKey))))));

    service.stop(stopContext);
  }

  @Test(expected = StartException.class)
  public void testStartWhenNoSuchServiceProviderException() throws Exception {
    context.checking(new Expectations() {
      {
        oneOf(serviceLocator).locate(SecretKeyProvider.class, PROVIDER, MODULE);
        will(throwException(new NoSuchServiceProviderException()));
      }
    });

    serviceBuilder().build().start(startContext);
  }

  @Test(expected = StartException.class)
  public void testStartWhenModuleLoadException() throws Exception {
    context.checking(new Expectations() {
      {
        oneOf(serviceLocator).locate(SecretKeyProvider.class, PROVIDER, MODULE);
        will(throwException(new ModuleLoadException()));
      }
    });

    serviceBuilder().build().start(startContext);
  }

  @Test(expected = SecretException.class)
  public void testGetSecretKeyWhenSecretException() throws Exception {
    context.checking(new Expectations() {
      {
        oneOf(startContext).getController();
        will(returnValue(serviceController));
        allowing(serviceController).getName();
        will(returnValue(SERVICE_NAME));

        oneOf(serviceLocator).locate(SecretKeyProvider.class, PROVIDER, MODULE);
        will(returnValue(secretKeyProvider));
      }
    });

    final SecretKeyService service = serviceBuilder().build();
    service.start(startContext);

    context.checking(new Expectations() {
      {
        oneOf(secretKeyProvider).getSecretKey(TYPE, LENGTH, PROPERTIES);
        will(throwException(new SecretException("error")));
      }
    });

    service.getSecretKey();
  }

  private SecretKeyService.Builder serviceBuilder() {
    return SecretKeyService.builder()
        .serviceLocator(serviceLocator)
        .id(ID)
        .type(TYPE)
        .length(LENGTH)
        .provider(PROVIDER)
        .module(MODULE)
        .properties(PROPERTIES);
  }

}