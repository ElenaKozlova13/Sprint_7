package utils;

import com.github.javafaker.Faker;
import com.google.gson.Gson;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class RandomOrderGenerator {
    Gson gson = new Gson();
    Faker faker = new Faker(new Locale("ru"));
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    String moscowMetroJson;

    {
        try {
            moscowMetroJson = Files.readString(Paths.get("src/main/resources/moscowMetro.json"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    MoscowMetro[] moscowMetro = gson.fromJson(moscowMetroJson.toString(), MoscowMetro[].class);

    //Имя заказчика, записывается в поле firstName таблицы Orders
    public String getFirstName() {
        return faker.name().firstName();
    }

    //Фамилия заказчика, записывается в поле lastName таблицы Orders
    public String getLastName() {
        return faker.name().lastName();
    }

    //Адрес заказчика, записывается в поле address таблицы Orders
    public String getAddress() {
        return faker.address().fullAddress();
    }

    //Телефон заказчика, записывается в поле phone таблицы Orders
    public String getPhone() {
        return faker.phoneNumber().phoneNumber();
    }

    //Количество дней аренды, записывается в поле rentTime таблицы Orders
    public int getRentTime() {
        return faker.number().numberBetween(1, 7);
    }

    //Дата доставки, записывается в поле deliveryDate таблицы Orders
    public String getDeliveryDate() {
        String deliveryDate = sdf.format(faker.date().future(30, TimeUnit.DAYS));
        return deliveryDate;
    }

    //Комментарий от заказчика, записывается в поле comment таблицы Orders
    public String getComment() {
        return faker.lorem().sentence(3, 3);
    }

    //Ближайшая к заказчику станция метро, записывается в поле metroStation таблицы Orders
    public int getMetroStation() {
        return moscowMetro[faker.number().numberBetween(1, moscowMetro.length)].getNumber();
    }

    static List<String> colorList = List.of("BLACK", "GREY");

    //Предпочитаемые цвета, записываются в поле color таблицы Orders
    public static List<String> getColor() {
        return colorList;
    }
}
