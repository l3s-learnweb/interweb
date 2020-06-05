package de.l3s.interwebj.core.query;

import java.util.HashSet;
import java.util.Set;

import de.l3s.interwebj.core.util.RandomGenerator;

public class QueryFactory {

    public Query createQuery(String stringQuery) {
        return createQuery(stringQuery, new HashSet<>());
    }

    public Query createQuery(String stringQuery, Set<ContentType> contentTypes) {
        String id = RandomGenerator.getInstance().nextHexId();
        return new Query(id, stringQuery, contentTypes);
    }
}
