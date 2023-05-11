package utils;

import com.github.javafaker.Faker;
import java.util.Locale;

public class RandomCourierGenerator {

     Faker faker = new Faker(new Locale("ru"));

    public String getLogin() {
        return this.faker.internet().password();
    }//логин в формате пароля для уникальности

    public String getPassword() {
        return faker.internet().password();
    }

    public String getFirstName() {
        return faker.name().firstName();
    }
}
