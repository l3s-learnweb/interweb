package de.l3s.interweb.tomcat.servlet;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serial;
import java.net.URI;
import java.nio.charset.StandardCharsets;

import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.l3s.interweb.core.AuthCredentials;
import de.l3s.interweb.core.InterWebException;
import de.l3s.interweb.core.Parameters;
import de.l3s.interweb.core.search.SearchProvider;
import de.l3s.interweb.tomcat.app.Engine;
import de.l3s.interweb.tomcat.app.InterWebPrincipal;
import de.l3s.interweb.tomcat.db.Database;
import de.l3s.interweb.tomcat.jaxb.services.ServiceResponse;
import de.l3s.interweb.tomcat.rest.Endpoint;

@WebServlet(name = "CallbackServlet", description = "Authorization callback", urlPatterns = {"/callback"})
public class CallbackServlet extends HttpServlet {
    private static final Logger log = LogManager.getLogger(CallbackServlet.class);
    @Serial
    private static final long serialVersionUID = 6534209215912582685L;

    @Inject
    private Engine engine;
    @Inject
    private Database database;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public CallbackServlet() {
        super();
    }

    public InterWebPrincipal getPrincipal(Parameters parameters) throws InterWebException {
        for (String parameter : parameters.keySet()) {
            if (parameter.equals(Parameters.IWJ_USER_ID)) {
                String userName = parameters.get(parameter);

                InterWebPrincipal principal = database.readPrincipalByName(userName);
                if (principal == null) {
                    throw new InterWebException("User [" + userName + "] not found");
                }
                return principal;
            }
        }

        throw new InterWebException("Unable to fetch user name from the callback URL");
    }

    public void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        log.info("query string: [{}]", request.getQueryString());

        Parameters parameters = new Parameters();
        parameters.addMultivaluedParams(request.getParameterMap());
        refineParameters(parameters);
        InterWebPrincipal principal = null;
        SearchProvider connector = null;
        String clientType = null;
        try {
            connector = getConnector(parameters);
            clientType = getClientType(parameters);
            principal = getPrincipal(parameters);

            engine.processAuthenticationCallback(principal, connector, parameters);
        } catch (InterWebException e) {
            log.catching(e);
            parameters.add(Parameters.ERROR, e.getMessage());
        }
        if ("rest".equals(clientType)) {
            processRestRequest(request, response, principal, connector, parameters);
        } else if ("servlet".equals(clientType)) {
            processServletRequest(request, response, parameters);
        } else {
            throw new ServletException("No valid client type parameter found in URL [" + clientType + "]");
        }
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     * response)
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        process(request, response);
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
     * response)
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        process(request, response);
    }

    private String fetchParam(Parameters parameters, String paramName) {
        for (String parameter : parameters.keySet()) {
            if (parameter.equals(paramName)) {
                return parameters.get(parameter);
            }
        }
        return null;
    }

    private String getClientType(Parameters parameters) throws InterWebException {
        String clientType = fetchParam(parameters, Parameters.CLIENT_TYPE);
        if (clientType == null) {
            throw new InterWebException("Unable to fetch client type from the callback URL");
        }
        return clientType;
    }

    private SearchProvider getConnector(Parameters parameters) throws InterWebException {
        String connectorName = fetchParam(parameters, Parameters.IWJ_CONNECTOR_ID);
        if (connectorName == null) {
            throw new InterWebException("Unable to fetch connector name from the callback URL");
        }

        SearchProvider connector = engine.getConnector(connectorName);
        if (connector == null) {
            throw new InterWebException("Connector [" + connectorName + "] not found");
        }
        return connector;
    }

    private void processRestRequest(HttpServletRequest request, HttpServletResponse response, InterWebPrincipal principal,
                                    SearchProvider connector, Parameters parameters) throws IOException {
        String callback = parameters.get(Parameters.CALLBACK);
        log.info("callback: [{}]", callback);

        if (StringUtils.isNotEmpty(callback)) {
            response.sendRedirect(callback);
            return;
        }

        if (parameters.hasParameter(Parameters.ERROR)) {
            throw new WebApplicationException(parameters.get(Parameters.ERROR), Response.Status.BAD_REQUEST);
        }

        String consumerKey = parameters.get(Parameters.CONSUMER_KEY);
        AuthCredentials consumerAuthCredentials = database.readConsumerByKey(consumerKey).authCredentials();

        AuthCredentials userAuthCredentials = principal.getOauthCredentials();
        URI baseUri = URI.create(request.getRequestURL().toString()).resolve(".");
        String serviceApiPath = baseUri.toASCIIString() + "api/users/default/services/" + connector.getName();
        WebTarget target = Endpoint.createWebTarget(serviceApiPath, consumerAuthCredentials, userAuthCredentials);

        Response clientResponse = target.request(MediaType.APPLICATION_XML).get();
        ServiceResponse serviceResponse = clientResponse.readEntity(ServiceResponse.class);
        writeIntoServletResponse(response, serviceResponse);
    }

    private void processServletRequest(HttpServletRequest request, HttpServletResponse response, Parameters parameters) throws IOException {
        String redirectUrl = request.getContextPath() + "/view/services.xhtml";
        if (parameters.hasParameter(Parameters.ERROR)) {
            String error = parameters.get(Parameters.ERROR);
            redirectUrl += "?error=" + error;
        }
        response.sendRedirect(redirectUrl);
    }

    private void refineParameters(Parameters parameters) {
        for (SearchProvider connector : engine.getSearchProviders()) {
            Parameters refinedParameters = connector.getRefinedCallbackParameters(parameters);
            parameters.add(refinedParameters, true);
        }
    }

    private void writeIntoServletResponse(HttpServletResponse servletResponse, ServiceResponse serviceResponse) throws IOException {
        byte[] content = serviceResponse.toString().getBytes(StandardCharsets.UTF_8);
        OutputStream os = servletResponse.getOutputStream();
        os.write(content);
        os.close();
        servletResponse.setContentType(MediaType.APPLICATION_XML);
    }
}
