wildfly-jwt-module
==================

A module for Wildfly that provides container-managed JWT-based
authentication for a Java web application.

This module provides container-managed support for JWT, such that web 
applications can use the Java EE standard security mechanisms; for example, 
declarative roles and constraints in `web.xml` and the `@RolesAllowed` bean
annotation. 

The main component is a standard JAAS `LoginModule` that participates in 
Wildfly's security subsystem.  The login module validates a JWT bearer token
and makes the claims specified in it available as roles in Wildfly's built
in security stack. Additionally, it makes the claims from the JWT payload
available to a deployed Java web application via a custom subtype of the
`javax.security.Principal` interface.

Using the module, a Java web application can utilize almost any combination
of container-managed authorization (through the standard mechanisms of
Java EE) as well as application-specific authorization by examining the 
claims made available via the custom `Principal` that is available via
the `HttpServletRequest` object.

Installation
------------

The module and its dependencies must be installed in the `modules` directory 
of your Wildfly server.  Thanks to Wildfly's modular design, the installed 
modules remain isolated in your server's configuration, and will never be seen 
by applications that do not require JWT support.  Moreover, the library
components needed by this extension will not appear on your 
application's class loader, avoiding any potential for conflict.

### Building From Source

Clone this repository and then run Maven at the top level of the source tree.
This will create a version of the extension that is compatible with Wildfly 11 and up.

```
mvn clean install
```

#### Install the Extension
```
tar -C ${WILDFLY_HOME} -zxpvf target/wildfly-jwt-{VERSION}-modules.tar.gz
```

### Configuration

TODO
