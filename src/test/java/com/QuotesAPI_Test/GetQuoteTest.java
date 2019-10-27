package com.QuotesAPI_Test;

import java.util.*; 
import org.testng.Assert;
import org.testng.annotations.*;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;


public class GetQuoteTest extends BaseTest {

    String quote, author, quote_id, invalid_quote_id, category;
    String[] categories;

    @BeforeClass
    public void setup(){
        quote = generate(112);
        author = generate(10);
        category = "confidence,management";
        categories = new String[]{"confidence","management"};
        invalid_quote_id = "invalid_quote_id";

        //creating a new quote to test get scenario
        quote_id = 
            given()
                .accept("application/json")
                .header("X-TheySaidSo-Api-Secret",token)
                .queryParam("quote", quote)
                .queryParam("author", author)
                .queryParam("tags", category)
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
    public void getQuoteWithoutToken() {
        
        given()
            .accept("application/json")
            // .header("X-TheySaidSo-Api-Secret",token)
            .queryParam("id", quote_id)
        .when()
            .get(url+"quote")
        .then()
            .assertThat()
            .statusCode(401)
            .body("error.message",is("Unauthorized"));
    }

    @Test(priority=2)
    public void getQuoteWithoutId() {
        
        given()
            .accept("application/json")
            .header("X-TheySaidSo-Api-Secret",token)
            // .queryParam("id", quote_id)
        .when()
            .get(url+"quote")
        .then()
            .assertThat()
            .statusCode(200)
            .body("success.total", is(1))
            .body("contents.quote", is(not(quote)))
            // .body("contents.author", is(not(author)))
            .body("contents.id", is(not(quote_id)));
    }

    @Test(priority=3)
    public void getQuoteWithInvalidId() {
        
        given()
            .accept("application/json")
            .header("X-TheySaidSo-Api-Secret",token)
            .queryParam("id", invalid_quote_id)
        .when()
            .get(url+"quote")
        .then()
            .assertThat()
            .statusCode(404)
            .body("error.message", is("Not Found: Quote not found"));
    }


    @Test(priority=4)
    public void getQuoteWithValidId() {
        
        given()
            .accept("application/json")
            .header("X-TheySaidSo-Api-Secret",token)
            .queryParam("id", quote_id)
        .when()
            .get(url+"quote")
        .then()
            .assertThat()
            .statusCode(200)
            .body("success.total", is(1))
            .body("contents.quote", is(quote))
            .body("contents.author", is(author))
            .body("contents.id", is(quote_id))
            .body("contents.categories", hasItems(categories));
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