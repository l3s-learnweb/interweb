package de.l3s.interwebj.tomcat.bean;

import java.io.IOException;
import java.io.Serial;
import java.io.Serializable;

import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.inject.Named;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.l3s.interwebj.core.core.Environment;
import de.l3s.interwebj.core.core.InterWebPrincipal;
import de.l3s.interwebj.core.db.Database;
import de.l3s.interwebj.tomcat.webutil.FacesUtils;

@Named
@SessionScoped
public class SessionBean implements Serializable {
    @Serial
    private static final long serialVersionUID = 2772677361579500292L;
    private static final Logger log = LogManager.getLogger(SessionBean.class);

    private InterWebPrincipal principal;
    private String savedRequestUrl;

    public InterWebPrincipal getPrincipal() {
        return principal;
    }

    public void setPrincipal(InterWebPrincipal principal) {
        this.principal = principal;
    }

    public void setSavedRequestUrl(String savedRequestUrl) {
        this.savedRequestUrl = savedRequestUrl;
    }

    public boolean hasRole(String role) {
        return principal != null && principal.hasRole(role);
    }

    public boolean isLoggedIn() {
        return principal != null;
    }

    public String login(String username, String password) throws IOException {
        Environment environment = Environment.getInstance();
        Database database = environment.getDatabase();
        InterWebPrincipal principal = database.authenticate(username, password);
        if (principal == null) {
            FacesUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, "Incorrect login. Please check username and/or password.");
            return null;
        }

        setPrincipal(principal);
        String requestUrl = savedRequestUrl;
        if (requestUrl != null) {
            savedRequestUrl = null;
            log.info("redirecting to: {}", requestUrl);
            FacesUtils.redirectLocal(requestUrl);
        }

        return "index";
    }

    public String logout() {
        principal = null;
        FacesUtils.getRequest().getSession(false).invalidate();
        return "index";
    }
}
