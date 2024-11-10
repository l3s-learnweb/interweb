package de.l3s.interweb.server.components.auth;

import de.l3s.interweb.core.models.Model;

import java.security.Permission;
import java.util.Objects;

public class ModelPermission extends Permission {

    private final Model model;

    public ModelPermission(String name, Model model) {
        super(name);
        this.model = model;
    }

    @Override
    public boolean implies(Permission permission) {
        // a condition, in which the permission is required to be checked
        return !model.isFree(); // means to skip checking if the model is free
    }

    @Override
    public String getActions() {
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ModelPermission that = (ModelPermission) o;
        return Objects.equals(model, that.model);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(model);
    }
}
