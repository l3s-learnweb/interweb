package de.l3s.interweb.server.features.models;

import java.util.List;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

import io.quarkus.security.Authenticated;
import io.smallrye.mutiny.Uni;

import de.l3s.interweb.core.ObjectWrapper;
import de.l3s.interweb.core.models.Model;

@Path("/models")
public class ModelsResource {

    @Inject
    ModelsService modelsService;

    @GET
    @Authenticated
    public Uni<ObjectWrapper<List<Model>>> models() {
        return modelsService.getModels().map(models -> new ObjectWrapper<>("list", models));
    }

    @GET
    @Authenticated
    @Path("{model}")
    public Uni<Model> chat(@PathParam("model") String model) {
        return modelsService.getModel(model);
    }
}
