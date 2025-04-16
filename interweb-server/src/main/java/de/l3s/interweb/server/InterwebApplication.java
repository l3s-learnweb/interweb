package de.l3s.interweb.server;

import jakarta.ws.rs.core.Application;

import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Contact;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.info.License;

@OpenAPIDefinition(
    info = @Info(
        title = "Interweb API",
        version = "4.0.0",
        description = "Unified API for Information Retrieval.",
        contact = @Contact(
            name = "Learnweb Team",
            email = "learnweb-support@l3s.de"),
        license = @License(name = "The MIT License", url = "https://opensource.org/licenses/MIT")
    )
)
public class InterwebApplication extends Application {
}
