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