FROM soulwing/wildfly:15-latest

COPY *.pem role-mapping.properties /apps/wildfly/standalone.OEM/configuration/
COPY secrets/ /run/secrets/
COPY config.d/ /etc/wildfly/config.d/

COPY modules /apps/wildfly/modules
COPY ${project.artifactId}.${project.packaging} /apps/artifacts/jwt-webapp-demo.war
