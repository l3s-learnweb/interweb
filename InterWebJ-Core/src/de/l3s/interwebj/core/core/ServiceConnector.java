package de.l3s.interwebj.core.core;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.lang3.NotImplementedException;

import de.l3s.interwebj.core.AuthCredentials;
import de.l3s.interwebj.core.InterWebException;
import de.l3s.interwebj.core.Parameters;
import de.l3s.interwebj.core.db.Database;
import de.l3s.interwebj.core.query.ConnectorResults;
import de.l3s.interwebj.core.query.ContentType;
import de.l3s.interwebj.core.query.Query;
import de.l3s.interwebj.core.query.ResultItem;

public abstract class ServiceConnector {

    private final String name;
    private final String baseUrl;
    private final List<ContentType> contentTypes;

    private AuthCredentials consumerAuthCredentials;

    public ServiceConnector(String name, String baseUrl, ContentType... contentTypes) {
        this(name, baseUrl, Arrays.asList(contentTypes));
    }

    public ServiceConnector(String name, String baseUrl, List<ContentType> contentTypes) {
        this.name = name;
        this.baseUrl = baseUrl;
        this.contentTypes = contentTypes;
    }

    public abstract ServiceConnector clone();

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        ServiceConnector other = (ServiceConnector) obj;
        if (name == null) {
            return other.name == null;
        } else {
            return name.equals(other.name);
        }
    }


    public Parameters getRefinedCallbackParameters(Parameters parameters) {
        return parameters;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    public boolean isRegistered() {
        return consumerAuthCredentials != null;
    }

    public boolean supportContentType(ContentType contentType) {
        return (contentType != null) && contentTypes.contains(contentType);
    }

    public InterWebPrincipal getPrincipal(Parameters parameters) throws InterWebException {
        for (String parameter : parameters.keySet()) {
            if (parameter.equals(Parameters.IWJ_USER_ID)) {
                String userName = parameters.get(parameter);

                Database database = Environment.getInstance().getDatabase();
                InterWebPrincipal principal = database.readPrincipalByName(userName);
                if (principal == null) {
                    throw new InterWebException("User [" + userName + "] not found");
                }
                return principal;
            }
        }

        throw new InterWebException("Unable to fetch user name from the callback URL");
    }

    public Parameters authenticate(String callbackUrl, Parameters parameters) throws InterWebException {
        throw new NotImplementedException();
    }

    public AuthCredentials completeAuthentication(Parameters params) throws InterWebException {
        throw new NotImplementedException();
    }

    public abstract ConnectorResults get(Query query, AuthCredentials authCredentials) throws InterWebException;

    public String getEmbedded(AuthCredentials authCredentials, String url, int width, int height) throws InterWebException {
        return null;
    }

    public String getUserId(AuthCredentials userAuthCredentials) throws InterWebException {
        throw new NotImplementedException();
    }

    public abstract boolean isConnectorRegistrationDataRequired();

    public abstract boolean isUserRegistrationDataRequired();

    public abstract boolean isUserRegistrationRequired();

    public ResultItem put(byte[] data, ContentType contentType, Parameters params, AuthCredentials authCredentials) throws InterWebException {
        throw new NotImplementedException();
    }

    public void revokeAuthentication() throws InterWebException {
        // not supported. do nothing
    }

    /**
     * Returns a set of tags. The tags have to belong <i>somehow</i> to the user.
     * (Tags that the user has used or the users favorite resources are tagged with ... depends on service)
     *
     * @param username the function throws an IllegalArgumentException if the username is not valid at the service
     * @param maxCount the maximal result count
     */
    public Set<String> getTags(String username, int maxCount) throws IllegalArgumentException, IOException {
        throw new NotImplementedException();
    }

    /**
     * Returns a set of usernames that belong <i>somehow</i> to the specified tags.
     */
    public Set<String> getUsers(Set<String> tags, int maxCount) throws IOException, InterWebException {
        throw new NotImplementedException();
    }

    public String generateCallbackUrl(String baseApiUrl, Parameters parameters) {
        return baseApiUrl + "callback?" + parameters.toQueryString();
    }

    public AuthCredentials getAuthCredentials() {
        return consumerAuthCredentials;
    }

    public void setAuthCredentials(AuthCredentials authCredentials) {
        this.consumerAuthCredentials = authCredentials;
    }

    public String getName() {
        return name;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public List<ContentType> getContentTypes() {
        return contentTypes;
    }
}
