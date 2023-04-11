package ordertests;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import order.Order;
import order.OrderClient;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import utils.RandomOrderGenerator;
import java.util.List;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(Parameterized.class)
public class CreateOrderTest {

    private Order order;
    private OrderClient orderClient;
    private int track;
    private List<String> colorList;
    private int expectedStatusCode;

    public CreateOrderTest(List<String> colorList, int expectedStatusCode) {
        this.colorList = colorList;
        this.expectedStatusCode = expectedStatusCode;
    }

    @Before
    public void setUp() {
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
                .setColor(colorList)
        ;
    }

    @Parameterized.Parameters
    public static Object[][] getColorData() {
        return new Object[][]{
                {List.of("BLACK"), HttpStatus.SC_CREATED},
                {List.of("GREY"), HttpStatus.SC_CREATED},
                {List.of("BLACK", "GREY"), HttpStatus.SC_CREATED},
                {List.of(""), HttpStatus.SC_CREATED},
        };
    }

    @Test
    @DisplayName("create order with optional colors and check response body") // имя теста
    @Description("1. можно указать один из цветов — BLACK или GREY" +
            "2. можно указать оба цвета" +
            "3. можно совсем не указывать цвет" +
            "4. тело ответа содержит track.") // описание теста
    public void createOrderWithOptionalColors() {
        order = order.setColor(colorList);
        ValidatableResponse response = orderClient.createOrder(order);
        assertEquals("Неверный статус код",
                expectedStatusCode,
                response.extract().statusCode());
        track = response.extract().path("track");
        assertNotNull("Неверное значение", track);
    }

    @After
    public void tearDown() {
        if (track != 0) {
            orderClient.cancelOrder(track).statusCode(HttpStatus.SC_OK);
        }
    }
}