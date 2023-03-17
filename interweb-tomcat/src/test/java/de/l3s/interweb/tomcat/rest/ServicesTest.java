package de.l3s.interweb.tomcat.rest;

import jakarta.ws.rs.client.WebTarget;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import de.l3s.interweb.tomcat.TestUtils;
import de.l3s.interweb.tomcat.jaxb.services.ServicesResponse;

@Disabled
class ServicesTest {
    @Test
    void testServices() {
        WebTarget target = TestUtils.createWebTarget("api/services", null);
        System.out.println("querying InterWeb URL: " + target);
        ServicesResponse servicesResponse = target.request().get(ServicesResponse.class);
        System.out.println(servicesResponse);
    }
}
