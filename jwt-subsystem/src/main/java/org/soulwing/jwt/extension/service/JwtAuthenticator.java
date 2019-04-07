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
package org.soulwing.jwt.extension.service;

import static org.soulwing.jwt.extension.service.ServiceLogger.LOGGER;

import org.soulwing.jwt.api.Claims;
import org.soulwing.jwt.api.JWTValidator;
import org.soulwing.jwt.api.exceptions.ExpirationAssertionException;
import org.soulwing.jwt.api.exceptions.JWTConfigurationException;
import org.soulwing.jwt.api.exceptions.JWTEncryptionException;
import org.soulwing.jwt.api.exceptions.JWTParseException;
import org.soulwing.jwt.api.exceptions.JWTSignatureException;
import org.soulwing.jwt.api.exceptions.JWTValidationException;
import org.soulwing.jwt.api.exceptions.LifetimeAssertionException;


/**
 * An authenticator that validates JWT bearer tokens using a {@link JWTValidator}.
 *
 * @author Carl Harris
 */
public class JwtAuthenticator implements Authenticator {

  private final JWTValidator jwtValidator;
  private final Configuration configuration;

  JwtAuthenticator(JWTValidator jwtValidator,
      Configuration configuration) {
    this.jwtValidator = jwtValidator;
    this.configuration = configuration;
  }

  @Override
  public Credential validate(String token) throws AuthenticationException {
    try {
      final Claims claims = jwtValidator.validate(token);
      return new JwtCredential(DelegatingUserPrincipal.newInstance(claims,
          configuration.getTransforms()));
    }
    catch (ExpirationAssertionException | LifetimeAssertionException ex) {
      LOGGER.warn("attempt to authenticate using expired token: "
          + ex.getMessage());
      throw new AuthenticationException(ex.getMessage());
    }
    catch (JWTConfigurationException ex) {
      LOGGER.warn("authentication failed due to configuration error: "
          + ex.getMessage());
      throw new AuthenticationException(ex.getMessage());
    }
    catch (JWTValidationException ex) {
      LOGGER.warn("authentication claims validation failed: "
          + ex.getMessage());
      throw new AuthenticationException(ex.getMessage());
    }
    catch (JWTParseException
          | JWTSignatureException
          | JWTEncryptionException ex) {
      LOGGER.warn("attempt to authenticate using corrupt token: "
          + ex.getMessage());
      throw new AuthenticationException(ex.getMessage());
    }
  }

}
