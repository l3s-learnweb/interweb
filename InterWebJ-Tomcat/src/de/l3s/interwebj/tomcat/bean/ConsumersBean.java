package de.l3s.interwebj.tomcat.bean;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.UriBuilder;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.cache.Cache;

import de.l3s.interwebj.core.AuthCredentials;
import de.l3s.interwebj.core.InterWebException;
import de.l3s.interwebj.core.core.Consumer;
import de.l3s.interwebj.core.core.Engine;
import de.l3s.interwebj.core.core.Environment;
import de.l3s.interwebj.core.core.InterWebPrincipal;
import de.l3s.interwebj.core.db.Database;
import de.l3s.interwebj.tomcat.webutil.FacesUtils;

@Named
@RequestScoped
public class ConsumersBean implements Serializable {
    private static final Logger log = LogManager.getLogger(ConsumersBean.class);
    private static final long serialVersionUID = -2226768983834482837L;

    private String name;
    private String url;
    private String description;
    private AuthCredentials accessToken;
    private String oauthToken;
    private final String callback;

    public ConsumersBean() {
        HttpServletRequest request = FacesUtils.getRequest();
        oauthToken = request.getParameter("oauth_token");
        callback = request.getParameter("oauth_callback");

        Engine engine = Environment.getInstance().getEngine();
        accessToken = (AuthCredentials) engine.getGeneralCache().getIfPresent("access_token:" + oauthToken);
    }

    public void addConsumer() throws IOException {
        Database database = Environment.getInstance().getDatabase();
        InterWebPrincipal principal = FacesUtils.getSessionBean().getPrincipal();
        AuthCredentials authCredentials = AuthCredentials.random();

        Consumer consumer = new Consumer(name, url, description, authCredentials);
        database.saveConsumer(principal.getName(), consumer);
        log.info("consumer: [" + consumer.getName() + "] successfully added");

        String contextPath = FacesUtils.getContextPath();
        FacesUtils.redirect(contextPath + "/view/consumers.xhtml");
    }

    public void authorizeConsumer() throws IOException {
        HttpServletRequest request = FacesUtils.getRequest();
        String requestToken = request.getParameter("oauth_token");
        Engine engine = Environment.getInstance().getEngine();
        Cache<String, Object> cache = engine.getGeneralCache();

        AuthCredentials authCredentials = (AuthCredentials) cache.getIfPresent("request_token:" + requestToken);
        String consumerKey = (String) cache.getIfPresent("consumer_token:" + requestToken);
        String newRandToken = RandomStringUtils.randomAlphanumeric(16);
        accessToken = new AuthCredentials(newRandToken, authCredentials.getSecret());
        cache.invalidate("request_token:" + requestToken);
        cache.invalidate("consumer_token:" + requestToken);
        cache.put("access_token:" + accessToken.getKey(), accessToken);
        cache.put("consumer_token:" + accessToken.getKey(), consumerKey);

        InterWebPrincipal principal = FacesUtils.getSessionBean().getPrincipal();
        principal.setOauthCredentials(accessToken);
        Database database = Environment.getInstance().getDatabase();
        database.updatePrincipal(principal);
        cache.put("principal:" + accessToken.getKey(), principal);
        oauthToken = accessToken.getKey();

        if (!StringUtils.isEmpty(callback)) {
            log.info("callback: [" + callback + "]");
            UriBuilder builder = UriBuilder.fromUri(callback);
            builder = builder.queryParam("oauth_token", accessToken.getKey());
            builder = builder.queryParam("oauth_token_secret", accessToken.getSecret());
            String callbackUrl = builder.build().toASCIIString();
            log.info("redirecting to callback URL: " + callbackUrl);
            FacesUtils.redirect(callbackUrl);
        } else {
            UriBuilder builder = UriBuilder.fromUri(request.getRequestURI());
            builder = builder.queryParam("oauth_token", oauthToken);
            builder = (callback == null) ? builder : builder.queryParam("oauth_callback", callback);
            builder = builder.queryParam("registered", "true");
            String url = builder.build().toASCIIString();
            log.info("forwarding to URL: " + url);
            FacesUtils.redirect(url);
        }
    }

    public AuthCredentials getAccessToken() {
        return accessToken;
    }

    public Consumer getAuthorizationConsumer() {
        Engine engine = Environment.getInstance().getEngine();
        String consumerKey = (String) engine.getGeneralCache().getIfPresent("consumer_token:" + oauthToken);
        Database database = Environment.getInstance().getDatabase();
        Consumer consumer = database.readConsumerByKey(consumerKey);
        return consumer;
    }

    public String getCallback() {
        return callback;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String consumerDescription) {
        description = consumerDescription;
    }

    public String getName() {
        return name;
    }

    public void setName(String consumerName) {
        name = consumerName;
    }

    public String getOauthToken() {
        return oauthToken;
    }

    public List<Consumer> getRegisteredConsumers() throws InterWebException {
        Environment environment = Environment.getInstance();
        Database database = environment.getDatabase();
        InterWebPrincipal principal = FacesUtils.getSessionBean().getPrincipal();
        return database.readConsumers(principal.getName());
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String consumerUrl) {
        url = consumerUrl;
    }

    public boolean isRegistered() {
        HttpServletRequest request = FacesUtils.getRequest();
        return Boolean.parseBoolean(request.getParameter("registered"));
    }

    public void newConsumer() throws IOException {
        String contextPath = FacesUtils.getContextPath();
        FacesUtils.redirect(contextPath + "/view/add_consumer.xhtml");
    }

    public void revoke(Object consumer) throws InterWebException {
        String consumerName = ((Consumer) consumer).getName();
        log.info("revoking consumer [" + consumerName + "]");
        Environment environment = Environment.getInstance();
        Database database = environment.getDatabase();
        InterWebPrincipal principal = FacesUtils.getSessionBean().getPrincipal();
        database.deleteConsumer(principal.getName(), consumerName);
    }
}
