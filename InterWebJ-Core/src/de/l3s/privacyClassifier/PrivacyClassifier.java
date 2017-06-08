package de.l3s.privacyClassifier;

import de.l3s.interwebj.query.Query;
import de.l3s.interwebj.query.QueryResult;

public interface PrivacyClassifier
{

    /**
     * Sets set privacy level of each resultItem of the queryResult
     * 
     * @param queryResult
     * @return
     */
    public abstract QueryResult classify(QueryResult queryResult, Query query);

}
