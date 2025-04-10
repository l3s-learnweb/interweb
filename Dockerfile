## Stage 1 : build with maven builder image with native capabilities
FROM quay.io/quarkus/ubi-quarkus-mandrel-builder-image:jdk-21 AS build
COPY --chown=quarkus:quarkus . /code
USER quarkus

WORKDIR /code
RUN ./mvnw -B org.apache.maven.plugins:maven-dependency-plugin:go-offline
RUN ./mvnw package -DskipTests -Dnative

## Stage 2 : create the docker final image
FROM quay.io/quarkus/quarkus-micro-image:3.0 AS final
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
