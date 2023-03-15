package lib;

import io.restassured.response.Response;

import java.util.List;

import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.not;
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

    public static void assertResponseCodeEquals(Response response, int expectedStatusCode){
        assertEquals(expectedStatusCode, response.getStatusCode(), "unexpected response code");
    }

    public  static void assertResponseTextEquals(Response response, String expectedResponseText){
        assertEquals(expectedResponseText, response.asString(), "unexpected JSon text");
    }

    public static void assertJsonHasField(Response response, String expectedFieldName) {
        response.then().assertThat().body("$", hasKey(expectedFieldName));
    }

    public static void assertJsonHasFields(Response response, String[] expectedFieldNames) {
        for (String expectedFieldName : expectedFieldNames) {
            assertJsonHasField(response, expectedFieldName);
        }
    }

    public static void assertJsonHasNotField(Response response, String unexpectedFieldName) {
        response.then().assertThat().body("$", not(hasKey(unexpectedFieldName)));
    }
}

