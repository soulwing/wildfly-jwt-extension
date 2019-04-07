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

import java.io.FileNotFoundException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.util.Properties;

import org.jboss.as.controller.services.path.PathManager;
import org.jboss.modules.ModuleLoadException;
import org.jboss.msc.service.ServiceController;
import org.jboss.msc.service.ServiceName;
import org.jboss.msc.service.StartContext;
import org.jboss.msc.service.StartException;
import org.jboss.msc.service.StopContext;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.jmock.lib.concurrent.Synchroniser;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Rule;
import org.junit.Test;
import org.soulwing.jwt.extension.spi.NoSuchServiceProviderException;
import org.soulwing.jwt.extension.spi.Secret;
import org.soulwing.jwt.extension.spi.SecretException;
import org.soulwing.jwt.extension.spi.ServiceLocator;
import org.soulwing.jwt.extension.spi.TrustStoreProvider;

/**
 * Unit tests for {@link TrustStoreService}.
 *
 * @author Carl Harris
 */
public class TrustStoreServiceTest {

  private static final String PATH = "path";
  private static final String RELATIVE_TO = "relativeTo";
  private static final String RESOLVED_PATH = "resolvedPath";
  private static final String PROVIDER = "provider";
  private static final String MODULE = "module";
  private static final String PROPERTY_NAME = "propertyName";
  private static final String PROPERTY_VALUE = "propertyValue";
  private static final Properties PROPERTIES = new Properties();
  private static final ServiceName SERVICE_NAME = ServiceName.of("test");

  static {
    PROPERTIES.setProperty(PROPERTY_NAME, PROPERTY_VALUE);
  }

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
  private ServiceController serviceController;

  @Mock
  private SecretService passwordSecretService;

  @Mock
  private Secret secret;

  @Mock
  private PathManager pathManager;

  @Mock
  private ServiceLocator serviceLocator;

  @Mock
  private TrustStoreProvider trustStoreProvider;

  @Test(expected = IllegalArgumentException.class)
  public void testWhenNoPath() throws Exception {
    serviceBuilder().path(null).build();
  }

  @Test(expected = IllegalArgumentException.class)
  public void testWhenNoProvider() throws Exception {
    serviceBuilder().provider(null).build();
  }


  @Test
  public void testSuccessfulBuild() throws Exception {
    final TrustStoreService service = serviceBuilder().build();
    assertThat(service.getPath(), is(equalTo(PATH)));
    assertThat(service.getRelativeTo(), is(equalTo(RELATIVE_TO)));
    assertThat(service.getValue(), is(sameInstance(service)));
    assertThat(service.getProvider(), is(equalTo(PROVIDER)));
    assertThat(service.getModule(), is(equalTo(MODULE)));
    assertThat(service.getProperties(), is(equalTo(PROPERTIES)));
  }

  @Test
  public void testStartStop() throws Exception {
    final KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());

    context.checking(new Expectations() {
      {
        oneOf(startContext).getController();
        will(returnValue(serviceController));
        oneOf(stopContext).getController();
        will(returnValue(serviceController));
        allowing(serviceController).getName();
        will(returnValue(SERVICE_NAME));

        oneOf(pathManager).resolveRelativePathEntry(PATH, RELATIVE_TO);
        will(returnValue(RESOLVED_PATH));

        oneOf(passwordSecretService).getSecret();
        will(returnValue(secret));
        oneOf(serviceLocator).locate(TrustStoreProvider.class, PROVIDER, MODULE);
        will(returnValue(trustStoreProvider));
        oneOf(trustStoreProvider).getTrustStore(RESOLVED_PATH, secret, PROPERTIES);
        will(returnValue(trustStore));

        oneOf(secret).destroy();
      }
    });

