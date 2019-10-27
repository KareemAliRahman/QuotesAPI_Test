package com.QuotesAPI_Test;

import java.util.*; 
import org.testng.Assert;
import org.testng.annotations.*;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;


public class UpdateQuoteTest extends BaseTest {

    String quote, updated_quote, author, tags, quote_id, invalid_quote_id;

    @BeforeClass
    public void setup(){
        quote = generate(112);
        updated_quote = generate(112);
        author = generate(10);
        tags = "confidence,management,inspire,worry";
        invalid_quote_id = "invalid_quote_id";
        quote_id = 
            given()
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

    @Test(priority=1)
    public void updateQuotewithoutToken() {
        
        given()
            .accept("application/json")
            .contentType("application/json")
            // .header("X-TheySaidSo-Api-Secret",token)
            .queryParam("quote", quote)
            .queryParam("quote_id", quote_id)
            .queryParam("author", author)
            .queryParam("tags", tags)
        .when()
            .patch(url+"quote")
        .then()
            .assertThat()
            .statusCode(401)
            // .body("error.code.",is(401))
            .body("error.message",is("Unauthorized"));
    }

    @Test(priority=2)
    public void updateQuotewithSameData() {
        
        given()
            .accept("application/json")
            .contentType("application/json")
            .header("X-TheySaidSo-Api-Secret",token)
            .queryParam("quote", quote)
            .queryParam("quote_id", quote_id)
            .queryParam("author", author)
            .queryParam("tags", tags)
        .when()
            .patch(url+"quote")
        .then()
            .assertThat()
            //I am expecting a 400 since we update a quote without any changes
            //Howerver system always respont with 401: unauthorized
            .statusCode(400);
    }

    @Test(priority=3)
    public void updateQuotewithInvalidId() {
        
        given()
            .accept("application/json")
            .contentType("application/json")
            .header("X-TheySaidSo-Api-Secret",token)
            .queryParam("quote", quote)
            //invalid quote id
            .queryParam("quote_id", invalid_quote_id)
            .queryParam("author", author)
            .queryParam("tags", tags)
        .when()
            .patch(url+"quote")
        .then()
            .assertThat()
            //I am expecting a 404 since we update a non existing quote
            //Howerver system always respont with 401: unauthorized
            .statusCode(404);
    }

    @Test(priority=4)
    public void updateQuotewithValidData() {
        
        given()
            .accept("application/json")
            .contentType("application/json")
            .header("X-TheySaidSo-Api-Secret",token)
            .queryParam("quote_id", quote_id)
            .queryParam("quote", updated_quote)
            .queryParam("author", author)
            .queryParam("tags", tags)
        .when()
            .patch(url+"quote")
        .then()
            .assertThat()
            //I am expecting a 200 since we update a quote with valid changes
            //Howerver system always respont with 401: unauthorized
            .statusCode(200);
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