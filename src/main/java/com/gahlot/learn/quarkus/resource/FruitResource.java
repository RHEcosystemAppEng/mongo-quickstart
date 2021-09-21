package com.gahlot.learn.quarkus.resource;

import com.gahlot.learn.quarkus.Fruit;
import com.gahlot.learn.quarkus.service.FruitService;
import org.jboss.logging.Logger;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/fruits")
public class FruitResource {

    @Inject
    FruitService fruitService;

    @Inject
    Logger logger;

    @GET
    public List<Fruit> getFruits() {
        logger.info("Retrieving fruits list");
        List<Fruit> fruits = fruitService.getFruits();
        logger.info("Fruits -> " + fruits);
        return fruits;
    }

    @POST
    public List<Fruit> add(Fruit fruit) {
        logger.info("Adding a new fruit -> " + fruit);
        fruitService.add(fruit);
        logger.info("Calling getFruits to return the list...");
        return getFruits();
    }

}