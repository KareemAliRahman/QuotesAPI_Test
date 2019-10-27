package com.QuotesAPI_Test;

import java.util.*; 
import org.testng.Assert;
import org.testng.annotations.*;

import static io.restassured.RestAssured.*;
import io.restassured.parsing.Parser;
import io.restassured.response.Response;
import static org.hamcrest.Matchers.*;


public class DeleteQuoteTest extends BaseTest {

    String quote, author, tags, quote_id, invalid_quote_id;

    @BeforeClass
    public void setup(){
        quote = generate(112);
        author = generate(10);
        tags = "confidence,management,inspire,worry";
        invalid_quote_id = "invalid_quote_id";

        //creating a new quote to test deletion scenario
        quote_id = 
            given()
                .accept("application/json")
                .header("X-TheySaidSo-Api-Secret",token)
                .queryParam("quote", quote)
                .queryParam("author", author)
                .queryParam("tags", tags)
                // .log().all()
            .when()
                .put(url+"quote")
            .then()
                .assertThat()
                .statusCode(200)
                .body("success.total",is(1))
                .body("content.quote.id",is(not(empty())))
            .extract().path("content.quote.id");

    }

    @Test(priority=1)
    public void deleteQuoteWithoutToken() {
        
        given()
            .accept("application/json")
            .queryParam("id", quote_id)
        .when()
            .delete(url+"quote")
        .then()
            .assertThat()
            .statusCode(401)
            .body("error.message",is("Unauthorized"));

    }


    @Test(priority=2)
    public void deleteQuoteWithoutId() {
        
        given()
            .accept("application/json")
            .header("X-TheySaidSo-Api-Secret",token)
            // .queryParam("id", quote_id)
        .when()
            .delete(url+"quote")
        .then()
            .assertThat()
            .statusCode(400)
            .body("error.message",is("Bad Request: id is missing."));
    }

    @Test(priority=3)
    public void deleteQuoteWithInvalidId() {
        
        given()
            .accept("application/json")
            .header("X-TheySaidSo-Api-Secret",token)
            .queryParam("id", invalid_quote_id)
        .when()
            .delete(url+"quote")
        .then()
            .assertThat()
            .statusCode(200)
            .body("failure.total", is(1))
            .body("contents", is("Something went wrong. Try again"));
    }

    @Test(priority=4)
    public void deleteQuoteWithValidId() {
        
        given()
            .accept("application/json")
            .header("X-TheySaidSo-Api-Secret",token)
            .queryParam("id", quote_id)
        .when()
            .delete(url+"quote")
        .then()
            .assertThat()
            .statusCode(200)
            .body("success.total",is(1))
            .body("contents",is("Quote deleted"));
    }

    
}
