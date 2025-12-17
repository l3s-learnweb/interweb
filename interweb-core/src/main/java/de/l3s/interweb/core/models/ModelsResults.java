package de.l3s.interweb.core.models;

import de.l3s.interweb.core.ObjectWrapper;

import java.util.ArrayList;
import java.util.List;

public class ModelsResults extends ObjectWrapper<List<Model>> {
    public ModelsResults() {
        super("list", new ArrayList<>());
    }

    public ModelsResults(List<Model> data) {
        super("list", data);
    }

    public void addModel(Model model) {
        this.getData().add(model);
    }

    public void addModel(List<Model> models) {
        this.getData().addAll(models);
    }
}
