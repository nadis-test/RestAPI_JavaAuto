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
        Map<String, String> data = new HashMap<>();
        data.put("login","secret_login");
        data.put("password","secret_pass");

        Response responseForGet = RestAssured
                .given()
                .body(data)
                .when()
                .post("https://playground.learnqa.ru/api/get_auth_cookie")
                .andReturn();

        System.out.println("\nPretty text:");
        responseForGet.prettyPrint();

        System.out.println("\nHeaders:");
        Headers responseHeaders = responseForGet.getHeaders();
        System.out.println(responseHeaders);

        System.out.println("\nCookies:");
        Map<String, String> responseCookies = responseForGet.getCookies();
        System.out.println(responseCookies);

        System.out.println("\nresponseAuthCookie:");
        String responseAuthCookie = responseForGet.getCookie("auth_cookie"); // сохранили cookie для передачи дальше
        System.out.println(responseAuthCookie);

        Map<String, String> cookies = new HashMap<>();
        if (responseAuthCookie != null) {
            cookies.put("auth_cookie", responseAuthCookie);
        }

        //передаем авторизац данные и cookie в следующий запрос
        Response responseForCheck = RestAssured
                .given()
                .body(data)
                .cookies(cookies)
                .when()
                .post("https://playground.learnqa.ru/api/check_auth_cookie")
                .andReturn();

        System.out.println("\nREsponse For Check:");
        responseForCheck.print();

    }

}
