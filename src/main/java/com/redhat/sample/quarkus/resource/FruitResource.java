package com.redhat.sample.quarkus.resource;

import com.redhat.sample.quarkus.Fruit;
import com.redhat.sample.quarkus.service.FruitService;
import org.jboss.logging.Logger;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import java.util.List;

@Path("/fruits")
public class FruitResource {

    @Inject
    FruitService fruitService;

    @Inject
    Logger logger;

    @GET
    public List<Fruit> getFruits() {
        logger.info("Retrieving fruits list: new");
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