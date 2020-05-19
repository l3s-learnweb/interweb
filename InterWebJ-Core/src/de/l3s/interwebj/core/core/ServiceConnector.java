package de.l3s.interwebj.core.core;

import java.io.IOException;
import java.util.Set;

import org.apache.commons.lang3.NotImplementedException;

import de.l3s.interwebj.core.AuthCredentials;
import de.l3s.interwebj.core.InterWebException;
import de.l3s.interwebj.core.Parameters;
import de.l3s.interwebj.core.query.ConnectorResults;
import de.l3s.interwebj.core.query.Query;
import de.l3s.interwebj.core.query.ResultItem;

public interface ServiceConnector {
    default Parameters authenticate(String callbackUrl, Parameters parameters) throws InterWebException {
        throw new NotImplementedException();
    }

    ServiceConnector clone();

    default AuthCredentials completeAuthentication(Parameters params) throws InterWebException {
        throw new NotImplementedException();
    }

    ConnectorResults get(Query query, AuthCredentials authCredentials) throws InterWebException;

    AuthCredentials getAuthCredentials();

    void setAuthCredentials(AuthCredentials consumerAuthCredentials);

    String getBaseUrl();

    Set<String> getContentTypes();

    default String getEmbedded(AuthCredentials authCredentials, String url, int width, int height) throws InterWebException {
        return null;
    }

    String getName();

    Parameters getRefinedCallbackParameters(Parameters parameters);

    default String getUserId(AuthCredentials userAuthCredentials) throws InterWebException {
        throw new NotImplementedException();
    }

    boolean isConnectorRegistrationDataRequired();

    boolean isRegistered();

    boolean isUserRegistrationDataRequired();

    boolean isUserRegistrationRequired();

    default ResultItem put(byte[] data, String contentType, Parameters params, AuthCredentials authCredentials) throws InterWebException {
        throw new NotImplementedException();
    }

    default void revokeAuthentication() throws InterWebException {
        // not supported. do nothing
    }

    boolean supportContentType(String contentType);

    /**
     * Returns a set of tags. The tags have to belong <i>somehow</i> to the user.
     * (Tags that the user has used or the users favorite resources are tagged with ... depends on service)
     *
     * @param username the function throws an IllegalArgumentException if the username is not valid at the service
     * @param maxCount the maximal result count
     */
    default Set<String> getTags(String username, int maxCount) throws IllegalArgumentException, IOException {
        throw new NotImplementedException();
    }

    /**
     * Returns a set of usernames that belong <i>somehow</i> to the specified tags.
     */
    default Set<String> getUsers(Set<String> tags, int maxCount) throws IOException, InterWebException {
        throw new NotImplementedException();
    }

    default String generateCallbackUrl(String baseApiUrl, Parameters parameters) {
        return baseApiUrl + "callback?" + parameters.toQueryString();
    }

    InterWebPrincipal getPrincipal(Parameters parameters) throws InterWebException;
}
