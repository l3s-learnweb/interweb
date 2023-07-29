package de.l3s.interweb.server;

import jakarta.ws.rs.core.Application;

import org.eclipse.microprofile.openapi.annotations.Components;
import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeType;
import org.eclipse.microprofile.openapi.annotations.info.Contact;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.info.License;
import org.eclipse.microprofile.openapi.annotations.security.SecurityScheme;

@OpenAPIDefinition(
        info = @Info(
                title = "Interweb API",
                version = "4.0.0",
                contact = @Contact(
                        name = "Learnweb Team",
                        url = "https://learnweb.l3s.uni-hannover.de/lw/contact.jsf",
                        email = "learnweb-support@l3s.de"),
                license = @License(name = "The MIT License", url = "https://opensource.org/licenses/MIT")
        ),
        components = @Components(
                securitySchemes = {
                        @SecurityScheme(
                                securitySchemeName = "JWT",
                                description = "Use /users/login to obtain a JWT token.",
                                type = SecuritySchemeType.HTTP,
                                scheme = "bearer",
                                bearerFormat = "JWT"
                        )
                }
        )
)
public class InterwebApplication extends Application {
}
