/*
 * File created on Feb 23, 2019
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
package org.soulwing.jwt.demo;

import java.io.StringWriter;
import java.util.Collections;
import javax.enterprise.context.ApplicationScoped;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.stream.JsonGenerator;

import org.soulwing.jwt.extension.api.UserPrincipal;

/**
 * A {@link GreetingService} implemented as an injectable
 * @author Carl Harris
 */
@ApplicationScoped
public class GreetingServiceBean implements GreetingService {

  @Override
  public String generateGreeting(UserPrincipal principal) {
    final JsonArrayBuilder affiliations = Json.createArrayBuilder();
    principal.getClaim("afl").asList(String.class).forEach(affiliations::add);
    final JsonArrayBuilder groups = Json.createArrayBuilder();
    principal.getClaim("grp").asList(String.class).forEach(groups::add);

    final JsonObject greeting = Json.createObjectBuilder()
        .add("sub", principal.getClaim("sub").asString())
        .add("uid", principal.getClaim("uid").asLong())
        .add("cn", principal.getClaim("cn").asString())
        .add("eml", principal.getClaim("eml").asString())
        .add("afl", affiliations)
        .add("grp", groups)
        .build();

    final StringWriter writer = new StringWriter();
    Json.createWriterFactory(
        Collections.singletonMap(JsonGenerator.PRETTY_PRINTING, true))
        .createWriter(writer).writeObject(greeting);

    return writer.toString();
  }

}
