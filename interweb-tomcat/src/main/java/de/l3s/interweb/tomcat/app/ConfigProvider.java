package de.l3s.interweb.tomcat.app;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serial;
import java.io.Serializable;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import javax.naming.InitialContext;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;

import jakarta.enterprise.context.ApplicationScoped;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@ApplicationScoped
public class ConfigProvider implements Serializable {
    @Serial
    private static final long serialVersionUID = 8265034665414043361L;
    private static final Logger log = LogManager.getLogger(ConfigProvider.class);
    private static final String PROP_KEY_PREFIX = "interweb_";

    /**
     * All the application configuration stored here.
     */
    private final Properties properties = new Properties();

    /**
     * An environment of the application, based on the configuration used.
     */
    private String environment;

    /**
     * Indicated whether the application is started in Servlet container with CDI or initialized manually.
     * E.g. {@code true} on Tomcat and {@code false} in tests or maintenance tasks.
     */
    private final boolean servlet;

    /**
     * Indicates whether the application is started in development mode according to jakarta.faces.PROJECT_STAGE in web.xml.
     * It is managed by Maven, {@code false} on build in `prod` profile, {@code true} otherwise. Always {@code false} when {@link #servlet} is {@code false}.
     */
    private Boolean development;

    @Deprecated
    public ConfigProvider() {
        this(true);
    }

    public ConfigProvider(final boolean servlet) {
        loadProperties();
        loadEnvironmentVariables();

        this.servlet = servlet;
        if (servlet) {
            loadJndiVariables();
        } else {
            development = true;
        }
    }

    private void loadProperties() {
        try (InputStream defaultProperties = getClass().getClassLoader().getResourceAsStream("interweb.properties")) {
            properties.load(defaultProperties);

            try (InputStream localProperties = getClass().getClassLoader().getResourceAsStream("interweb_local.properties")) {
                if (localProperties != null) {
                    properties.load(localProperties);
                    environment = "local";
                    log.info("Local properties loaded.");
                }
            }
        } catch (IOException e) {
            log.error("Unable to load properties file(s)", e);
        }
    }

    /**
     * Because Tomcat always removes per-application context config file, we have to add context-prefix.
     * https://stackoverflow.com/questions/4032773/why-does-tomcat-replace-context-xml-on-redeploy
     */
    private void loadEnvironmentVariables() {
        try {
            Map<String, String> env = System.getenv();
            env.forEach((originalKey, propValue) -> {
                String propKey = originalKey.toLowerCase(Locale.ROOT);
                if (propKey.startsWith(PROP_KEY_PREFIX)) {
                    propKey = propKey.substring(PROP_KEY_PREFIX.length());
                    log.debug("Found environment variable {}: {} (original name {})", propKey, propValue, originalKey);
                    properties.setProperty(propKey, propValue);
                }
            });
        } catch (Exception e) {
            log.error("Unable to load environment variables", e);
        }
    }

    private void loadJndiVariables() {
        try {
            String namespace = "java:comp/env/";
            InitialContext ctx = new InitialContext();
            NamingEnumeration<NameClassPair> list = ctx.list(namespace);

            while (list.hasMore()) {
                NameClassPair next = list.next();
                String namespacedKey = namespace + next.getName();
                String propKey = next.getName().toLowerCase(Locale.ROOT);
                if (propKey.startsWith(PROP_KEY_PREFIX)) {
                    propKey = propKey.substring(PROP_KEY_PREFIX.length());
                    String propValue = ctx.lookup(namespacedKey).toString();
                    log.debug("Found JNDI variable {}: {} (original name {})", propKey, propValue, namespacedKey);
                    properties.setProperty(propKey, propValue);
                }
            }

            list.close();
            ctx.close();
        } catch (Exception e) {
            log.error("Unable to load JNDI variables", e);
        }
    }

    public Object setProperty(final String key, final String value) {
        return properties.setProperty(key, value);
    }

    public String getProperty(final String key) {
        return properties.getProperty(key);
    }

    public String getProperty(final String key, final String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }
}
