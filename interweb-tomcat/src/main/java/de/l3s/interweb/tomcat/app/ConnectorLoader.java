package de.l3s.interweb.tomcat.app;

import java.util.List;
import java.util.ServiceLoader;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.l3s.interweb.core.search.SearchProvider;
import de.l3s.interweb.core.suggest.SuggestionProvider;

public class ConnectorLoader {
    private static final Logger log = LogManager.getLogger(ConnectorLoader.class);

    public List<SearchProvider> loadSearchProviders() {
        log.info("loading search providers...");
        ServiceLoader<SearchProvider> providers = ServiceLoader.load(SearchProvider.class);
        providers.forEach(provider -> log.info(provider.getName() + " loaded"));
        return providers.stream().map(ServiceLoader.Provider::get).collect(Collectors.toList());
    }

    public List<SuggestionProvider> loadSuggestionProviders() {
        log.info("loading suggestion providers...");
        ServiceLoader<SuggestionProvider> providers = ServiceLoader.load(SuggestionProvider.class);
        providers.forEach(provider -> log.info(provider.getService().name() + " loaded"));
        return providers.stream().map(ServiceLoader.Provider::get).collect(Collectors.toList());
    }
}
