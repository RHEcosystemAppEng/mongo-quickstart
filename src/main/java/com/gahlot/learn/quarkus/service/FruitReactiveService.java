package com.gahlot.learn.quarkus.service;

import com.gahlot.learn.quarkus.Fruit;
import io.quarkus.mongodb.reactive.ReactiveMongoClient;
import io.quarkus.mongodb.reactive.ReactiveMongoCollection;
import io.smallrye.mutiny.Uni;
import org.bson.Document;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

import static com.gahlot.learn.quarkus.service.FruitService.DESC;
import static com.gahlot.learn.quarkus.service.FruitService.NAME;

@ApplicationScoped
public class FruitReactiveService {
    @Inject
    ReactiveMongoClient mongoClient;

    public Uni<List<Fruit>> getFruits() {
        return getCollection().find()
                .map(doc -> new Fruit(doc.getString(NAME), doc.getString(DESC)))
                .collect().asList();
    }

    public Uni<Void> add(Fruit fruit) {
        Document document = new Document()
                .append(NAME, fruit.getName())
                .append(DESC, fruit.getDescription());
        return getCollection().insertOne(document).onItem().ignore().andContinueWithNull();
    }

    private ReactiveMongoCollection<Document> getCollection() {
        return mongoClient.getDatabase("fruit").getCollection("fruit");
    }
}