/*
 * File created on Oct 23, 2019
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
package org.soulwing.jwt.extension.undertow;

import static org.soulwing.jwt.extension.undertow.UndertowLogger.LOGGER;

import java.util.Enumeration;

import io.undertow.security.api.SecurityContext;
import io.undertow.server.HandlerWrapper;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.servlet.handlers.ServletRequestContext;
import io.undertow.servlet.spec.HttpSessionImpl;

/**
 * A {@link HandlerWrapper} that invalids authentication context after each
 * request.
 * <p>
 * When using bearer authentication, we want to validate the bearer token
 * for each no request. To ensure that this happens, we log out of the security
 * context and invalidate the session context.
 *
 * @author Carl Harris
 */
class JwtAuthenticationContextInvalidator
    implements HandlerWrapper, HttpHandler {

  private HttpHandler delegate;

  @Override
  public HttpHandler wrap(HttpHandler delegate) {
    this.delegate = delegate;
    return this;
  }

  @Override
  public void handleRequest(HttpServerExchange exchange) throws Exception {
    delegate.handleRequest(exchange);

    final HttpSessionImpl session = getHttpSession(exchange);
    dumpSessionAttributes(session);

    final SecurityContext securityContext = exchange.getSecurityContext();
    if (securityContext != null && securityContext.isAuthenticated()) {
      securityContext.logout();
      LOGGER.info("authentication context logout completed");
    }

    dumpSessionAttributes(session);

//      if (session != null && !session.isInvalid()) {
//        session.invalidate();
//        LOGGER.info("session context invalidated");
//      }
//      else {
//        LOGGER.info("no valid session context");
//      }

  }

  private HttpSessionImpl getHttpSession(HttpServerExchange exchange) {
    final ServletRequestContext requestContext = ServletRequestContext.current();
    if (requestContext == null) return null;
    return requestContext.getCurrentServletContext().getSession(exchange, false);
  }

  private void dumpSessionAttributes(HttpSessionImpl session) {
    if (session == null) {
      LOGGER.debug("no session available");
      return;
    }
    final Enumeration<String> e = session.getAttributeNames();
    while (e.hasMoreElements()) {
      final String name = e.nextElement();
      final Object value = session.getAttribute(name);
      LOGGER.debug("session attribute: " + name + "=" + value);
    }
  }

}
