/*
 * File created on Apr 7, 2019
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
package org.soulwing.jwt.extension.spi.local.transformer;

import java.util.Properties;
import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;

import org.soulwing.jwt.extension.spi.Transformer;

/**
 * An {@link Transformer} that returns a
 * name component of a distinguished name value.
 *
 * @author Carl Harris
 */
public class DistinguishedToSimpleNameTransformer
    implements Transformer<String, String> {

  static final String NAME_COMPONENT = "name-component";
  static final String FAIL_ON_ERROR = "fail-on-error";
  static final String DEFAULT_NAME_COMPONENT = "cn";
  
  private String nameComponent;
  private boolean failOnError;

  @Override
  public String getName() {
    return "DistinguishedToSimpleName";
  }

  @Override
  public void initialize(Properties properties) { 
    nameComponent = properties.getProperty(NAME_COMPONENT,
        DEFAULT_NAME_COMPONENT); 
    failOnError = Boolean.parseBoolean(properties.getProperty(FAIL_ON_ERROR, 
        Boolean.FALSE.toString()));
  }
  
  @Override
  public String apply(String value) {
    try {
      LdapName name = new LdapName(value);
      for (Rdn rdn : name.getRdns()) {
        if (nameComponent.equalsIgnoreCase(rdn.getType())) {
          return rdn.getValue().toString();
        }
      }
      if (!failOnError) return value;
      throw new IllegalArgumentException("does not contain a `"
          + nameComponent + "` component: `" + value + "`");
    }
    catch (InvalidNameException ex) {
      if (!failOnError) return value;
      throw new IllegalArgumentException("not a valid LDAP name: `" + value
          + "`");
    }
  }

  @Override
  public String toString() {
    return String.format("%s(%s=%s, %s=%s)",
        getClass().getSimpleName().replaceFirst(
            Transformer.class.getSimpleName() + "$", ""),
            NAME_COMPONENT, nameComponent,
            FAIL_ON_ERROR, failOnError);
  }

}
