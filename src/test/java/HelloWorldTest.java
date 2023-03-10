import io.restassured.http.Headers;
import io.restassured.path.json.JsonPath;
import org.junit.jupiter.api.Test;
import io.restassured.RestAssured;
import io.restassured.response.Response;

import java.util.HashMap;
import java.util.Map;

public class HelloWorldTest {
    @Test
    public void testRestAssured(){
        Map<String, String> headers = new HashMap<>();
        headers.put("Header1","Value1");
        headers.put("Header2","Value2");

        Response response = RestAssured
                .given()
                .redirects()
                .follow(false)
                .headers(headers)
                .when()
                .get("https://playground.learnqa.ru/api/get_303")
                .andReturn();

        response.prettyPrint();

        Headers responseHeaders = response.getHeaders();
        System.out.println(responseHeaders);
        String locationHeader = response.getHeader("location");
        System.out.println("Location header = " + locationHeader);
    }

}
