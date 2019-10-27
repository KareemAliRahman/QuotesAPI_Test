package com.QuotesAPI_Test;

import java.util.*;

import org.testng.Assert;
import org.testng.annotations.*;

import static io.restassured.RestAssured.*;
import io.restassured.parsing.Parser;
import static org.hamcrest.Matchers.*;



public class SearchQuoteTest extends BaseTest {

    String quote, quote_id, author, minlength, maxlength, query, privateParam, category;
    String[] categories;

    @BeforeClass
    public void setup(){
        author = "";
        minlength = "100";
        maxlength = "300";
        query = "";
        privateParam = "false";

        author = generate(10);
        quote = generate(200);
        category = "inspire";
        categories = new String[] {"inspire"};
        
        //creating a new quote to test Search scenario
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
    public void searchQuoteWithoutToken() {
        
        given()
            .accept("application/json")
            // .header("X-TheySaidSo-Api-Secret",token)
            .queryParam("author", author)
            .queryParam("category", category)
            .queryParam("minlength",minlength)
            .queryParam("maxlength",maxlength)
            .queryParam("query",query)
            .queryParam("private",privateParam)
        .when()
            .get(url+"quote/search")
        .then()
            .assertThat()
            .statusCode(401)
            .body("error.message",is("Unauthorized"));

    }

    @Test(priority=2)
    public void searchQuoteWithMinGreaterThanMax() {

        given()
            .accept("application/json")
            .header("X-TheySaidSo-Api-Secret",token)
            .queryParam("author", author)
            //maxlength and minlength are mixed up
            .queryParam("minlength",maxlength)
            .queryParam("maxlength",minlength)
            .queryParam("private",privateParam)
        .when()
            .get(url+"quote/search")
        .then()
            .assertThat()
            .statusCode(404)
            .body("error.message",
                is("Not Found: No Quote found matching the search filters. Try with different serach filters."));

    }

    @Test(priority=5)
    public void searchQuoteNotPrivate() {
        //seatch in private collection
        privateParam = "true";
        //generate random author, should not exist in private collection
        String author_temp = generate(10);
        
        given()
            .accept("application/json")
            .header("X-TheySaidSo-Api-Secret",token)
            .queryParam("author", author_temp)
            .queryParam("minlength",minlength)
            .queryParam("maxlength",maxlength)
            .queryParam("private",privateParam)
        .when()
            .get(url+"quote/search")
        .then()
            .assertThat()
            .statusCode(404)
            .body("error.message",
                is("Not Found: No Quote found matching the search filters. Try with different serach filters."));

    }

    @Test(priority=3)
    public void searchQuotePublic() {
        
        given()
            .accept("application/json")
            .header("X-TheySaidSo-Api-Secret",token)
            // .queryParam("author", author)
            .queryParam("category", category)
            .queryParam("minlength",minlength)
            .queryParam("maxlength",maxlength)
            // .queryParam("query",query)
            .queryParam("private",privateParam)
        .when()
            .get(url+"quote/search")
        .then()
            .assertThat()
            .statusCode(200)
            .body("success.total",is(1))
            .body("contents.quote", is(not(empty())))
            .body("contents.author", is(not(empty())))
            .body("contents.id", is(not(empty())))
            .body("contents.permalink", is(not(empty())))
            .body("contents.categories", is(notNullValue()));

    }

    @Test(priority=4)
    public void searchQuotePrivate(){

        //search in private collection
        privateParam = "true";

        //executing the test
        given()
            .accept("application/json")
            .header("X-TheySaidSo-Api-Secret",token)
            .queryParam("author", author)
            .queryParam("minlength",minlength)
            .queryParam("maxlength",maxlength)
            .queryParam("private",privateParam)
        .when()
            .get(url+"quote/search")
        .then()
            .assertThat()
            .statusCode(200)
            .body("success.total",is(1))
            .body("contents.quote", is(quote))
            .body("contents.author", is(author))
            .body("contents.id", is(quote_id))
            .body("contents.permalink", is(not(empty())))
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