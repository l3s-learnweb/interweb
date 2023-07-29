package de.l3s.interweb.server;

import jakarta.inject.Singleton;

import io.quarkus.jackson.ObjectMapperCustomizer;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;

@Singleton
public class ObjectMapperConfig implements ObjectMapperCustomizer {
    public void customize(ObjectMapper config) {
        config.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        config.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        config.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
    }
}
