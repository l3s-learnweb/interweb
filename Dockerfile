## Stage 1 : build with maven builder image with native capabilities
FROM quay.io/quarkus/ubi9-quarkus-mandrel-builder-image:jdk-25 AS build

USER root

# the pakcage contains node 16, when required 20.19+
#RUN microdnf update -y \
#    && microdnf install -y nodejs \
#    && microdnf clean all

# Install Node.js and npm for Quinoa
RUN curl -fsSL https://rpm.nodesource.com/setup_24.x | bash - && \
    microdnf install -y nodejs && microdnf clean all

USER quarkus
WORKDIR /code

COPY --chown=quarkus:quarkus --chmod=0755 mvnw /code/mvnw
COPY --chown=quarkus:quarkus .mvn /code/.mvn
COPY --chown=quarkus:quarkus pom.xml /code/
COPY --chown=quarkus:quarkus interweb-core/pom.xml /code/interweb-core/
COPY --chown=quarkus:quarkus interweb-server/pom.xml /code/interweb-server/
COPY --chown=quarkus:quarkus interweb-client/pom.xml /code/interweb-client/
COPY --chown=quarkus:quarkus connectors/AnthropicConnector/pom.xml /code/connectors/AnthropicConnector/
COPY --chown=quarkus:quarkus connectors/FlickrConnector/pom.xml /code/connectors/FlickrConnector/
COPY --chown=quarkus:quarkus connectors/GiphyConnector/pom.xml /code/connectors/GiphyConnector/
COPY --chown=quarkus:quarkus connectors/GoogleConnector/pom.xml /code/connectors/GoogleConnector/
COPY --chown=quarkus:quarkus connectors/IpernityConnector/pom.xml /code/connectors/IpernityConnector/
COPY --chown=quarkus:quarkus connectors/OllamaConnector/pom.xml /code/connectors/OllamaConnector/
COPY --chown=quarkus:quarkus connectors/OpenaiConnector/pom.xml /code/connectors/OpenaiConnector/
COPY --chown=quarkus:quarkus connectors/SlideShareConnector/pom.xml /code/connectors/SlideShareConnector/
COPY --chown=quarkus:quarkus connectors/VimeoConnector/pom.xml /code/connectors/VimeoConnector/
COPY --chown=quarkus:quarkus connectors/YouTubeConnector/pom.xml /code/connectors/YouTubeConnector/

RUN ./mvnw -B org.apache.maven.plugins:maven-dependency-plugin:go-offline

COPY --chown=quarkus:quarkus . /code
RUN ./mvnw package -DskipTests -pl -interweb-client -Dnative

## Stage 2 : create the docker final image
FROM quay.io/quarkus/ubi9-quarkus-micro-image:2.0 AS final
WORKDIR /work/
COPY --from=build /code/interweb-server/target/*-runner /work/application

# set up permissions for user `1001`
RUN chmod 775 /work /work/application \
  && chown -R 1001 /work \
  && chmod -R "g+rwX" /work \
  && chown -R 1001:root /work

EXPOSE 8080
USER 1001

CMD ["./application", "-Dquarkus.http.host=0.0.0.0"]
