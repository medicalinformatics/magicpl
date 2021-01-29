ARG DOCKER_COMMON_VERSION=0.2.0
FROM torbenbrenner/tomcat.common:${DOCKER_COMMON_VERSION}
MAINTAINER t.brenner@dkfz-heidelberg.de
LABEL magicpl.version="latest"
### Environment variables for docker.common.
ENV COMPONENT="magicpl" MANDATORY_VARIABLES="MAGICPL_API_KEY MAGICPL_PASSPHRASE"
ENV MAGICPL_LOG_LEVEL="info"
