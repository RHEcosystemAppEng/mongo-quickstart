package com.gahlot.learn.quarkus.service;

import com.gahlot.learn.quarkus.Fruit;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import org.bson.Document;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class FruitService {
    public static final String NAME = "name";
    public static final String DESC = "description";

    @Inject
    MongoClient mongoClient;

    public List<Fruit> getFruits() {
        ArrayList<Fruit> fruits = new ArrayList<>();
        MongoCursor<Document> cursor = getCollection().find().iterator();

        try {
            while (cursor.hasNext()) {
                Document document = cursor.next();
                fruits.add(new Fruit(document.getString(NAME), document.getString(DESC)));
            }
        } finally {
            cursor.close();
        }

        return fruits;
    }

    public void add(Fruit fruit) {
        Document document = new Document()
                .append(NAME, fruit.getName())
                .append(DESC, fruit.getDescription());
        getCollection().insertOne(document);
    }

    private MongoCollection getCollection() {
        return mongoClient.getDatabase("fruit").getCollection("fruit");
    }

}