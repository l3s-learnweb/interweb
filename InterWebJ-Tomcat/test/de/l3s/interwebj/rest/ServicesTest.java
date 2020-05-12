package de.l3s.interwebj.rest;

import de.l3s.interwebj.jaxb.services.ServicesResponse;
import de.l3s.interwebj.util.TestUtils;
import org.junit.jupiter.api.Test;

import javax.ws.rs.client.WebTarget;

class ServicesTest {
    @Test
    void testServices() {
        WebTarget target = TestUtils.createWebTarget("api/services", null);
        System.out.println("querying InterWebJ URL: " + target.toString());
        ServicesResponse servicesResponse = target.request().get(ServicesResponse.class);
        System.out.println(servicesResponse);
    }
}