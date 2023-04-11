package couriertests;

import courier.Courier;
import courier.CourierClient;
import courier.CourierCreds;
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
    private CourierClient courierClient;
    private int courierId;

    @Before
    public void setUp() {
        courierClient = new CourierClient();
        courier = new Courier()
                .setLogin(RandomCourierGenerator.getLogin())
                .setPassword(RandomCourierGenerator.getPassword())
                .setFirstName(RandomCourierGenerator.getFirstName());
    }

    @Test
    @DisplayName("create new courier and check response body") // имя теста
    @Description("1. курьера можно создать" +
            "4 запрос возвращает правильный код ответа" +
            "5. успешный запрос возвращает ok: true") // описание теста
    public void createNewCourier() {
        ValidatableResponse response = courierClient.createCourier(courier);
        assertEquals("Неверный статус код",
                HttpStatus.SC_CREATED,
                response.extract().statusCode());
        assertEquals("Неверное значение",
                true,
                response.extract().path("ok"));
        ValidatableResponse loginResponse = courierClient.loginCourier(credsFrom(courier)).statusCode(HttpStatus.SC_OK);
        courierId = loginResponse.extract().path("id");
        System.out.println(courierId);
    }

    @Test
    @DisplayName("create two equal couriers and check response body") // имя теста
    @Description("2. нельзя создать двух одинаковых курьеров") // описание теста
    // тест должен упасть, тк в документации "message": "Этот логин уже используется"
    public void createTwoEqualCouriers() {
        ValidatableResponse response1 = courierClient.createCourier(courier);
        assertEquals("Неверный статус код",
                HttpStatus.SC_CREATED,
                response1.extract().statusCode());
        ValidatableResponse response2 = courierClient.createCourier(courier);
        assertEquals("Неверный статус код",
                HttpStatus.SC_CONFLICT,
                response2.extract().statusCode());
        assertEquals("Неверное сообщение",
                "Этот логин уже используется. Попробуйте другой.",
                response2.extract().path("message"));
        ValidatableResponse loginResponse = courierClient.loginCourier(credsFrom(courier)).statusCode(HttpStatus.SC_OK);
        courierId = loginResponse.extract().path("id");
    }

    @Test
    @DisplayName("create courier without loginCourier and check response body") // имя теста
    @Description("3. чтобы создать курьера, нужно передать в ручку все обязательные поля - без логина" +
            "6. если одного из полей нет, запрос возвращает ошибку") // описание теста
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
    @DisplayName("create courier without password and check response body") // имя теста
    @Description("3. чтобы создать курьера, нужно передать в ручку все обязательные поля - без пароля" +
            "6. если одного из полей нет, запрос возвращает ошибку") // описание теста
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
    @DisplayName("create courier without first name and check response body") // имя теста
    @Description("3. чтобы создать курьера, нужно передать в ручку все обязательные поля - без имени успех")
    // описание теста
    public void createCourierWithoutFirstName() {
        courier = courier.setFirstName("");
        ValidatableResponse response = courierClient.createCourier(courier);
        assertEquals("Неверный статус код",
                HttpStatus.SC_CREATED,
                response.extract().statusCode());
        assertEquals("Неверное значение",
                true,
                response.extract().path("ok"));
        ValidatableResponse loginResponse = courierClient.loginCourier(credsFrom(courier)).statusCode(HttpStatus.SC_OK);
        courierId = loginResponse.extract().path("id");
    }

    @Test
    @DisplayName("create courier with existing loginCourier and check response body") // имя теста
    @Description("7. если создать пользователя с логином, который уже есть, возвращается ошибка") // описание теста
    // тест должен упасть - в документации "Этот логин уже используется"
    public void createCourierWithExistingLogin() {
        ValidatableResponse response1 = courierClient.createCourier(courier);
        assertEquals("Неверный статус код",
                HttpStatus.SC_CREATED,
                response1.extract().statusCode());
        assertEquals("Неверное значение",
                true,
                response1.extract().path("ok"));
        CourierCreds courierCreds = credsFrom(courier);
        Courier courier2 = courier.setPassword(RandomCourierGenerator.getPassword()).setFirstName(RandomCourierGenerator.getFirstName());
        ValidatableResponse response2 = courierClient.createCourier(courier2);
        assertEquals("Неверный статус код",
                HttpStatus.SC_CONFLICT,
                response2.extract().statusCode());
        assertEquals("Неверное сообщение",
                "Этот логин уже используется. Попробуйте другой.",
                response2.extract().path("message"));
        ValidatableResponse loginResponse = courierClient.loginCourier(courierCreds).statusCode(HttpStatus.SC_OK);
        courierId = loginResponse.extract().path("id");
    }

    @After
    public void tearDown() {
        if (courierId != 0) {
            courierClient.deleteCourier(courierId).statusCode(HttpStatus.SC_OK);
        }
    }

}
