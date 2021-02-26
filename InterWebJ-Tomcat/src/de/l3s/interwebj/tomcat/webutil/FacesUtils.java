package de.l3s.interwebj.tomcat.webutil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;

import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FacesUtils {
    private static final Logger log = LogManager.getLogger(FacesUtils.class);

    public static void addGlobalMessage(FacesMessage.Severity severity, String message) {
        addGlobalMessage(severity, message, null);
    }

    public static void addGlobalMessage(FacesMessage.Severity severity, String message, String id) {
        FacesContext.getCurrentInstance().addMessage(id, new FacesMessage(severity, message, null));
    }

    public static void addGlobalMessage(FacesMessage.Severity severity, Throwable e) {
        log.catching(e);
        addGlobalMessage(severity, new ByteArrayOutputStream().toString(StandardCharsets.UTF_8));
    }

    public static ExternalContext getExternalContext() {
        return FacesContext.getCurrentInstance().getExternalContext();
    }

    public static HttpServletRequest getRequest() {
        return (HttpServletRequest) getExternalContext().getRequest();
    }

    public static String getRequestBaseURL() {
        HttpServletRequest request = getRequest();
        URI currentUri = URI.create(request.getRequestURL().toString());
        URI baseUri = currentUri.resolve(request.getContextPath() + "/");
        return baseUri.toASCIIString();
    }

    public static void redirect(String redirectPath) throws IOException {
        getExternalContext().redirect(redirectPath);
    }

    public static void redirectLocal(String redirectPath) throws IOException {
        getExternalContext().redirect(getRequest().getContextPath() + redirectPath);
    }
}
