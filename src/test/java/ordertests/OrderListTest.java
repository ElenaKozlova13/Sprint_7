package ordertests;

import com.google.gson.Gson;
import courier.Courier;
import courier.CourierClient;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ValidatableResponse;
import order.Order;
import order.OrderClient;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import utils.RandomCourierGenerator;
import utils.RandomOrderGenerator;

import static courier.CourierCreds.credsFrom;
import static org.junit.Assert.assertEquals;

public class OrderListTest {
    private Courier courier;
    private CourierClient courierClient;
    private int courierId;
    private Order order;
    private OrderClient orderClient;
    private int track;
    private int orderId;
    private int orderIdInOrders;

    @Before
    public void setUp() {
        Gson gson = new Gson();
        courierClient = new CourierClient();
        courier = new Courier()
                .setLogin(RandomCourierGenerator.getLogin())
                .setPassword(RandomCourierGenerator.getPassword())
                .setFirstName(RandomCourierGenerator.getFirstName());
        orderClient = new OrderClient();
        order = new Order()
                .setFirstName(RandomOrderGenerator.getFirstName())
                .setLastName(RandomOrderGenerator.getLastName())
                .setAddress(RandomOrderGenerator.getAddress())
                .setMetroStation(RandomOrderGenerator.getMetroStation())
                .setPhone(RandomOrderGenerator.getPhone())
                .setRentTime(RandomOrderGenerator.getRentTime())
                .setDeliveryDate(RandomOrderGenerator.getDeliveryDate())
                .setComment(RandomOrderGenerator.getComment())
                .setColor(RandomOrderGenerator.getColor());

        ValidatableResponse createCourierResponse = courierClient.createCourier(courier).statusCode(HttpStatus.SC_CREATED);
        ValidatableResponse loginCourierResponse = courierClient.loginCourier(credsFrom(courier)).statusCode(HttpStatus.SC_OK);
        courierId = loginCourierResponse.extract().path("id");
        ValidatableResponse createOrderResponse = orderClient.createOrder(order).statusCode(HttpStatus.SC_CREATED);
        track = createOrderResponse.extract().path("track");
        ValidatableResponse getOrderResponse = orderClient.getOrder(track).statusCode(HttpStatus.SC_OK);
        orderId = JsonPath.from(getOrderResponse.extract().asPrettyString()).getInt("order.id");
        ValidatableResponse acceptOrderResponse = orderClient.acceptOrder(courierId, orderId).statusCode(HttpStatus.SC_OK);

    }

    @Test
    @DisplayName("get orders and check response body") // имя теста
    @Description("Проверить, что в тело ответа возвращается список заказов.") // описание теста
    public void getOrdersAndCheckResponse() {
        ValidatableResponse getOrdersResponse = orderClient.getOrders(courierId);
        assertEquals("Неверный статус код",
                HttpStatus.SC_OK,
                getOrdersResponse.extract().statusCode());
        orderIdInOrders = JsonPath.from(getOrdersResponse.extract().asPrettyString()).getInt("orders[0].id");
        assertEquals("в списке заказов нет созданного заказа", orderId, orderIdInOrders);
    }

    @After
    public void tearDown() {
        if (orderId != 0) {
            orderClient.finishOrder(orderId).statusCode(HttpStatus.SC_OK);
        }

        if (courierId != 0) {
            courierClient.deleteCourier(courierId).statusCode(HttpStatus.SC_OK);
        }
    }

}