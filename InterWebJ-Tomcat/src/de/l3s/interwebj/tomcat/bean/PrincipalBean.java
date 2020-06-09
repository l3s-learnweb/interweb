package de.l3s.interwebj.tomcat.bean;

import java.io.IOException;
import java.io.Serializable;

import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.l3s.interwebj.core.core.Environment;
import de.l3s.interwebj.core.core.InterWebPrincipal;
import de.l3s.interwebj.core.db.Database;
import de.l3s.interwebj.tomcat.webutil.FacesUtils;

@Named
@SessionScoped
public class PrincipalBean implements Serializable {
    private static final long serialVersionUID = -955620779684197312L;
    private static final Logger log = LogManager.getLogger(PrincipalBean.class);

    public boolean hasRole(String role) {
        SessionBean sessionBean = (SessionBean) FacesUtils.getManagedBean("sessionBean");
        InterWebPrincipal principal = sessionBean.getPrincipal();
        return principal != null && principal.hasRole(role);
    }

    public boolean isLoggedIn() {
        SessionBean sessionBean = (SessionBean) FacesUtils.getManagedBean("sessionBean");
        return sessionBean.getPrincipal() != null;
    }

    public String login(String username, String password) throws IOException {
        Environment environment = Environment.getInstance();
        Database database = environment.getDatabase();
        InterWebPrincipal principal = database.authenticate(username, password);
        if (principal == null) {
            FacesUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, "Incorrect login. Please check username and/or password.");
            return null;
        }
        SessionBean sessionBean = (SessionBean) FacesUtils.getManagedBean("sessionBean");
        sessionBean.setPrincipal(principal);
        String savedRequestUrl = sessionBean.getSavedRequestUrl();
        if (savedRequestUrl != null) {
            sessionBean.setSavedRequestUrl(null);
            log.info("redirecting to: {}", savedRequestUrl);
            String contextPath = FacesUtils.getContextPath();
            FacesUtils.redirect(contextPath + savedRequestUrl);
        }
        return "index";
    }

    public String logout() {
        SessionBean sessionBean = (SessionBean) FacesUtils.getManagedBean("sessionBean");
        sessionBean.setPrincipal(null);
        HttpServletRequest request = FacesUtils.getRequest();
        request.getSession(false).invalidate();
        return "index";
    }
}
