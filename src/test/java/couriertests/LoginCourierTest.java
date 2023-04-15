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
    @DisplayName("loginCourier courier")
    @Description("1. курьер может авторизоваться" +
            "6 успешный запрос возвращает id")
    public void loginCourierAndCheckResponse() {
        courierClient.createCourier(courier);
        ValidatableResponse loginResponse = courierClient.loginCourier(credsFrom(courier));
        courierId = loginResponse.extract().path("id");
        assertEquals("Неверный статус код, авторизация не прошла",
                HttpStatus.SC_OK,
                loginResponse.extract().statusCode());
        assertTrue(courierId != 0);
    }

    @Test
    @DisplayName("loginCourier courier without loginCourier")
    @Description("4. если какого-то поля нет, запрос возвращает ошибку, без логина" +
            "2. для авторизации нужно передать все обязательные поля*")
    public void loginCourierWithoutLogin() {
        courierClient.createCourier(courier);
        courierId = courierClient.loginCourier(credsFrom(courier)).extract().path("id");
        ValidatableResponse withoutLoginResponse = courierClient.loginCourier(credsFrom(courier.setLogin("")));
        assertEquals("Неверный статус код",
                HttpStatus.SC_BAD_REQUEST,
                withoutLoginResponse.extract().statusCode());
        assertEquals("Неверное сообщение",
                "Недостаточно данных для входа",
                withoutLoginResponse.extract().path("message"));
    }

    @Test
    @DisplayName("loginCourier courier without password")
    @Description("4. если какого-то поля нет, запрос возвращает ошибку, без пароля" +
            "2. для авторизации нужно передать все обязательные поля*")
    public void loginCourierWithoutPassword() {
        courierClient.createCourier(courier);
        courierId = courierClient.loginCourier(credsFrom(courier)).extract().path("id");
        ValidatableResponse withoutPasswordResponse = courierClient.loginCourier(credsFrom(courier.setPassword("")));
        assertEquals("Неверный статус код",
                HttpStatus.SC_BAD_REQUEST,
                withoutPasswordResponse.extract().statusCode());
        assertEquals("Неверное сообщение",
                "Недостаточно данных для входа",
                withoutPasswordResponse.extract().path("message"));
    }

    @Test
    @DisplayName("loginCourier not existing courier")
    @Description("5. если авторизоваться под несуществующим пользователем, запрос возвращает ошибку")
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
    @DisplayName("loginCourier courier with wrong login")
    @Description("3 система вернёт ошибку, если неправильно указать логин или пароль - неверный логин")
    public void loginCourierWithWrongLogin() {
        courierClient.createCourier(courier);
        courierId = courierClient.loginCourier(credsFrom(courier)).extract().path("id");
        ValidatableResponse wrongLoginResponse = courierClient.loginCourier(credsFrom(courier.setLogin(RandomCourierGenerator.getLogin())));
        assertEquals("Неверный статус код",
                HttpStatus.SC_NOT_FOUND,
                wrongLoginResponse.extract().statusCode());
        assertEquals("Неверное сообщение",
                "Учетная запись не найдена",
                wrongLoginResponse.extract().path("message"));
    }

    @Test
    @DisplayName("loginCourier courier with wrong password")
    @Description("3 система вернёт ошибку, если неправильно указать логин или пароль - неверный пароль")
    public void loginCourierWithWrongPassword() {
        courierClient.createCourier(courier);
        courierId = courierClient.loginCourier(credsFrom(courier)).extract().path("id");
        ValidatableResponse wrongPasswordResponse = courierClient.loginCourier(credsFrom(courier.setPassword(RandomCourierGenerator.getPassword())));
        assertEquals("Неверный статус код",
                HttpStatus.SC_NOT_FOUND,
                wrongPasswordResponse.extract().statusCode());
        assertEquals("Неверное сообщение",
                "Учетная запись не найдена",
                wrongPasswordResponse.extract().path("message"));
    }

    @After
    public void tearDown() {
        if (courierId != 0) {
            courierClient.deleteCourier(courierId);
        }
    }
}
