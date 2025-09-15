package de.l3s.interweb.core.suggest;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import io.quarkus.runtime.annotations.RegisterForReflection;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import de.l3s.interweb.core.ConnectorResults;

@RegisterForReflection
@JsonPropertyOrder({"service", "service_url", "elapsed_time", "items"})
public class SuggestConnectorResults extends ConnectorResults {
    private final List<String> items = new LinkedList<>();

    public List<String> getItems() {
        return items;
    }

    public void addItems(String... items) {
        Collections.addAll(this.items, items);
    }

    public int size() {
        return items.size();
    }
}
