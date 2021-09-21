package com.gahlot.learn.quarkus.codec;

import com.gahlot.learn.quarkus.Fruit;
import com.mongodb.MongoClientSettings;
import org.bson.*;
import org.bson.codecs.Codec;
import org.bson.codecs.CollectibleCodec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

import java.util.UUID;

import static com.gahlot.learn.quarkus.service.FruitService.DESC;
import static com.gahlot.learn.quarkus.service.FruitService.NAME;


// Takes care of transforming the entity to/from MongoDB Document. Using "CollectibleCodec" as the object is retrieved from DB
// More info on the codecs is at: https://mongodb.github.io/mongo-java-driver/3.10/bson/codecs
public class FruitCodec implements CollectibleCodec<Fruit> {

    private final Codec<Document> documentCodec;

    public FruitCodec() {
        this.documentCodec = MongoClientSettings.getDefaultCodecRegistry().get(Document.class);
    }

    @Override
    public Fruit generateIdIfAbsentFromDocument(Fruit fruit) {
        if (!documentHasId(fruit)) {
            fruit.setId(UUID.randomUUID().toString());
        }
        return fruit;
    }

    @Override
    public boolean documentHasId(Fruit fruit) {
        return fruit.getId() != null;
    }

    @Override
    public BsonValue getDocumentId(Fruit fruit) {
        return new BsonString(fruit.getId());
    }

    @Override
    public Fruit decode(BsonReader bsonReader, DecoderContext decoderContext) {
        Document document = documentCodec.decode(bsonReader, decoderContext);
        Fruit fruit = new Fruit(document.getString(NAME), document.getString(DESC));
        fruit.setId(document.getString("id"));

        return fruit;
    }

    @Override
    public void encode(BsonWriter bsonWriter, Fruit fruit, EncoderContext encoderContext) {
        Document doc = new Document();
        doc.put(NAME, fruit.getName());
        doc.put(DESC, fruit.getDescription());
        documentCodec.encode(bsonWriter, doc, encoderContext);
    }

    @Override
    public Class<Fruit> getEncoderClass() {
        return Fruit.class;
    }
}