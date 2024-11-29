package de.l3s.interweb.server.features.models;

import java.util.List;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import de.l3s.interweb.core.ObjectWrapper;
import de.l3s.interweb.core.models.Model;
import de.l3s.interweb.server.Roles;

@Tag(name = "Models", description = "List all available models")
@Path("/models")
@RolesAllowed({Roles.APPLICATION})
public class ModelsResource {

    @Inject
    ModelsService modelsService;

    @GET
    public Uni<ObjectWrapper<List<Model>>> list() {
        return modelsService.getModels().map(models -> new ObjectWrapper<>("list", models));
    }

    @GET
    @Path("{model}")
    public Uni<Model> get(@PathParam("model") String model) {
        return modelsService.getModel(model);
    }
}
