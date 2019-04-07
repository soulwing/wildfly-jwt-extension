wildfly-jwt-extension
=====================

An extension for Wildfly that provides container-managed JWT-based
authentication for a Java web application.

This extension provides container-managed support for JWT, such that web 
applications can use the Java EE standard security mechanisms; for example, 
declarative roles and constraints in `web.xml` and the `@RolesAllowed` bean
annotation. 

The main component is a standard JAAS `LoginModule` that participates in 
Wildfly's security subsystem.  The login module validates a JWT bearer delegate
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
tar -C ${WILDFLY_HOME} -zxpvf target/wildfly-jwt-${VERSION}-modules.tar.gz
```

### Configuration

TODO

#### Generating Suitable RSA Key Pairs using OpenSSL

* For the `RS256` algorihm, a 2048-bit key RSA key pair is recommended
* For the `RS384` algorihm, a 3072-bit key RSA key pair is recommended
* For the `RS512` algorihm, a 4096-bit key RSA key pair is recommended

The commands below produce a 2048-bit RSA key pair is produced for `RSA256`.

```bash
openssl genrsa -out rsa-private.pem 2048
openssl rsa -in rsa-private.pem -pubout -out rsa-public.pem
openssl pkcs8 -topk8 -nocrypt -in rsa-private.pem -out rsa-private-pk8.pem  
```

#### Generating Suitable EC Key Pairs using OpenSSL

The EC curve specified by the `-name` parameter of the OpenSSL `ecparam`
command must be supported by the platform.

> On Alpine, note that only the `secp256r1`, `secp384r1`, and `secp521r1`
> curves are supported.
 
```bash
openssl ecparam -name secp256r1 -genkey -noout -out ec-private.pem
openssl ec -in ec-private.pem -pubout -out ec-public.pem
openssl pkcs8 -topk8 -nocrypt -in ec-private.pem -out ec-private-pk8.pem  
```

Alpine note on supported EC curves: secp256r1, secp384r1, secp521r1
see https://github.com/docker-library/openjdk/issues/115

#### Sample Configuration Using RS256 Signatures And AES128 Encryption

```
/extension=org.soulwing.jwt:add(module=org.soulwing.jwt)
/subsystem=jwt:add
reload

/subsystem=security/security-domain=jwt:add
/subsystem=security/security-domain=jwt/authentication=classic:add
/subsystem=security/security-domain=jwt/authentication=classic/login-module=JwtClaim:add(module=org.soulwing.jwt, code=org.soulwing.jwt.extension.jaas.JwtLoginModule, flag=required, module-options={ role-claims="grp, afl" })
/subsystem=security/security-domain=jwt/authentication=classic/login-module=RoleMapping:add(code=RoleMapping, flag=optional, module-options={ rolesProperties="file:/run/wildfly/configuration/role-mapping.properties" })
reload

/subsystem=jwt/secret=trust-store-password:add(provider=FILE, properties={path="/run/secrets/trust-store-password"})
/subsystem=jwt/trust-store=default-jks:add(path="jwt-truststore.jks", relative-to="jboss.server.config.dir", provider="JCA", password-secret=trust-store-password, properties={type=JKS})

/subsystem=jwt/secret-key=shared-key-1:add(id=1, type=AES, length=128, provider=FILE, properties={path="/run/secrets/encryption-password"})

/subsystem=jwt/transformer=DistinguishedToSimpleName:add(properties={name-component=uugid})
/subsystem=jwt/transformer=FlattenCase:add
/subsystem=jwt/claim-transform=grp:add(transformers=[DistinguishedToSimpleName,FlattenCase])
/subsystem=jwt/signature=public-key:add(algorithm=RS256, trust-store=default-jks)
/subsystem=jwt/encryption=shared-key:add(key-management-algorithm=A128KW, content-encryption-algorithm=A128CBC-HS256, compression-algorithm=DEF, secret-keys=[shared-key-1])
/subsystem=jwt/validator=default:add(issuer="token-issuer", issuer-url="https://token-issuer.localhost.vt.edu", expiration-tolerance=90, audience="test-service", signature=public-key, encryption=shared-key, transforms=[grp])

```