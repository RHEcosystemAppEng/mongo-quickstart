package com.redhat.sample.quarkus.codec;

import com.redhat.sample.quarkus.Fruit;
import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;

// Links our Codec to Fruit class
// Quarkus takes care of registering the CodecProvider
public class FruitCodecProvider implements CodecProvider {
    @Override
    public <T> Codec<T> get(Class<T> clazz, CodecRegistry registry) {
        if (clazz == Fruit.class) {
            return (Codec<T>) new FruitCodec();
        }
        return null;
    }
}