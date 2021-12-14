package de.l3s.interwebj.tomcat.servlet.provider;

import java.net.URI;

import jakarta.inject.Singleton;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.PreMatching;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.Provider;

import org.apache.commons.lang3.StringUtils;

@Provider
@PreMatching
@Singleton
public class RequestUriFilter implements ContainerRequestFilter {

    @Override
    public void filter(ContainerRequestContext request) {
        MultivaluedMap<String, String> headers = request.getHeaders();
        UriInfo uriInfo = request.getUriInfo();
        if (headers.containsKey("X-Forwarded-Host") || headers.containsKey("X-Forwarded-Proto")) {
            String host = getHost(request);
            int port = -1;
            if (host.contains(":")) {
                port = Integer.parseInt(StringUtils.substringAfter(host, ":"));
                host = StringUtils.substringBefore(host, ":");
            }

            URI baseUri = uriInfo.getBaseUriBuilder()
                .host(host)
                .scheme(getScheme(request))
                .port(port)
                .build();
            URI requestUri = uriInfo.getRequestUriBuilder()
                .host(host)
                .scheme(getScheme(request))
                .port(port)
                .build();
            request.setRequestUri(baseUri, requestUri);
        } else if (uriInfo.getBaseUri().getPort() == 80 || uriInfo.getBaseUri().getPort() == 443) {
            request.setRequestUri(uriInfo.getBaseUriBuilder().port(-1).build(), uriInfo.getRequestUriBuilder().port(-1).build());
        }
    }

    private String getHost(ContainerRequestContext request) {
        String host = request.getHeaderString("X-Forwarded-Host");
        if (StringUtils.isBlank(host)) {
            host = request.getUriInfo().getBaseUri().getHost();
        } else if (host.contains(",")) {
            host = StringUtils.substringBefore(host, ",");
        }
        return host;
    }

    private String getScheme(ContainerRequestContext request) {
        String protocol = request.getHeaderString("X-Forwarded-Proto");
        if (StringUtils.isBlank(protocol)) {
            protocol = request.getUriInfo().getBaseUri().getScheme();
        } else if (protocol.contains(",")) {
            protocol = StringUtils.substringBefore(protocol, ",");
        }
        return protocol;
    }
}