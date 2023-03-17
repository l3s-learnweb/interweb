package de.l3s.interweb.tomcat.db;

import java.util.List;

import de.l3s.interweb.core.AuthCredentials;
import de.l3s.interweb.tomcat.core.Consumer;
import de.l3s.interweb.tomcat.core.InterWebPrincipal;

public interface Database {

    InterWebPrincipal authenticate(String userName, String userPassword);

    void close();

    void deleteConnector(String connectorName);

    void deleteConsumer(String userName, String consumerName);

    boolean hasPrincipal(String userName);

    AuthCredentials readConnectorAuthCredentials(String connectorName);

    String readConnectorUserId(String connectorName, String userName);

    Consumer readConsumerByKey(String key);

    List<Consumer> readConsumers(String userName);

    InterWebPrincipal readPrincipalByKey(String key);

    InterWebPrincipal readPrincipalByName(String name);

    AuthCredentials readUserAuthCredentials(String connectorName, String userName);

    void saveConnector(String connectorName, AuthCredentials authCredentials);

    void saveConsumer(String userName, Consumer consumer);

    void savePrincipal(InterWebPrincipal principal, String password);

    void saveUserAuthCredentials(String connectorName, String userName, String userId, AuthCredentials authCredentials);

    void updatePrincipal(InterWebPrincipal principal);

    void deleteMediator(String mediator);

    boolean hasConnector(String connectorName);

    String readMediator(String userName);

    void saveMediator(String userName, String mediator);

    void logQuery(String consumerKey, String query);
}