    final TrustStoreService service = serviceBuilder().build();
    service.setPasswordSecretService(() -> passwordSecretService);
    service.setPathManager(() -> pathManager);
    service.start(startContext);
    assertThat(service.getTrustStore(), is(sameInstance(trustStore)));
    service.stop(stopContext);
  }

  @Test(expected = StartException.class)
  public void testStartWhenModuleNotFoundException() throws Exception {
    context.checking(new Expectations() {
      {
        oneOf(pathManager).resolveRelativePathEntry(PATH, RELATIVE_TO);
        will(returnValue(RESOLVED_PATH));
        oneOf(serviceLocator).locate(TrustStoreProvider.class, PROVIDER, MODULE);
        will(throwException(new ModuleLoadException()));
      }
    });

    final TrustStoreService service = serviceBuilder().build();
    service.setPasswordSecretService(() -> passwordSecretService);
    service.setPathManager(() -> pathManager);
    service.start(startContext);
  }

  @Test(expected = StartException.class)
  public void testStartWhenNoSuchServiceProviderException() throws Exception {
    context.checking(new Expectations() {
      {
        oneOf(pathManager).resolveRelativePathEntry(PATH, RELATIVE_TO);
        will(returnValue(RESOLVED_PATH));
        oneOf(serviceLocator).locate(TrustStoreProvider.class, PROVIDER, MODULE);
        will(throwException(new NoSuchServiceProviderException()));
      }
    });

    final TrustStoreService service = serviceBuilder().build();
    service.setPasswordSecretService(() -> passwordSecretService);
    service.setPathManager(() -> pathManager);
    service.start(startContext);
  }

  @Test(expected = StartException.class)
  public void testStartWhenSecretException() throws Exception {
    context.checking(new Expectations() {
      {
        oneOf(pathManager).resolveRelativePathEntry(PATH, RELATIVE_TO);
        will(returnValue(RESOLVED_PATH));
        oneOf(serviceLocator).locate(TrustStoreProvider.class, PROVIDER, MODULE);
        will(returnValue(trustStoreProvider));
        oneOf(passwordSecretService).getSecret();
        will(throwException(new SecretException("error")));
      }
    });

    final TrustStoreService service = serviceBuilder().build();
    service.setPasswordSecretService(() -> passwordSecretService);
    service.setPathManager(() -> pathManager);
    service.start(startContext);
  }

  @Test(expected = StartException.class)
  public void testStartWhenFileNotFoundException() throws Exception {
    context.checking(new Expectations() {
      {
        oneOf(pathManager).resolveRelativePathEntry(PATH, RELATIVE_TO);
        will(returnValue(RESOLVED_PATH));
        oneOf(serviceLocator).locate(TrustStoreProvider.class, PROVIDER, MODULE);
        will(returnValue(trustStoreProvider));
        oneOf(passwordSecretService).getSecret();
        will(returnValue(secret));
        oneOf(trustStoreProvider).getTrustStore(RESOLVED_PATH, secret, PROPERTIES);
        will(throwException(new FileNotFoundException()));
      }
    });

    final TrustStoreService service = serviceBuilder().build();
    service.setPasswordSecretService(() -> passwordSecretService);
    service.setPathManager(() -> pathManager);
    service.start(startContext);
  }

  @Test(expected = StartException.class)
  public void testStartWhenKeyStoreException() throws Exception {
    context.checking(new Expectations() {
      {
        oneOf(pathManager).resolveRelativePathEntry(PATH, RELATIVE_TO);
        will(returnValue(RESOLVED_PATH));
        oneOf(serviceLocator).locate(TrustStoreProvider.class, PROVIDER, MODULE);
        will(returnValue(trustStoreProvider));
        oneOf(passwordSecretService).getSecret();
        will(returnValue(secret));
        oneOf(trustStoreProvider).getTrustStore(RESOLVED_PATH, secret, PROPERTIES);
        will(throwException(new KeyStoreException()));
      }
    });

    final TrustStoreService service = serviceBuilder().build();
    service.setPasswordSecretService(() -> passwordSecretService);
    service.setPathManager(() -> pathManager);
    service.start(startContext);
  }

  private TrustStoreService.Builder serviceBuilder() {
    return TrustStoreService.builder()
        .serviceLocator(serviceLocator)
        .path(PATH)
        .relativeTo(RELATIVE_TO)
        .provider(PROVIDER)
        .module(MODULE)
        .properties(PROPERTIES);
  }

}