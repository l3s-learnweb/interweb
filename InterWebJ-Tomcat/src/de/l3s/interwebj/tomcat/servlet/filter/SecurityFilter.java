package de.l3s.interwebj.tomcat.servlet.filter;

import java.io.IOException;

import jakarta.inject.Inject;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.l3s.interwebj.core.core.AccessControl;
import de.l3s.interwebj.core.core.Environment;
import de.l3s.interwebj.core.core.InterWebPrincipal;
import de.l3s.interwebj.tomcat.bean.SessionBean;

@WebFilter("/view/*")
public class SecurityFilter implements Filter {
    private static final Logger log = LogManager.getLogger(SecurityFilter.class);
    public static final String LOGIN_PAGE = "/view/login.xhtml";

    @Inject
    private SessionBean sessionBean;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (request instanceof HttpServletRequest httpRequest) {
            if (response instanceof HttpServletResponse httpResponse) {
                String requestUrl = getRequestUrl(httpRequest);
                // log.debug("Requested URL: [" + requestUrl + "]");

                AccessControl accessControl = Environment.getInstance().getAccessControl();
                InterWebPrincipal principal = sessionBean.getPrincipal();
                boolean authorized = accessControl.isAuthorized(principal, requestUrl, null);
                if (!authorized) {
                    log.info("Login required. User: {} is not authorized to access the resource: {}", principal, requestUrl);
                    log.info("saving requested URL: {}", requestUrl);
                    sessionBean.setSavedRequestUrl(requestUrl);
                    httpResponse.sendRedirect(httpRequest.getContextPath() + LOGIN_PAGE);
                    return;
                }
            }
        }

        chain.doFilter(request, response);
    }

    private String getRequestUrl(HttpServletRequest httpRequest) {
        String requestURL = httpRequest.getServletPath();
        if (httpRequest.getPathInfo() != null) {
            requestURL += httpRequest.getPathInfo();
        }
        if (httpRequest.getQueryString() != null) {
            requestURL += "?" + httpRequest.getQueryString();
        }
        return requestURL;
    }
}
