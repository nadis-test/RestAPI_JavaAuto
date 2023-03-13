package lib;
import io.restassured.http.Headers;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.Map;
import static org.hamcrest.Matchers.hasKey;

import static org.junit.jupiter.api.Assertions.assertTrue;


public class BaseTestcase {
    protected String getHeader(Response response, String name){
        Headers headers = response.getHeaders();
        //проверяем, что в headers ответе есть 'name'
        assertTrue(headers.hasHeaderWithName(name), "expected header '" + name + "' not found");
        return headers.getValue(name);
    }

    protected String getCookie(Response response, String name){
        Map<String, String> cookies = new HashMap<>();
        cookies = response.getCookies();
        //проверяем, что в cookie ответе есть 'name'
        assertTrue(cookies.containsKey(name), "expected cookie '" + name + "' not found");
        return cookies.get(name);
    }

    protected int getIntFromJson(Response response, String name){
        //проверяем, что в json ответе есть 'name'
        //ищем ключ 'name' в json, знак "$" означает, что ищем в корне json, а не во вложенных ветках
        response.then().assertThat().body("$", hasKey(name));
        return response.jsonPath().getInt(name);
    }
}
