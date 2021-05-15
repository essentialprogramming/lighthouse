package com.lighthouse;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;

public class LightHouseTest {

    static String accessToken;

    @BeforeAll()
    public static void setup() {
        RestAssured.baseURI = "https://lighthouse-demoapi.evozon.com";

        ObjectMapper objectMapper = new ObjectMapper();

        // Creating Node that maps to JSON Object structures in JSON content
        ObjectNode userDetails = objectMapper.createObjectNode();

        userDetails.put("username", "razvan.prichici@stud.ubbcluj.ro");
        userDetails.put("password", "Aa#123456");

        RestAssured.basePath = "/login";
        accessToken = given()
                        .accept(ContentType.JSON)
                        .contentType(ContentType.JSON)
                        .body(userDetails)
                     .when()
                          .post()
                     .then()
                         .assertThat()
                         .statusCode(200)
                         .extract()
                         .path("token");

        System.out.println(accessToken);
    }

    @AfterAll
    public static void afterAll() {
        RestAssured.reset();
    }

    @Test
    public void givenNoAuthentication_whenRequestSecuredResource_thenUnauthorizedResponse() {
        RestAssured.basePath = "/api";
        get("/v1/roles").then()
                .assertThat()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    public void whenAuthenticated_ask_user_roles() {
        RestAssured.basePath = "/api";
        given().
                  accept(ContentType.JSON).
                  contentType(ContentType.JSON).
                  header("Authorization",  accessToken).
        when().
                  get("/v1/roles").
        then().
                  assertThat().
                  statusCode(HttpStatus.OK.value()).
        and().
                  contentType(ContentType.JSON).
        and().
                  body("ROLE_API_ADMINISTRATOR", equalTo("Administrator")).
        log().all();


    }

    @Test
    public void create_visitor_account_check_new_account_is_loaded() {

        ObjectMapper objectMapper = new ObjectMapper();

        // Creating Node that maps to JSON Object structures in JSON content
        ObjectNode userDetails = objectMapper.createObjectNode();

        final String email = NanoIdUtils.randomNanoId()+"@stud.ubbcluj.ro";
        userDetails.put("username",email );
        userDetails.put("first_name", "Test");
        userDetails.put("last_name", "User");
        userDetails.put("email", email);

        Integer id;

        RestAssured.basePath = "/api";
        id = given().
                accept(ContentType.JSON).
                contentType(ContentType.JSON).
                header("Authorization",  accessToken).
                body(userDetails).
        when().
                post("/v1/users").
        then().
                assertThat().
                statusCode(HttpStatus.CREATED.value()).
        and().
                contentType(ContentType.JSON).
        and().
                body("email", equalTo(email)).
         log().all().
         extract()
                .path("id");




        given().
                accept(ContentType.JSON).
                contentType(ContentType.JSON).
                header("Authorization",  accessToken).
        when().
                get("/v1/users?guest=1&page=1&size=100").
        then().
                assertThat().
                statusCode(HttpStatus.OK.value()).
        and().
                contentType(ContentType.JSON).
        and().
                body("data",
                        hasItem(
                                allOf(
                                        hasEntry("email", email)
                                )
                        )
                ).
        log().all();




        given().
                accept(ContentType.JSON).
                contentType(ContentType.JSON).
                header("Authorization",  accessToken).
        when().
                delete("/v1/users/" + id).
        then().
                assertThat().
                statusCode(HttpStatus.OK.value());

    }


    @Test
    public void retrieve_checkin_data_for_a_specific_date_range_boundary_test() {
        RestAssured.basePath = "/api";

        given().
                accept(ContentType.JSON).
                contentType(ContentType.JSON).
                header("Authorization",  accessToken).
                param("from", LocalDate.now().minusYears(1).format(DateTimeFormatter.BASIC_ISO_DATE)).
                param("to",   LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE)).
        when().
                get("/v1/checkins").
        then().
                assertThat().
                statusCode(HttpStatus.OK.value()).
        and().
                contentType(ContentType.JSON).
        and().
                body("data.keySet()", everyItem(greaterThanOrEqualTo(LocalDate.now().minusYears(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))))).
                body("data.keySet()", everyItem(lessThanOrEqualTo(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))))).
                log().all();

    }

    @Test
    public void retrieve_checkin_data_for_a_specific_invalid_date_range_boundary_test() {
        RestAssured.basePath = "/api";

        given().
                accept(ContentType.JSON).
                contentType(ContentType.JSON).
                header("Authorization",  accessToken).
                param("from", LocalDate.now().minusDays(1).format(DateTimeFormatter.BASIC_ISO_DATE)).
                param("to",   LocalDate.now().with(TemporalAdjusters.lastDayOfMonth()).plusDays(1).format(DateTimeFormatter.BASIC_ISO_DATE)).
                when().
                get("/v1/checkins").
                then().
                assertThat().
                statusCode(HttpStatus.OK.value()).
                and().
                contentType(ContentType.JSON).
                and().
                body("data.keySet()", everyItem(greaterThanOrEqualTo(LocalDate.now().minusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))))).
                body("data.keySet()", everyItem(lessThanOrEqualTo( LocalDate.now().with(TemporalAdjusters.lastDayOfMonth()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))))).
                log().all();

    }
}
