package de.l3s.interweb.server;

import java.util.HashMap;
import java.util.Map;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.containers.Network;

public class DatabaseResource implements QuarkusTestResourceLifecycleManager {
    public static final String DATABASE_USERNAME = "quarkus";
    public static final String DATABASE_PASSWORD = "quarkus";
    public static final String DATABASE_NAME = "quarkus";
    public static final String DATABASE_NETWORK_ALIAS = "database";

    private MariaDBContainer<?> dbServer;

    @Override
    public Map<String, String> start() {

        dbServer = new MariaDBContainer<>("mariadb:10.11")
            .withNetwork(Network.newNetwork())
            .withNetworkAliases(DATABASE_NETWORK_ALIAS)
            .withUsername(DATABASE_USERNAME)
            .withPassword(DATABASE_PASSWORD)
            .withDatabaseName(DATABASE_NAME);
        dbServer.start();

        String address = dbServer.getHost() + ':' + dbServer.getMappedPort(3306);

        Map<String, String> params = new HashMap<>();
        params.put("quarkus.datasource.reactive.url", String.format("mariadb://%s/%s", address, DATABASE_NAME));
        return params;
    }

    @Override
    public void stop() {
        if (dbServer != null) {
            dbServer.stop();
        }
    }
}
