package de.l3s.interweb.server.principal;

import java.util.HashMap;
import java.util.Map;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;

import io.quarkus.hibernate.reactive.panache.common.WithSessionOnDemand;
import io.quarkus.runtime.StartupEvent;
import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import org.jboss.logging.Logger;

import de.l3s.interweb.core.AuthCredentials;

@ApplicationScoped
public class SecretsService {
    private static final Logger log = Logger.getLogger(SecretsService.class);
    private static final String CREDENTIALS_PREFIX = "connectors.";
    private static final String CREDENTIALS_SUFFIX_KEY = ".key";
    private static final String CREDENTIALS_SUFFIX_SECRET = ".secret";

    private Map<String, AuthCredentials> defaultCredentials = new HashMap<>();

    public void loadCredentials(@Observes StartupEvent evt) {
        log.info("Loading credentials...");

        Config config = ConfigProvider.getConfig();
        for (String name : config.getPropertyNames()) {
            if (name.startsWith(CREDENTIALS_PREFIX) && name.endsWith(CREDENTIALS_SUFFIX_KEY)) {
                String service = name.substring(CREDENTIALS_PREFIX.length(), name.length() - CREDENTIALS_SUFFIX_KEY.length()).toLowerCase();
                String key = config.getValue(name, String.class);
                String secret = config.getOptionalValue(CREDENTIALS_PREFIX + service + CREDENTIALS_SUFFIX_SECRET, String.class).orElse(null);

                defaultCredentials.put(service, new AuthCredentials(key, secret));
                log.info("Loaded credentials for " + service);
            }
        }
    }

    public AuthCredentials getAuthCredentials(String name) {
        return defaultCredentials.get(name);
    }

    @WithSessionOnDemand
    public Uni<AuthCredentials> getAuthCredentials(String name, Principal principal) {
        if (principal == null) {
            return Uni.createFrom().item(getAuthCredentials(name));
        }

        return Secrets.findByPrincipalAndName(principal, name)
                .map(cred -> new AuthCredentials(cred.secret1, cred.secret2))
                .onFailure().recoverWithItem(() -> getAuthCredentials(name));
    }
}
