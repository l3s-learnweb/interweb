package de.l3s.interweb.core.chat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class FunctionToolBuilder {
    private final Function function = new Function();

    public FunctionToolBuilder name(String name) {
        function.setName(name);
        return this;
    }

    public FunctionToolBuilder description(String description) {
        function.setDescription(description);
        return this;
    }

    private void ensureParametersExists() {
        if (function.getParameters() == null) {
            Parameters parameters = new Parameters();
            parameters.setType("object");
            parameters.setProperties(new HashMap<>());
            parameters.setRequired(new ArrayList<>());
            function.setParameters(parameters);
        }
    }

    public FunctionToolBuilder addProperty(Customizer<PropertyConfigurer> propertyCustomizer) {
        if (function.getParameters() == null) {
            ensureParametersExists();
        }

        PropertyConfigurer propertyConfigurer = new PropertyConfigurer();
        propertyCustomizer.customize(propertyConfigurer);

        function.getParameters().getProperties().put(propertyConfigurer.name, propertyConfigurer.property);
        if (propertyConfigurer.required) {
            function.getParameters().getRequired().add(propertyConfigurer.name);
        }

        return this;
    }

    @SafeVarargs
    public final FunctionToolBuilder properties(Customizer<PropertyConfigurer>... parameterCustomizer) {
        for (Customizer<PropertyConfigurer> customizer : parameterCustomizer) {
            addProperty(customizer);
        }

        return this;
    }

    public FunctionToolBuilder additionalProperties() {
        if (function.getParameters() == null) {
            ensureParametersExists();
        }

        function.getParameters().setAdditionalProperties(true);
        return this;
    }

    public FunctionToolBuilder strict(Boolean strict) {
        function.setStrict(strict);
        return this;
    }

    public Tool build() {
        return new Tool(function);
    }

    public interface Customizer<T> {
        void customize(T var1);
    }

    public static class PropertyConfigurer {
        private String name;
        private final Property property = new Property();
        private boolean required;

        public PropertyConfigurer name(String name) {
            this.name = name;
            return this;
        }

        public PropertyConfigurer description(String description) {
            this.property.setDescription(description);
            return this;
        }

        public PropertyConfigurer type(String type) {
            this.property.setType(type);
            return this;
        }

        public PropertyConfigurer enumValues(String... enumValues) {
            this.property.setEnumValues(Arrays.asList(enumValues));
            return this;
        }

        public PropertyConfigurer required() {
            this.required = true;
            return this;
        }
    }
}
