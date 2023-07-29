package de.l3s.interweb.core.suggest;

import java.util.LinkedList;
import java.util.List;

import de.l3s.interweb.core.ConnectorResults;

public class SuggestConnectorResults extends ConnectorResults {
    private final List<String> items = new LinkedList<>();

    public List<String> getItems() {
        return items;
    }

    public void addItem(String result) {
        items.add(result);
    }

    public int size() {
        return items.size();
    }
}
