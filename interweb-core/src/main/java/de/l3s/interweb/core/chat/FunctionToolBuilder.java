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

    public FunctionToolBuilder parameters(Customizer<PropertyConfigurer> ...parameterCustomizer) {
        if (function.getParameters() == null) {
            function.setParameters(new Parameters());
        }

        HashMap<String, Property> properties = new HashMap<>();
        ArrayList<String> required = new ArrayList<>();
        for (Customizer<PropertyConfigurer> customizer : parameterCustomizer) {
            PropertyConfigurer propertyConfigurer = new PropertyConfigurer();
            customizer.customize(propertyConfigurer);

            properties.put(propertyConfigurer.name, propertyConfigurer.property);
            if (propertyConfigurer.required) {
                required.add(propertyConfigurer.name);
            }
        }

        function.getParameters().setType("object");
        function.getParameters().setProperties(properties);
        function.getParameters().setRequired(required);
        return this;
    }

    public FunctionToolBuilder additionalProperties() {
        if (function.getParameters() == null) {
            function.setParameters(new Parameters());
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

        public PropertyConfigurer enumValues(String ...enumValues) {
            this.property.setEnumValues(Arrays.asList(enumValues));
            return this;
        }

        public PropertyConfigurer required() {
            this.required = true;
            return this;
        }
    }
}
