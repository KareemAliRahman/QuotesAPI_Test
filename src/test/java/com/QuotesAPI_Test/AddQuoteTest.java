package com.QuotesAPI_Test;

import java.util.*; 
import org.testng.Assert;
import org.testng.annotations.*;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;


public class AddQuoteTest extends BaseTest{

    String quote, author, tags, quote_id;

    @BeforeClass
    public void setup(){
        quote = generate(112);
        author = generate(10);
        tags = "confidence,management";
    }

    @Test(priority=1)
    public void addQuoteWithoutToken() {
        
        given()
            .accept("application/json")
            // .header("X-TheySaidSo-Api-Secret",token)
            .queryParam("quote", quote)
            .queryParam("author", author)
            .queryParam("tags", tags)
        .when()
            .put(url+"quote")
        .then()
            .assertThat()
            .statusCode(401)
            // .body("error.code.",is(401))
            .body("error.message",is("Unauthorized"));
    }

    @Test(priority=2)
    public void addQuoteWithoutQuote() {
        
        given()
            .accept("application/json")
            .header("X-TheySaidSo-Api-Secret",token)
            // .queryParam("quote", quote)
            .queryParam("author", author)
            // .queryParam("tags", tags)
        .when()
            .put(url+"quote")
        .then()
            .assertThat()
            .statusCode(400)
            // .body("error.code.",is(401))
            .body("error.message",is("Bad Request: quote is missing."));
    } 

    @Test(priority=3)
    public void addQuoteWithoutAuthor() {
        
        given()
            .accept("application/json")
            .header("X-TheySaidSo-Api-Secret",token)
            .queryParam("quote", quote)
            // .queryParam("author", author)
            // .queryParam("tags", tags)
        .when()
            .put(url+"quote")
        .then()
            .assertThat()
            .statusCode(400)
            // .body("error.code.",is(401))
            .body("error.message",is("Bad Request: author is missing."));
    }

    @Test(priority=4)
    public void addQuoteWithValidData() {
        
        quote_id = given()
                .accept("application/json")
                .header("X-TheySaidSo-Api-Secret",token)
                .queryParam("quote", quote)
                .queryParam("author", author)
                .queryParam("tags", tags)
            .when()
                .put(url+"quote")
            .then()
                .assertThat()
                .statusCode(200)
                .body("success.total",is(1))
                .body("content.quote.id",is(not(empty())))
                .extract().path("content.quote.id");
    }

    @AfterClass
    public void tearDown(){

        //deleting the quote generated for the test
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