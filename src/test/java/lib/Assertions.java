package lib;

import io.restassured.response.Response;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Assertions extends BaseTestcase {
    public static void assertJsonByName(Response response, String name, int expectedValue){
        //в метод assertJsonByName передаем некоторый ответ запроса,
        // название поля "name"
        // некотрое эталонное значение expectedValue
        //метод внутри из ответа вытасткивает значение для поля "name" и производит сравнение с эталонным значением
        assertEquals(expectedValue, getIntFromJson(response,name),
                "json value '"+name+"' dosen't match with expected value '" + expectedValue +"'");
    }
}
