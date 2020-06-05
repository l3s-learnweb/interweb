package de.l3s.interwebj.tomcat.bean;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.l3s.interwebj.core.AuthCredentials;
import de.l3s.interwebj.core.InterWebException;
import de.l3s.interwebj.core.Parameters;
import de.l3s.interwebj.core.connector.ServiceConnector;
import de.l3s.interwebj.core.core.Engine;
import de.l3s.interwebj.core.core.Environment;
import de.l3s.interwebj.core.core.InterWebPrincipal;
import de.l3s.interwebj.core.db.Database;
import de.l3s.interwebj.tomcat.webutil.FacesUtils;

@Named
@RequestScoped
public class ServicesBean {
    private static final Logger log = LogManager.getLogger(ServicesBean.class);

    private final Engine engine;
    private final Database database;
    private final InterWebPrincipal principal;
    private final List<ConnectorWrapper> connectorWrappers;
    private final List<ConnectorWrapper> awaitingConnectorWrappers;
    private String error;

    public ServicesBean() throws InterWebException {
        engine = Environment.getInstance().getEngine();
        database = Environment.getInstance().getDatabase();
        principal = FacesUtils.getSessionBean().getPrincipal();
        connectorWrappers = new ArrayList<>();
        awaitingConnectorWrappers = new ArrayList<>();
        for (ServiceConnector connector : engine.getConnectors()) {
            if (connector.isRegistered() && connector.isUserRegistrationRequired()) {
                ConnectorWrapper connectorWrapper = new ConnectorWrapper();
                connectorWrapper.setConnector(connector);
                connectorWrappers.add(connectorWrapper);
                if (!isUserAuthenticated(connectorWrapper)) {
                    awaitingConnectorWrappers.add(connectorWrapper);
                }
            }
        }
        Parameters parameters = new Parameters();
        parameters.addMultivaluedParams(FacesUtils.getRequest().getParameterMap());
        if (parameters.hasParameter(Parameters.ERROR)) {
            error = parameters.get(Parameters.ERROR);
        }
    }

    public void authenticate(Object obj) throws InterWebException {
        String baseApiUrl = FacesUtils.getInterWebJBean().getBaseUrl();
        ConnectorWrapper connectorWrapper = (ConnectorWrapper) obj;
        ServiceConnector connector = connectorWrapper.getConnector();
        connectorWrapper.setKey(connector.getAuthCredentials().getKey());
        connectorWrapper.setSecret(connector.getAuthCredentials().getSecret());
        Parameters parameters = new Parameters();
        parameters.add(Parameters.IWJ_USER_ID, principal.getName());
        parameters.add(Parameters.IWJ_CONNECTOR_ID, connector.getName());
        parameters.add(Parameters.CLIENT_TYPE, "servlet");

        String interwebjCallbackUrl = connector.generateCallbackUrl(baseApiUrl, parameters);
        log.info("interwebjCallbackUrl: [" + interwebjCallbackUrl + "]");
        parameters = connector.authenticate(interwebjCallbackUrl, parameters);
        if (connectorWrapper.getKey() != null) {
            parameters.add(Parameters.USER_KEY, connectorWrapper.getKey());
        }
        if (connectorWrapper.getSecret() != null) {
            parameters.add(Parameters.USER_SECRET, connectorWrapper.getSecret());
        }
        String authorizationUrl = parameters.get(Parameters.AUTHORIZATION_URL);
        if (authorizationUrl != null) {
            log.info("redirecting to service authorization url: " + authorizationUrl);
            engine.addPendingAuthorizationConnector(principal, connector, parameters);
            try {
                FacesUtils.getExternalContext().redirect(authorizationUrl);
            } catch (IOException e) {
                log.error(e);
                FacesUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, e);
            }
        }
    }

    public List<ConnectorWrapper> getAwaitingConnectorWrappers() throws InterWebException {
        return awaitingConnectorWrappers;
    }

    public List<ConnectorWrapper> getConnectorWrappers() throws InterWebException {
        return connectorWrappers;
    }

    public String getError() {
        return error;
    }

    public boolean isUserAuthenticated(Object obj) throws InterWebException {
        if (principal != null) {
            ConnectorWrapper connectorWrapper = (ConnectorWrapper) obj;
            ServiceConnector connector = connectorWrapper.getConnector();
            return engine.isUserAuthenticated(connector, principal);
        }
        return false;
    }

    public boolean isUserRegistrationRequired(Object obj) throws InterWebException {
        ConnectorWrapper connectorWrapper = (ConnectorWrapper) obj;
        ServiceConnector connector = connectorWrapper.getConnector();
        return connector.isUserRegistrationDataRequired() && !isUserAuthenticated(obj);
    }

    public void revoke(Object obj) throws InterWebException {
        ConnectorWrapper connectorWrapper = (ConnectorWrapper) obj;
        ServiceConnector connector = connectorWrapper.getConnector();
        log.info("revoking user authentication");
        engine.setUserAuthCredentials(connector.getName(), principal, null, null);
        connector.revokeAuthentication();
    }

    public void save(Object obj) {
        ConnectorWrapper connectorWrapper = (ConnectorWrapper) obj;
        ServiceConnector connector = connectorWrapper.getConnector();
        String key = StringUtils.isEmpty(connectorWrapper.getKey()) ? null : connectorWrapper.getKey();
        String secret = StringUtils.isEmpty(connectorWrapper.getSecret()) ? null : connectorWrapper.getSecret();
        AuthCredentials authCredentials = new AuthCredentials(key, secret);
        try {
            String userId = connector.getUserId(authCredentials);
            database.saveUserAuthCredentials(connector.getName(), principal.getName(), userId, authCredentials);
        } catch (InterWebException e) {
            log.error(e);
            FacesUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, e);
        }
    }
}
