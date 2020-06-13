package de.l3s.interwebj.tomcat.rest;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.l3s.interwebj.core.AuthCredentials;
import de.l3s.interwebj.core.InterWebException;
import de.l3s.interwebj.core.connector.ServiceConnector;
import de.l3s.interwebj.core.core.Engine;
import de.l3s.interwebj.core.core.Environment;
import de.l3s.interwebj.core.core.InterWebPrincipal;
import de.l3s.interwebj.tomcat.jaxb.EmbeddedResponse;

@Path("/embedded")
public class Embedded extends Endpoint {
    private static final Logger log = LogManager.getLogger(Embedded.class);

    @Context
    HttpServletRequest request;

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public EmbeddedResponse getEmbedded(@FormParam("url") String url, @FormParam("max_width") int maxWidth, @FormParam("max_height") int maxHeight) {
        if (StringUtils.isEmpty(url)) {
            throw new WebApplicationException("URL must not by null or empty", Response.Status.BAD_REQUEST);
        }

        InterWebPrincipal principal = getPrincipal();
        AuthCredentials authCredentials = (principal == null) ? null : principal.getOauthCredentials();
        Engine engine = Environment.getInstance().getEngine();
        List<ServiceConnector> connectors = engine.getConnectors();
        String embedded = null;
        for (ServiceConnector connector : connectors) {
            if (connector.isRegistered()) {
                try {
                    log.info("querying connector: {}", connector.getName());
                    embedded = connector.getEmbedded(authCredentials, url, maxWidth, maxHeight);
                    if (embedded != null) {
                        break;
                    }
                } catch (InterWebException e) {
                    log.catching(e);
                }
            }
        }
        return new EmbeddedResponse(embedded);
    }
}
