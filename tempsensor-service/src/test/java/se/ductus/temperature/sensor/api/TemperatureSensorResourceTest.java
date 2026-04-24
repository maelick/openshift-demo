package se.ductus.temperature.sensor.api;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.matchesPattern;

@QuarkusTest
class TemperatureSensorResourceTest {
    @Test
    void testTemperatureEndpoint() {
        given()
                .when().get("/temperature-sensor/temperature")
                .then()
                .statusCode(200)
                .body(matchesPattern("\\{\"celsius\":.*}"));
    }

    @Test
    void testHeatingEndpoint() {
        given().contentType("application/json")
                .when().put("/temperature-sensor/heating")
                .then()
                .statusCode(400);

        given().contentType("application/json")
                .body("\"not-a-json-object\"")
                .when().put("/temperature-sensor/heating")
                .then()
                .statusCode(400);

        given().contentType("application/json")
                .body("{}")
                .when().put("/temperature-sensor/heating")
                .then()
                .statusCode(204);
    }
}