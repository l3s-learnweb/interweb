package de.l3s.interwebj.tomcat.servlet;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.l3s.interwebj.core.AuthCredentials;
import de.l3s.interwebj.core.InterWebException;
import de.l3s.interwebj.core.Parameters;
import de.l3s.interwebj.core.core.Engine;
import de.l3s.interwebj.core.core.Environment;
import de.l3s.interwebj.core.core.InterWebPrincipal;
import de.l3s.interwebj.core.core.ServiceConnector;
import de.l3s.interwebj.core.db.Database;
import de.l3s.interwebj.core.xml.ErrorResponse;
import de.l3s.interwebj.core.xml.XmlResponse;
import de.l3s.interwebj.tomcat.jaxb.services.ServiceResponse;
import de.l3s.interwebj.tomcat.rest.Endpoint;

@WebServlet(name = "CallbackServlet", description = "Authorization callback", urlPatterns = {"/callback"})
public class CallbackServlet extends HttpServlet {
    private static final Logger log = LogManager.getLogger(CallbackServlet.class);
    private static final long serialVersionUID = 6534209215912582685L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public CallbackServlet() {
        super();
    }

    public void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        log.info("query string: [" + request.getQueryString() + "]");

        Parameters parameters = new Parameters();
        parameters.addMultivaluedParams(request.getParameterMap());
        refineParameters(parameters);
        InterWebPrincipal principal = null;
        ServiceConnector connector = null;
        String clientType = null;
        try {
            connector = getConnector(parameters);
            clientType = getClientType(parameters);
            principal = connector.getPrincipal(parameters);

            Engine engine = Environment.getInstance().getEngine();
            engine.processAuthenticationCallback(principal, connector, parameters);
        } catch (InterWebException e) {
            log.error(e);
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

    private ServiceConnector getConnector(Parameters parameters) throws InterWebException {
        String connectorName = fetchParam(parameters, Parameters.IWJ_CONNECTOR_ID);
        if (connectorName == null) {
            throw new InterWebException("Unable to fetch connector name from the callback URL");
        }
        Engine engine = Environment.getInstance().getEngine();
        ServiceConnector connector = engine.getConnector(connectorName);
        if (connector == null) {
            throw new InterWebException("Connector [" + connectorName + "] not found");
        }
        return connector;
    }

    private void processRestRequest(HttpServletRequest request, HttpServletResponse response, InterWebPrincipal principal,
                                    ServiceConnector connector, Parameters parameters) throws ServletException, IOException {
        String callback = parameters.get(Parameters.CALLBACK);
        log.info("callback: [" + callback + "]");
        if (StringUtils.isNotEmpty(callback)) {
            response.sendRedirect(callback);
            return;
        }
        if (parameters.hasParameter(Parameters.ERROR)) {
            ErrorResponse errorResponse = new ErrorResponse(999, parameters.get(Parameters.ERROR));
            writeIntoServletResponse(response, errorResponse);
            return;
        }
        String consumerKey = parameters.get(Parameters.CONSUMER_KEY);
        Database database = Environment.getInstance().getDatabase();
        AuthCredentials consumerAuthCredentials = database.readConsumerByKey(consumerKey).getAuthCredentials();
        AuthCredentials userAuthCredentials = principal.getOauthCredentials();
        URI baseUri = URI.create(request.getRequestURL().toString()).resolve(".");
        String serviceApiPath = baseUri.toASCIIString() + "api/users/default/services/" + connector.getName();
        WebTarget target = Endpoint.createWebTarget(serviceApiPath, consumerAuthCredentials, userAuthCredentials);
        Response clientResponse = target.request(MediaType.APPLICATION_XML).get();
        ServiceResponse serviceResponse = clientResponse.readEntity(ServiceResponse.class);
        writeIntoServletResponse(response, serviceResponse);
    }

    private void processServletRequest(HttpServletRequest request, HttpServletResponse response, Parameters parameters) throws ServletException, IOException {
        String redirectUrl = request.getContextPath() + "/view/services.xhtml";
        if (parameters.hasParameter(Parameters.ERROR)) {
            String error = parameters.get(Parameters.ERROR);
            redirectUrl += "?error=" + error;
        }
        response.sendRedirect(redirectUrl);
    }

    private void refineParameters(Parameters parameters) {
        Engine engine = Environment.getInstance().getEngine();
        for (ServiceConnector connector : engine.getConnectors()) {
            Parameters refinedParameters = connector.getRefinedCallbackParameters(parameters);
            parameters.add(refinedParameters, true);
        }
    }

    private void writeIntoServletResponse(HttpServletResponse servletResponse, XmlResponse xmlResponse) throws IOException {
        byte[] content = xmlResponse.toString().getBytes(StandardCharsets.UTF_8);
        OutputStream os = servletResponse.getOutputStream();
        os.write(content);
        os.close();
        servletResponse.setContentType(MediaType.APPLICATION_XML);
    }
}
