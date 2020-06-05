package de.l3s.interwebj.core.query;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.RandomStringUtils;

public class QueryFactory {

    public Query createQuery(String stringQuery) {
        return createQuery(stringQuery, new HashSet<>());
    }

    public Query createQuery(String stringQuery, Set<ContentType> contentTypes) {
        return new Query(createQueryId(), stringQuery, contentTypes);
    }

    public static String createQueryId() {
        return RandomStringUtils.randomAlphanumeric(36);
    }
}
