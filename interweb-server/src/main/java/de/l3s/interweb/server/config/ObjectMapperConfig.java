package de.l3s.interweb.server.config;

import java.io.IOException;
import java.time.Instant;

import jakarta.inject.Singleton;

import io.quarkus.jackson.ObjectMapperCustomizer;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Singleton
public class ObjectMapperConfig implements ObjectMapperCustomizer {
    public void customize(ObjectMapper config) {
        config.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        config.setSerializationInclusion(JsonInclude.Include.NON_DEFAULT);

        JavaTimeModule timeModule = new JavaTimeModule();
        timeModule.addSerializer(Instant.class, new JsonSerializer<>() {
            @Override
            public void serialize(Instant value, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
                jsonGenerator.writeNumber(value.getEpochSecond());
            }
        });
        config.registerModule(timeModule);
    }
}
