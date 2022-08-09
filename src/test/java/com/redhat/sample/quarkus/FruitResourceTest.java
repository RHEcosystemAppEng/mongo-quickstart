package com.redhat.sample.quarkus;

import static io.restassured.RestAssured.given;

import java.util.Arrays;
import java.util.List;

import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.google.common.collect.Lists;
import com.redhat.sample.quarkus.service.FruitService;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.restassured.response.Response;

@QuarkusTest
public class FruitResourceTest {

    @InjectMock
    FruitService fruitService;

    @Test
    public void verifyFruitList() {
        Mockito.when(fruitService.getFruits()).thenReturn(Lists.newArrayList(
                new Fruit("Apple", "An apple a day keeps the doctor away"),
                new Fruit("Banana", "Fills you up")
                )
        );

        Response response = given()
                .when().get("/fruits");

        response.then()
                .log().all()
                .statusCode(HttpStatus.SC_OK);

        List<Fruit> fruitList = Arrays.asList(response.as(Fruit[].class));
        Assertions.assertEquals(2, fruitList.size());
        Assertions.assertEquals("Apple", fruitList.get(0).getName());
        Assertions.assertEquals("Banana", fruitList.get(1).getName());
    }

}