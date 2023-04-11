package courier;

import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;
import static io.restassured.RestAssured.given;

public class CourierClient {
    public static final String BASE_URI = "https://qa-scooter.praktikum-services.ru";
    public static final String COURIER_PATH = "/api/v1/courier/";
    public static final String LOGIN_COURIER_PATH = "/api/v1/courier/login/";
    private Courier courier;
    private static CourierCreds courierCreds;
    private int courierId;

    public CourierClient() {
        RestAssured.baseURI = BASE_URI;
    }

    public static ValidatableResponse createCourier(Courier courier) {
        return given()
                .header("Content-type", "application/json")
                .body(courier)
                .when()
                .post(COURIER_PATH)
                .then();
    }
    public static ValidatableResponse loginCourier(CourierCreds courierCreds) {
        return given()
                .header("Content-type", "application/json")
                .body(courierCreds)
                .when()
                .post(LOGIN_COURIER_PATH)
                .then();
    }
    public static ValidatableResponse deleteCourier(int courierId) {
        return given()
                .when()
                .delete(COURIER_PATH + courierId)
                .then();
    }
}
