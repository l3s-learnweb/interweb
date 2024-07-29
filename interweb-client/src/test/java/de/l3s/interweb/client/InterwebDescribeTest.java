package de.l3s.interweb.client;

import static org.junit.jupiter.api.Assertions.*;

import jakarta.inject.Inject;

import io.quarkus.test.junit.QuarkusTest;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import de.l3s.interweb.core.describe.DescribeQuery;
import de.l3s.interweb.core.describe.DescribeResults;

@Disabled
@QuarkusTest
class InterwebDescribeTest {

    private final Interweb interweb;

    @Inject
    public InterwebDescribeTest(@ConfigProperty(name = "interweb.server") String server, @ConfigProperty(name = "interweb.apikey") String apikey) {
        this.interweb = new Interweb(server, apikey);
    }

    @Test
    void describeTest() throws InterwebException {
        DescribeQuery query = new DescribeQuery();
        // query.setLink("https://vimeo.com/524933864");
        query.setId("524933864");
        query.setServices("vimeo");

        DescribeResults response = interweb.describe(query);

        assertEquals("524933864", response.getEntity().getId());
        assertEquals("Vimeo | Video Power", response.getEntity().getTitle());
        assertEquals("https://vimeo.com/524933864", response.getEntity().getUrl());
    }
}
