package lib;
import io.restassured.http.Headers;
import io.restassured.response.Response;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;


public class BaseTestcase {
    protected String getHeader(Response response, String name){
        Headers headers = response.getHeaders();
        assertTrue(headers.hasHeaderWithName(name), "expected header '" + name + "' not found");
        return headers.getValue(name);
    }

    protected String getCookie(Response response, String name){
        Map<String, String> cookies = new HashMap<>();
        cookies = response.getCookies();
        assertTrue(cookies.containsKey(name), "expected cookie '" + name + "' not found");
        return cookies.get(name);
    }
}
