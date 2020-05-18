package de.l3s.interwebj.core.core;

import java.util.Objects;
import java.util.Set;

import de.l3s.interwebj.core.AuthCredentials;
import de.l3s.interwebj.core.InterWebException;
import de.l3s.interwebj.core.Parameters;
import de.l3s.interwebj.core.db.Database;

public abstract class AbstractServiceConnector implements ServiceConnector {

    private final String name;
    private final String baseUrl;
    private final Set<String> contentTypes;

    private AuthCredentials consumerAuthCredentials;

    public AbstractServiceConnector(String name, String baseUrl, Set<String> contentTypes) {
        this.name = name;
        this.baseUrl = baseUrl;
        this.contentTypes = contentTypes;
    }

    @Override
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
        AbstractServiceConnector other = (AbstractServiceConnector) obj;
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        return true;
    }

    @Override
    public AuthCredentials getAuthCredentials() {
        return consumerAuthCredentials;
    }

    @Override
    public void setAuthCredentials(AuthCredentials authCredentials) {
        this.consumerAuthCredentials = authCredentials;
    }

    @Override
    public String getBaseUrl() {
        return baseUrl;
    }

    @Override
    public Set<String> getContentTypes() {
        return contentTypes;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Parameters getRefinedCallbackParameters(Parameters parameters) {
        return parameters;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public boolean isRegistered() {
        return consumerAuthCredentials != null;
    }

    @Override
    public boolean supportContentType(String contentType) {
        return (contentType != null) && contentTypes.contains(contentType);
    }

    @Override
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
}