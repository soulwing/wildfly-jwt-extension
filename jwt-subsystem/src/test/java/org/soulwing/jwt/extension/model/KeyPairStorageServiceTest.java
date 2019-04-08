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
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import org.jboss.msc.service.ServiceController;
import org.jboss.msc.service.ServiceName;
import org.jboss.msc.service.StartContext;
import org.jboss.msc.service.StartException;
import org.jboss.msc.service.StopContext;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.soulwing.jwt.extension.spi.ClassLoaderServiceLocator;
import org.soulwing.jwt.extension.spi.ServiceLocator;
import org.soulwing.jwt.extension.spi.ServiceLocatorException;
import org.soulwing.s2ks.spi.KeyPairStorageProvider;

/**
 * Unit tests for {@link SecretService}.
 *
 * @author Carl Harris
 */
public class KeyPairStorageServiceTest {

  private static final String PROVIDER = "LOCAL";
  private static final String MODULE = "module";

  private static final ServiceName SERVICE_NAME = ServiceName.of("test");

  private static Properties properties = new Properties();
  private static Path storageDirectory;

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

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
    storageDirectory = Files.createTempDirectory("keyPairStorage");
    properties.setProperty("password", "password");
    properties.setProperty("storageDirectory", storageDirectory.toString());
  }

  @AfterClass
  public static void tearDownAfterClass() throws Exception {
    Files.deleteIfExists(storageDirectory);
  }

  @Test
  public void testSuccessfulBuild() throws Exception {
    final KeyPairStorageService service = serviceBuilder().build();
    assertThat(service.getProvider(), is(equalTo(PROVIDER)));
    assertThat(service.getModule(), is(equalTo(MODULE)));
    assertThat(service.getProperties(), is(equalTo(properties)));
    assertThat(service.getValue(), is(sameInstance(service)));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testBuildWithNoProvider() throws Exception {
    final KeyPairStorageService.Builder builder = serviceBuilder();
    builder.provider(null).build();
  }

  @Test
  public void testStartAndStop() throws Exception {
    context.checking(new Expectations() {
      {
        allowing(startContext).getController();
        will(returnValue(serviceController));
        allowing(stopContext).getController();
        will(returnValue(serviceController));
        allowing(serviceController).getName();
        will(returnValue(SERVICE_NAME));

        oneOf(serviceLocator).getLoader(KeyPairStorageProvider.class,
            MODULE);
        will(returnValue(ClassLoaderServiceLocator.INSTANCE.getLoader(
            KeyPairStorageProvider.class, null)));
      }
    });

    final KeyPairStorageService service = serviceBuilder().build();
    service.start(startContext);
    assertThat(service.getKeyPairStorage(), is(not(nullValue())));
    service.stop(stopContext);
  }

  @Test(expected = StartException.class)
  public void testStartWhenServiceLocatorException() throws Exception {
    context.checking(new Expectations() {
      {
        allowing(startContext).getController();
        will(returnValue(serviceController));
        allowing(serviceController).getName();
        will(returnValue(SERVICE_NAME));

        oneOf(serviceLocator).getLoader(KeyPairStorageProvider.class,
            MODULE);
        will(throwException(new ServiceLocatorException()));
      }
    });

    serviceBuilder().build().start(startContext);
  }

  private KeyPairStorageService.Builder serviceBuilder() {
    return KeyPairStorageService.builder()
        .serviceLocator(serviceLocator)
        .provider(PROVIDER)
        .module(MODULE)
        .properties(properties);
  }

}