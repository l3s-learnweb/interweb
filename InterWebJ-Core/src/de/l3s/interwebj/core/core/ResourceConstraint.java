package de.l3s.interwebj.core.core;

import java.util.regex.Pattern;

public class ResourceConstraint {

    private final Pattern pattern;
    private final int weight;
    private final String role;

    public ResourceConstraint(String pattern, int weight) {
        this(pattern, null, weight);
    }

    public ResourceConstraint(String patternString, String role, int weight) {
        pattern = Pattern.compile(patternString);
        this.role = role;
        this.weight = weight;
    }

    public String getRole() {
        return role;
    }

    public int getWeight() {
        return weight;
    }

    public boolean matches(String path) {
        return pattern.matcher(path).matches();
    }
}
