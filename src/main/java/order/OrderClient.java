package order;

import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;

import static io.restassured.RestAssured.given;

public class OrderClient {
    public static final String BASE_URI = "https://qa-scooter.praktikum-services.ru";
    public static final String ORDER_PATH = "/api/v1/orders/";
    public static final String GET_ORDER_PATH = "/api/v1/orders/track";
    public static final String CANCEL_ORDER_PATH = "/api/v1/orders/cancel/";
    public static final String ACCEPT_ORDER_PATH = "/api/v1/orders/accept/";
    public static final String FINISH_ORDER_PATH = "/api/v1/orders/finish/";

    private Order order;
    private int courierId;
    private int orderId;

    public OrderClient() {
        RestAssured.baseURI = BASE_URI;
    }
    @Step("Создание заказа")
    public ValidatableResponse createOrder(Order order) {
        return given()
                .header("Content-type", "application/json")
                .body(order)
                .when()
                .post(ORDER_PATH)
                .then();
    }
    @Step("Отмена заказа")
    public ValidatableResponse cancelOrder(int track) {
        return given()
                .queryParam("track", track)
                .when()
                .put(CANCEL_ORDER_PATH)
                .then();
    }
    @Step("Завершение заказа")
    public ValidatableResponse finishOrder(int orderId) {
        return given()
                .when()
                .put(FINISH_ORDER_PATH + orderId)
                .then();
    }
    @Step("Получение заказа по трэк номеру")
    public ValidatableResponse getOrder(int track) {
        return given()
            .queryParam("t", track)
            .when()
            .get(GET_ORDER_PATH)
            .then();
    }
    @Step("Назначение заказа на курьера по Id заказа и курьера")
    public ValidatableResponse acceptOrder(int courierId, int orderId) {
        return given()
                .param("courierId", courierId)
                .when()
                .put(ACCEPT_ORDER_PATH + orderId)
                .then();
    }
    @Step("Получение списка заказов курьера по Id курьера")
    public ValidatableResponse getOrders(int courierId) {
        return given()
                .queryParam("courierId", courierId)
                .when()
                .get(ORDER_PATH)
                .then();
    }
}
