package couriertests;

import courier.Courier;
import courier.CourierClient;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import utils.RandomCourierGenerator;

import static courier.CourierCreds.credsFrom;
import static org.junit.Assert.assertEquals;

public class CreateCourierTest {
    private Courier courier;
    private CourierClient courierClient =  new CourierClient();
    private RandomCourierGenerator courierGenerator =  new RandomCourierGenerator();
    private int courierId;

    @Before
    public void setUp() {
        courier = new Courier()
                .setLogin(courierGenerator.getLogin())
                .setPassword(courierGenerator.getPassword())
                .setFirstName(courierGenerator.getFirstName());
    }

    @Test
    @DisplayName("create new courier")
    @Description("1. курьера можно создать" +
            "4 запрос возвращает правильный код ответа" +
            "5. успешный запрос возвращает ok: true")
    public void createNewCourier() {
        ValidatableResponse response = courierClient.createCourier(courier);
        courierId = courierClient.loginCourier(credsFrom(courier)).extract().path("id");
        assertEquals("Неверный статус код",
                HttpStatus.SC_CREATED,
                response.extract().statusCode());
        assertEquals("Неверное значение",
                true,
                response.extract().path("ok"));
    }

    @Test
    @DisplayName("create two equal couriers")
    @Description("2. нельзя создать двух одинаковых курьеров")
    // тест должен упасть, тк в документации "message": "Этот логин уже используется"
    public void createTwoEqualCouriers() {
        courierClient.createCourier(courier);
        courierId = courierClient.loginCourier(credsFrom(courier)).extract().path("id");
        ValidatableResponse response2 = courierClient.createCourier(courier);
        assertEquals("Неверный статус код",
                HttpStatus.SC_CONFLICT,
                response2.extract().statusCode());
        assertEquals("Неверное сообщение",
                "Этот логин уже используется. Попробуйте другой.",
                response2.extract().path("message"));
    }

    @Test
    @DisplayName("create courier without login")
    @Description("3. чтобы создать курьера, нужно передать в ручку все обязательные поля - без логина" +
            "6. если одного из полей нет, запрос возвращает ошибку")
    public void createCourierWithoutLogin() {
        courier = courier.setLogin("");
        ValidatableResponse response = courierClient.createCourier(courier);
        assertEquals("Неверный статус код",
                HttpStatus.SC_BAD_REQUEST,
                response.extract().statusCode());
        assertEquals("Неверное значение",
                "Недостаточно данных для создания учетной записи",
                response.extract().path("message"));
    }

    @Test
    @DisplayName("create courier without password")
    @Description("3. чтобы создать курьера, нужно передать в ручку все обязательные поля - без пароля" +
            "6. если одного из полей нет, запрос возвращает ошибку")
    public void createCourierWithoutPassword() {
        courier = courier.setPassword("");
        ValidatableResponse response = courierClient.createCourier(courier);
        assertEquals("Неверный статус код",
                HttpStatus.SC_BAD_REQUEST,
                response.extract().statusCode());
        assertEquals("Неверное значение",
                "Недостаточно данных для создания учетной записи",
                response.extract().path("message"));
    }

    @Test
    @DisplayName("create courier without first name")
    @Description("3. чтобы создать курьера, нужно передать в ручку все обязательные поля - без имени успех")
    public void createCourierWithoutFirstName() {
        courier = courier.setFirstName("");
        ValidatableResponse response = courierClient.createCourier(courier);
        courierId = courierClient.loginCourier(credsFrom(courier)).extract().path("id");
        assertEquals("Неверный статус код",
                HttpStatus.SC_CREATED,
                response.extract().statusCode());
        assertEquals("Неверное значение",
                true,
                response.extract().path("ok"));
    }

    @Test
    @DisplayName("create courier with existing loginCourier and check response body")
    @Description("7. если создать пользователя с логином, который уже есть, возвращается ошибка")
    // тест должен упасть - в документации "Этот логин уже используется"
    public void createCourierWithExistingLogin() {
        courierClient.createCourier(courier);
        courierId = courierClient.loginCourier(credsFrom(courier)).extract().path("id");
        Courier courier2 = courier.setPassword(courierGenerator.getPassword()).setFirstName(courierGenerator.getFirstName());
        ValidatableResponse response2 = courierClient.createCourier(courier2);
        assertEquals("Неверный статус код",
                HttpStatus.SC_CONFLICT,
                response2.extract().statusCode());
        assertEquals("Неверное сообщение",
                "Этот логин уже используется. Попробуйте другой.",
                response2.extract().path("message"));
    }

    @After
    public void tearDown() {
        if (courierId != 0) {
            courierClient.deleteCourier(courierId);
        }
    }

}
