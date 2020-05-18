package de.l3s.interwebj.tomcat.servlet.filter;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.l3s.interwebj.core.core.AccessControll;
import de.l3s.interwebj.core.core.Environment;
import de.l3s.interwebj.core.core.InterWebPrincipal;
import de.l3s.interwebj.tomcat.bean.SessionBean;

@WebFilter("/view/*")
public class SecurityFilter implements Filter {
    public static final String LOGIN_PAGE = "/view/login.xhtml";
    private static final Logger log = LogManager.getLogger(SecurityFilter.class);
    @Inject
    private SessionBean sessionBean;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (request instanceof HttpServletRequest) {
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            String requestUrl = getRequestUrl(httpRequest);
            // log.debug("Requested URL: [" + requestUrl + "]");

            AccessControll accessControll = Environment.getInstance().getAccessControll();
            InterWebPrincipal principal = sessionBean.getPrincipal();
            boolean authorized = accessControll.isAuthorized(principal, requestUrl, null);
            if (!authorized) {
                log.info("Login required. User: " + principal + " is not authorized to access the resource: " + requestUrl);
                log.info("saving requested URL: " + requestUrl);
                sessionBean.setSavedRequestUrl(requestUrl);
                httpResponse.sendRedirect(httpRequest.getContextPath() + LOGIN_PAGE);
                return;
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
