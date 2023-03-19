package de.l3s.interweb.tomcat.bean;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.l3s.interweb.core.AuthCredentials;
import de.l3s.interweb.core.search.SearchProvider;
import de.l3s.interweb.tomcat.app.Engine;

@Named
@ViewScoped
public class RegisterServicesBean implements Serializable {
    @Serial
    private static final long serialVersionUID = 1250989064923740720L;
    private static final Logger log = LogManager.getLogger(RegisterServicesBean.class);

    private transient List<ConnectorWrapper> connectorWrappers;

    @Inject
    private Engine engine;

    public List<ConnectorWrapper> getConnectorWrappers() {
        if (connectorWrappers == null) {
            connectorWrappers = new ArrayList<>();
            for (SearchProvider connector : engine.getSearchProviders()) {
                if (connector.isConnectorRegistrationDataRequired()) {
                    ConnectorWrapper connectorWrapper = new ConnectorWrapper();
                    connectorWrapper.setConnector(connector);
                    if (connector.getAuthCredentials() != null) {
                        connectorWrapper.setKey(connector.getAuthCredentials().getKey());
                        connectorWrapper.setSecret(connector.getAuthCredentials().getSecret());
                    }
                    connectorWrappers.add(connectorWrapper);
                }
            }
        }
        return connectorWrappers;
    }

    public boolean isRegistered(ConnectorWrapper connectorWrapper) {
        return connectorWrapper.getConnector().isRegistered();
    }

    public void register(ConnectorWrapper connectorWrapper) {
        try {
            AuthCredentials authCredentials = new AuthCredentials(connectorWrapper.getKey(), connectorWrapper.getSecret());
            SearchProvider connector = connectorWrapper.getConnector();
            log.info("registering connector: [{}] with credentials: {}", connector.getName(), authCredentials);
            connector.setAuthCredentials(authCredentials);
            engine.setConsumerAuthCredentials(connector.getName(), authCredentials);
        } catch (Exception e) {
            log.catching(e);
        }
    }

    public void unregister(ConnectorWrapper connectorWrapper) {
        try {
            SearchProvider connector = connectorWrapper.getConnector();
            log.info("unregistering connector: [{}]", connector.getName());
            connector.setAuthCredentials(null);
            engine.setConsumerAuthCredentials(connector.getName(), null);
        } catch (Exception e) {
            log.catching(e);
        }
    }

}
