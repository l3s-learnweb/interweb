package de.l3s.interweb.server;

import io.quarkus.panache.common.Sort;

public final class PanacheUtils {
    public static Sort createSort(String order) {
        Sort sort = Sort.empty();
        if (order != null) {
            String[] tokens = order.split(",");
            for (String token : tokens) {
                if (token.startsWith("-")) {
                    sort = sort.and(token.substring(1), Sort.Direction.Descending);
                } else {
                    sort = sort.and(token, Sort.Direction.Ascending);
                }
            }
        }
        return sort;
    }
}
