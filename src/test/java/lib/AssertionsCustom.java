package lib;

import io.restassured.response.Response;

import static org.hamcrest.Matchers.hasKey;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class AssertionsCustom extends BaseTestcase {
    public static void assertJsonByName(Response response, String name, int expectedValue) {
        //в метод assertJsonByName передаем некоторый ответ запроса,
        // название поля "name"
        // некотрое эталонное значение expectedValue
        //метод внутри из ответа вытасткивает значение для поля "name" и производит сравнение с эталонным значением
        assertEquals(expectedValue, getIntFromJson(response, name),
                "json value '" + name + "' dosen't match with expected value '" + expectedValue + "'");
    }

    public static void assertJsonStringByName(Response response, String name, String expectedValue) {
        //в метод assertJsonByName передаем некоторый ответ запроса,
        // название поля "name"
        // некотрое эталонное значение expectedValue
        //метод внутри из ответа вытасткивает значение для поля "name" и производит сравнение с эталонным значением
        response.then().assertThat().body("$", hasKey(name));
        String parameter = response.jsonPath().getString(name);
        assertEquals(expectedValue, parameter,
                "json value '" + name + "' dosen't match with expected value '" + expectedValue + "'");
    }
}

