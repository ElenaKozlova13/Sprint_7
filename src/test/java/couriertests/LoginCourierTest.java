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
import static org.junit.Assert.assertTrue;

public class LoginCourierTest {
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
    @DisplayName("loginCourier courier and check response body") // имя теста
    @Description("1. курьер может авторизоваться" +
            "6 успешный запрос возвращает id") // описание теста
    public void loginCourierAndCheckResponse() {
        ValidatableResponse response = courierClient.createCourier(courier).statusCode(HttpStatus.SC_CREATED);
        ValidatableResponse loginResponse = courierClient.loginCourier(credsFrom(courier));
        assertEquals("Неверный статус код, авторизация не прошла",
                HttpStatus.SC_OK,
                loginResponse.extract().statusCode());
        courierId = loginResponse.extract().path("id");
        assertTrue(courierId != 0);
    }

    @Test
    @DisplayName("loginCourier courier without loginCourier") // имя теста
    @Description("4. если какого-то поля нет, запрос возвращает ошибку, без логина" +
            "2. для авторизации нужно передать все обязательные поля*") // описание теста
    public void loginCourierWithoutLogin() {
        ValidatableResponse response = courierClient.createCourier(courier).statusCode(HttpStatus.SC_CREATED);
        CourierCreds courierCreds = credsFrom(courier);
        ValidatableResponse withoutLoginResponse = courierClient.loginCourier(credsFrom(courier.setLogin("")));
        assertEquals("Неверный статус код",
                HttpStatus.SC_BAD_REQUEST,
                withoutLoginResponse.extract().statusCode());
        assertEquals("Неверное сообщение",
                "Недостаточно данных для входа",
                withoutLoginResponse.extract().path("message"));

        ValidatableResponse loginResponse = courierClient.loginCourier(courierCreds).statusCode(HttpStatus.SC_OK);
        courierId = loginResponse.extract().path("id");
    }

    @Test
    @DisplayName("loginCourier courier without password") // имя теста
    @Description("4. если какого-то поля нет, запрос возвращает ошибку, без пароля" +
            "2. для авторизации нужно передать все обязательные поля*") // описание теста
    public void loginCourierWithoutPassword() {
        ValidatableResponse response = courierClient.createCourier(courier).statusCode(HttpStatus.SC_CREATED);
        CourierCreds courierCreds = credsFrom(courier);
        ValidatableResponse withoutPasswordResponse = courierClient.loginCourier(credsFrom(courier.setPassword("")));
        assertEquals("Неверный статус код",
                HttpStatus.SC_BAD_REQUEST,
                withoutPasswordResponse.extract().statusCode());
        assertEquals("Неверное сообщение",
                "Недостаточно данных для входа",
                withoutPasswordResponse.extract().path("message"));

        ValidatableResponse loginResponse = courierClient.loginCourier(courierCreds).statusCode(HttpStatus.SC_OK);
        courierId = loginResponse.extract().path("id");
    }

    @Test
    @DisplayName("loginCourier not existing courier") // имя теста
    @Description("5. если авторизоваться под несуществующим пользователем, запрос возвращает ошибку") // описание теста
    public void loginNotExistingCourier() {
        ValidatableResponse loginResponse = courierClient.loginCourier(credsFrom(courier));
        assertEquals("Неверный статус код",
                HttpStatus.SC_NOT_FOUND,
                loginResponse.extract().statusCode());
        assertEquals("Неверное сообщение",
                "Учетная запись не найдена",
                loginResponse.extract().path("message"));
    }

    @Test
    @DisplayName("loginCourier courier with wrong loginCourier") // имя теста
    @Description("3 система вернёт ошибку, если неправильно указать логин или пароль - неверный логин")
    // описание теста
    public void loginCourierWithWrongLogin() {
        ValidatableResponse response = courierClient.createCourier(courier).statusCode(HttpStatus.SC_CREATED);
        CourierCreds courierCreds = credsFrom(courier);
        // System.out.println("!!!!!!" + RandomCourierGenerator.getLogin());
        ValidatableResponse wrongLoginResponse = courierClient.loginCourier(credsFrom(courier.setLogin(RandomCourierGenerator.getLogin())));
        assertEquals("Неверный статус код",
                HttpStatus.SC_NOT_FOUND,
                wrongLoginResponse.extract().statusCode());
        assertEquals("Неверное сообщение",
                "Учетная запись не найдена",
                wrongLoginResponse.extract().path("message"));

        ValidatableResponse loginResponse = courierClient.loginCourier(courierCreds).statusCode(HttpStatus.SC_OK);
        courierId = loginResponse.extract().path("id");
    }

    @Test
    @DisplayName("loginCourier courier with wrong password") // имя теста
    @Description("3 система вернёт ошибку, если неправильно указать логин или пароль - неверный пароль")
    // описание теста
    public void loginCourierWithWrongPassword() {
        ValidatableResponse response = courierClient.createCourier(courier).statusCode(HttpStatus.SC_CREATED);
        CourierCreds courierCreds = credsFrom(courier);
        ValidatableResponse wrongPasswordResponse = courierClient.loginCourier(credsFrom(courier.setPassword(RandomCourierGenerator.getPassword())));
        assertEquals("Неверный статус код",
                HttpStatus.SC_NOT_FOUND,
                wrongPasswordResponse.extract().statusCode());
        assertEquals("Неверное сообщение",
                "Учетная запись не найдена",
                wrongPasswordResponse.extract().path("message"));

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
