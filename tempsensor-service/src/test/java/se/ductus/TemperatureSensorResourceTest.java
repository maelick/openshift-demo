package se.ductus;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.ws.rs.core.MediaType;
import org.junit.jupiter.api.Test;
import se.ductus.tempsensor.api.models.TemperatureResponse;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
class TemperatureSensorResourceTest {
    @Test
    void testTemperatureEndpoint() {
        given()
                .when().get("/temperature-sensor/temperature")
                .then()
                .statusCode(200)
                .body(is("{\"celsius\":0.0}"));
    }

    @Test
    void testHeatingEndpoint() {
        given().contentType("application/json")
                .when().put("/temperature-sensor/heating")
                .then()
                .statusCode(400);
    }
}