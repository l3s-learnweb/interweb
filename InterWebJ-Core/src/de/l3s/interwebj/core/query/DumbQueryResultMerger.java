package de.l3s.interwebj.core.query;

public class DumbQueryResultMerger implements QueryResultMerger {

    @Override
    public QueryResult merge(QueryResult queryResult) {
        return queryResult;
    }

}
