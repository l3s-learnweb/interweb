package de.l3s.interweb.server.config;

import jakarta.inject.Singleton;

import io.quarkus.jackson.ObjectMapperCustomizer;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Singleton
public class ObjectMapperConfig implements ObjectMapperCustomizer {
    public void customize(ObjectMapper config) {
        config.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        config.setSerializationInclusion(JsonInclude.Include.NON_DEFAULT);
        config.registerModule(new JavaTimeModule());
        config.enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }
}
