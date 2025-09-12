package de.l3s.interweb.server.features.search;

import java.lang.reflect.Method;

import io.quarkus.cache.CacheKeyGenerator;
import io.quarkus.cache.CompositeCacheKey;

import de.l3s.interweb.core.search.SearchConnector;
import de.l3s.interweb.core.search.SearchQuery;

public class SearchQueryKeyGen implements CacheKeyGenerator {
    @Override
    public Object generate(Method method, Object... methodParams) {
        if (methodParams.length == 2 && methodParams[0] instanceof SearchQuery query && methodParams[1] instanceof SearchConnector connector) {
            return new CompositeCacheKey(
                query.getQuery(),
                query.getContentTypes(),
                query.getLanguage(),
                query.getCountry(),
                query.getOffset(),
                query.getExtras(),
                query.getSort(),
                query.getDateTo(),
                query.getDateFrom(),
                connector.getId()
            );
        }

        return new CompositeCacheKey(method.getName(), methodParams);
    }
}
