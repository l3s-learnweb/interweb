package de.l3s.interwebj.core.core;

import java.io.IOException;
import java.util.Set;

import de.l3s.interwebj.core.AuthCredentials;
import de.l3s.interwebj.core.InterWebException;
import de.l3s.interwebj.core.Parameters;
import de.l3s.interwebj.core.query.Query;
import de.l3s.interwebj.core.query.QueryResult;
import de.l3s.interwebj.core.query.ResultItem;
import org.apache.commons.lang3.NotImplementedException;

public interface ServiceConnector {
    public default Parameters authenticate(String callbackUrl, Parameters parameters) throws InterWebException {
        throw new NotImplementedException();
    }

    public abstract ServiceConnector clone();

    public default AuthCredentials completeAuthentication(Parameters params) throws InterWebException {
        throw new NotImplementedException();
    }

    public abstract QueryResult get(Query query, AuthCredentials authCredentials) throws InterWebException;

    public abstract AuthCredentials getAuthCredentials();

    public abstract String getBaseUrl();

    public abstract Set<String> getContentTypes();

    public default String getEmbedded(AuthCredentials authCredentials, String url, int width, int height) throws InterWebException
    {
        return null;
    }

    public abstract String getName();

    public abstract Parameters getRefinedCallbackParameters(Parameters parameters);

    public default String getUserId(AuthCredentials userAuthCredentials) throws InterWebException
    {
        throw new NotImplementedException();
    }

    public abstract boolean isConnectorRegistrationDataRequired();

    public abstract boolean isRegistered();

    public abstract boolean isUserRegistrationDataRequired();

    public abstract boolean isUserRegistrationRequired();

    public default ResultItem put(byte[] data, String contentType, Parameters params, AuthCredentials authCredentials) throws InterWebException
    {
        throw new NotImplementedException();
    }

    public default void revokeAuthentication() throws InterWebException
    {
        // not supported. do nothing
    }

    public abstract void setAuthCredentials(AuthCredentials consumerAuthCredentials);

    public abstract boolean supportContentType(String contentType);

    /**
     * Returns a set of tags. The tags have to belong <i>somehow</i> to the user.
     * (Tags that the user has used or the users favorite resources are tagged with ... depends on service)
     *
     * @param username the function throws an IllegalArgumentException if the username is not valid at the service
     * @param maxCount the maximal result count
     */
    public default Set<String> getTags(String username, int maxCount) throws IllegalArgumentException, IOException
    {
        throw new NotImplementedException();
    }

    /**
     * Returns a set of usernames that belong <i>somehow</i> to the specified tags
     *
     * @param tags
     * @param maxCount the maximal result count
     * @return
     * @throws InterWebException
     */
    public default Set<String> getUsers(Set<String> tags, int maxCount) throws IOException, InterWebException
    {
        throw new NotImplementedException();
    }

    public default String generateCallbackUrl(String baseApiUrl, Parameters parameters)
    {
        return baseApiUrl + "callback?" + parameters.toQueryString();
    }

    public abstract InterWebPrincipal getPrincipal(Parameters parameters) throws InterWebException;
}
