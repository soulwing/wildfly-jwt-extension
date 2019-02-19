/*
 * File created on Feb 15, 2019
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
package org.soulwing.jwt.jaas;

import static org.soulwing.jwt.jaas.JaasLogger.LOGGER;

import java.io.IOException;
import java.security.Principal;
import java.security.acl.Group;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginException;

import org.jboss.security.SimpleGroup;
import org.jboss.security.auth.callback.ObjectCallback;
import org.jboss.security.auth.spi.AbstractServerLoginModule;
import org.soulwing.jwt.api.Claim;
import org.soulwing.jwt.api.UserPrincipal;
import org.soulwing.jwt.service.Credential;

/**
 * A JAAS {@code LoginModule} that validates a credential of type 
 * {@link com.auth0.jwt.interfaces.DecodedJWT}.
 * <p>
 * Assertion attribute values can be used as role names by specifying the
 * {{roleClaims}} module option.  The value is a list of claim names
 * whose values will be used as role names.  The list may be delimited with
 * spaces and/or commas.  Each value of each named role claim is used
 * as a role for the authentic user.
 *
 * @author Carl Harris
 */
public class JwtLoginModule extends AbstractServerLoginModule {

  
  public static final String ROLE_CLAIMS = "role-claims";
  
  private String[] roleClaims;
  protected Credential credential;
  
  @Override
  public void initialize(Subject subject, CallbackHandler callbackHandler,
      Map<String, ?> sharedState, Map<String, ?> options) {
    super.initialize(subject, callbackHandler, sharedState, options);
    this.roleClaims = parseRoleAttributes(options.get(ROLE_CLAIMS));
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("role attributes: " + Arrays.asList(this.roleClaims));
    }
  }

  private static String[] parseRoleAttributes(Object attrs) {
    if (attrs == null) return new String[0];
    final String attributes = attrs.toString();
    if (attributes.isEmpty()) return new String[0];
    return attributes.split("\\s*(,|\\s)\\s*");
  }
  
  @Override
  public boolean login() throws LoginException {
    final ObjectCallback callback = new ObjectCallback("Credential");
    try {
      callbackHandler.handle(new Callback[] { callback });
      final Object obj = callback.getCredential();
      if (!(obj instanceof Credential)) {
        if (LOGGER.isDebugEnabled()) {
          LOGGER.debug("credential object is not a decoded JWT");
        }
        return false;
      }

      credential = (Credential) obj;
      loginOk = true;
      return true;
    }
    catch (UnsupportedCallbackException ex) {
      throw new LoginException("ObjectCallback not supported");
    }
    catch (IOException ex) {
      throw new LoginException("I/O error: " + ex.toString());
    }
  }

  @Override
  protected Principal getIdentity() {
    UserPrincipal principal = credential.getPrincipal();
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("identity assertion for principal '" 
          + principal.getName() + "'");
    }
    return principal;
  }

  @Override
  protected Group[] getRoleSets() throws LoginException {
    final Set<Principal> roles = getRoles();
    if (roles.isEmpty()) return new Group[0];

    Group rolesGroup = new SimpleGroup("Roles");
    for (Principal role : roles) {
      rolesGroup.addMember(role);
    }
    
    return new Group[] { rolesGroup };
  }

  protected Set<Principal> getRoles() throws LoginException {
    final Set<Principal> roles = new LinkedHashSet<>();
    if (roleClaims.length > 0) {
      final Map<String, Claim> claims = credential.getPrincipal().getClaims();
      for (String roleClaim : roleClaims) {
        final Claim claim = claims.get(roleClaim);
        if (claim.type() == Claim.Type.ARRAY) {
          for (String value : claim.asList(String.class)) {
            roles.add(createRole(value));
          }
        }
        else if (!claim.isNull()) {
          roles.add(createRole(claim.asString()));
        }
        else {
          if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("assertion does not contain claim '"
                + roleClaim + "'");
          }
        }
      }
    }
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("assertion-derived roles: " + roles);
    }
    return roles; 
  }
  
  protected Principal createRole(String name) throws LoginException {
    try {
      final Principal role = createIdentity(name);
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("created role principal '" + name + "'");
      }
      return role;
    }
    catch (Exception ex) {
      LOGGER.error("while creating role '" + name + "': " + ex, ex);
      throw new LoginException("cannot create role: " + name);
    }
  }
  
}
