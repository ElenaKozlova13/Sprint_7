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
    private OrderClient orderClient =  new OrderClient();
    private RandomOrderGenerator orderGenerator =  new RandomOrderGenerator();
    private int track;
    private List<String> colorList;
    private int expectedStatusCode;

    public CreateOrderTest(List<String> colorList, int expectedStatusCode) {
        this.colorList = colorList;
        this.expectedStatusCode = expectedStatusCode;
    }

    @Before
    public void setUp() {
        order = new Order()
                .setFirstName(orderGenerator.getFirstName())
                .setLastName(orderGenerator.getLastName())
                .setAddress(orderGenerator.getAddress())
                .setMetroStation(orderGenerator.getMetroStation())
                .setPhone(orderGenerator.getPhone())
                .setRentTime(orderGenerator.getRentTime())
                .setDeliveryDate(orderGenerator.getDeliveryDate())
                .setComment(orderGenerator.getComment())
                .setColor(colorList)
        ;
    }

    @Parameterized.Parameters(name = "Предпочитаемые цвета. Тестовые данные: список цветов {0}, ожидаемый статус-код {1}")
    public static Object[][] getColorData() {
        return new Object[][]{
                {List.of("BLACK"), HttpStatus.SC_CREATED},
                {List.of("GREY"), HttpStatus.SC_CREATED},
                {List.of("BLACK", "GREY"), HttpStatus.SC_CREATED},
                {List.of(""), HttpStatus.SC_CREATED},
        };
    }

    @Test
    @DisplayName("create order with optional colors")
    @Description("1. можно указать один из цветов — BLACK или GREY" +
            "2. можно указать оба цвета" +
            "3. можно совсем не указывать цвет" +
            "4. тело ответа содержит track.")
    public void createOrderWithOptionalColors() {
        order = order.setColor(colorList);
        ValidatableResponse response = orderClient.createOrder(order);
        track = response.extract().path("track");
        assertEquals("Неверный статус код",
                expectedStatusCode,
                response.extract().statusCode());
        assertNotNull("Неверное значение", track);
    }

    @After
    public void tearDown() {
        if (track != 0) {
            orderClient.cancelOrder(track);
        }
    }
}
