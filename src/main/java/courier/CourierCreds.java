package courier;

import io.qameta.allure.Step;

public class CourierCreds {
    private String login;
    private String password;
    public CourierCreds(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public String getLogin() {
        return login;
    }
    public String getPassword() {
        return password;
    }
    @Step("Получение учетных данных курьера")
    public static CourierCreds credsFrom(Courier courier) {
        return new CourierCreds(courier.getLogin(), courier.getPassword());
    }

}
