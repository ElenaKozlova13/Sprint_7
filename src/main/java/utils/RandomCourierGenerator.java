package utils;

import com.github.javafaker.Faker;
import java.util.Locale;

public class RandomCourierGenerator {

    static Faker faker = new Faker(new Locale("ru"));

    public static String getLogin() {
        return faker.internet().password();
    }

    public static String getPassword() {
        return faker.internet().password();
    }

    public static String getFirstName() {
        return faker.name().firstName();
    }
}
