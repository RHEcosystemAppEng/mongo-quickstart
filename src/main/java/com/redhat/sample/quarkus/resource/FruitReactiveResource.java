package com.redhat.sample.quarkus.resource;

import com.redhat.sample.quarkus.Fruit;
import com.redhat.sample.quarkus.service.FruitReactiveService;

import io.smallrye.mutiny.Uni;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/fruits_reactive")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class FruitReactiveResource {
    @Inject
    FruitReactiveService fruitService;

    @GET
    public Uni<List<Fruit>> getFruits() {
        return fruitService.getFruits();
    }

    @POST
    public Uni<List<Fruit>> add(Fruit fruit) {
        return fruitService.add(fruit).onItem().ignore().andSwitchTo(this::getFruits);
    }
}