import io.restassured.RestAssured;
import io.restassured.http.Headers;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;


import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserAuthTest {
    @Test
    public void testAuthUser(){
        Map<String, String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");

        // выполняем запрос на авторизацию
        Response responseGetAuth = RestAssured
                .given()
                .body(authData)
                .when()
                .post("https://playground.learnqa.ru/api/user/login")
                .andReturn();

        Map<String, String> cookies = responseGetAuth.cookies();
        Headers headers = responseGetAuth.getHeaders();
        int userAuthOnId = responseGetAuth.jsonPath().getInt("user_id");

        // проверяем статус код
        assertEquals(200, responseGetAuth.statusCode(), "Unexpected status code");
        // проверяем, что в ответе есть авторизационная кука
        assertTrue(cookies.containsKey("auth_sid"), "Response doesn't have cookie 'auth_sid'");
        // проверяем, что в ответе есть авторизационный токен
        assertTrue(headers.hasHeaderWithName("x-csrf-token"), "Response doesn't have 'x-csrf-token'");
        // проверяем, что в ответе есть id пользователя больший нуля, то есть непустой
        assertTrue(responseGetAuth.jsonPath().getInt("user_id")>0, "'user_id' is epmty");

        // выполняем запрос на проверку того, авторизован ли пользователь

        Response responseCheckAuth = RestAssured
                .given()
                .header("x-csrf-token", responseGetAuth.getHeader("x-csrf-token"))
                .cookie("auth_sid", responseGetAuth.getCookie("auth_sid"))
                .get("https://playground.learnqa.ru/api/user/auth")
                .andReturn();

        //получаем user_id из второго запроса для сравнения с тем, что получили при авторизации
        int userIdOnCheck = responseCheckAuth.jsonPath().getInt("user_id");

        //проверям, что id непустой
        assertTrue(userIdOnCheck > 0, "'user_id' is unexpected");
        //сравниваем id при проверке и при авторизации, если они равны - то юзер авторизован
        assertEquals(userAuthOnId, userIdOnCheck, "'user_id' on check doesn't match with 'user_id' on auth");
    }
}
