package de.l3s.interwebj.tomcat.rest;

import jakarta.ws.rs.client.WebTarget;

import org.junit.jupiter.api.Test;

import de.l3s.interwebj.tomcat.TestUtils;
import de.l3s.interwebj.tomcat.jaxb.services.ServicesResponse;

class ServicesTest {
    @Test
    void testServices() {
        WebTarget target = TestUtils.createWebTarget("api/services", null);
        System.out.println("querying InterWebJ URL: " + target.toString());
        ServicesResponse servicesResponse = target.request().get(ServicesResponse.class);
        System.out.println(servicesResponse);
    }
}
