package ordertests;

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
    private CourierClient courierClient =  new CourierClient();
    private RandomCourierGenerator courierGenerator =  new RandomCourierGenerator();
    private Order order;
    private OrderClient orderClient =  new OrderClient();
    private RandomOrderGenerator orderGenerator =  new RandomOrderGenerator();
    private int courierId;
    private int track;
    private int orderId;
    private int orderIdInOrders;

    @Before
    public void setUp() {
        courier = new Courier()
                .setLogin(courierGenerator.getLogin())
                .setPassword(courierGenerator.getPassword())
                .setFirstName(courierGenerator.getFirstName());
        order = new Order()
                .setFirstName(orderGenerator.getFirstName())
                .setLastName(orderGenerator.getLastName())
                .setAddress(orderGenerator.getAddress())
                .setMetroStation(orderGenerator.getMetroStation())
                .setPhone(orderGenerator.getPhone())
                .setRentTime(orderGenerator.getRentTime())
                .setDeliveryDate(orderGenerator.getDeliveryDate())
                .setComment(orderGenerator.getComment())
                .setColor(orderGenerator.getColor());

        courierClient.createCourier(courier);
        courierId = courierClient.loginCourier(credsFrom(courier)).extract().path("id");
        track = orderClient.createOrder(order).extract().path("track");
        orderId = JsonPath.from(orderClient.getOrder(track).extract().asPrettyString()).getInt("order.id");
        orderClient.acceptOrder(courierId, orderId);
    }

    @Test
    @DisplayName("get orders and check response body")
    @Description("Проверить, что в тело ответа возвращается список заказов.")
    public void getOrdersAndCheckResponse() {
        ValidatableResponse getOrdersResponse = orderClient.getOrders(courierId);
        orderIdInOrders = JsonPath.from(getOrdersResponse.extract().asPrettyString()).getInt("orders[0].id");
        assertEquals("Неверный статус код",
                HttpStatus.SC_OK,
                getOrdersResponse.extract().statusCode());
        assertEquals("в списке заказов нет созданного заказа", orderId, orderIdInOrders);
    }

    @After
    public void tearDown() {
        if (orderId != 0) {
            orderClient.finishOrder(orderId);
        }

        if (courierId != 0) {
            courierClient.deleteCourier(courierId);
        }
    }

}