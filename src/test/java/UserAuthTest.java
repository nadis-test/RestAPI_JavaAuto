import io.restassured.RestAssured;
import io.restassured.http.Headers;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;


import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserAuthTest {

    String cookie;
    String header;
    int userIdOnAuth;

    @BeforeEach
    public void loginUser(){
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

        this.cookie = responseGetAuth.getCookie("auth_sid");
        this.header = responseGetAuth.getHeader("x-csrf-token");
        this.userIdOnAuth = responseGetAuth.jsonPath().getInt("user_id");

    }
    @Test
    public void testAuthUser(){

        // выполняем запрос на проверку того, авторизован ли пользователь
        // используем куку и хэдер из полей класса
        // значения для куки и хэдера мы получили в методе loginUser

        Response responseCheckAuth = RestAssured
                .given()
                .header("x-csrf-token", this.header)
                .cookie("auth_sid", this.cookie)
                .get("https://playground.learnqa.ru/api/user/auth")
                .andReturn();

        //получаем user_id из второго запроса для сравнения с тем, что получили при авторизации
        int userIdOnCheck = responseCheckAuth.jsonPath().getInt("user_id");

        //проверям, что id непустой
        assertTrue(userIdOnCheck > 0, "'user_id' is unexpected");
        //сравниваем id при проверке и при авторизации, если они равны - то юзер авторизован
        assertEquals(this.userIdOnAuth, userIdOnCheck, "'user_id' on check doesn't match with 'user_id' on auth");
    }

    @ParameterizedTest
    @ValueSource(strings = {"cookie", "headers"})
    public void testNegativeAuthUser(String condition){
        //создаем сущность RequestSpecification, чтобы сконструировать запрос с нужными параметрами на выбор из ValueSource
        RequestSpecification spec = RestAssured.given();
        //засовываем в сущность RequestSpecification адрес эндпойнта
        spec.baseUri("https://playground.learnqa.ru/api/user/auth");

        //засовываем в сущность RequestSpecification те параметры, которые указаны в ValueSource
        if (condition.equals("cookie")) {  //если в condition из ValueSource пришло "cookie"
            spec.cookie("auth_sid", this.cookie); //то в spec мы передаем куку с названием 'auth_sid' и значением
            //которое мы вытащили из response: cookies = responseGetAuth.cookies()
        }
        else if (condition.equals("headers")) {  //если в condition из ValueSource пришло "header"
                spec.header("x-csrf-token", this.header); //то в spec мы передаем хэдер с названием 'x-csrf-token'
                                                                    // и значением из headers = responseGetAuth.getHeaders();
            }
        else {
            //это на случай, если мы неправильно вдруг параметры указали и ни одно узловие if не выполнилось
            throw new IllegalArgumentException("Condition value is known: " + condition);
        }

        // делаем запрос со сконструированными параметрами и получаем его ответ в виде JsonPath
        JsonPath responseForCheck = spec.get().jsonPath();

        //проверяем значение user_id - т.к. наш тест негативный и мы намеренно  передавали в запрос только куку ИЛИ только хэдер
        // то юзер не должен быть авторизован (условие авторизации = И хэдер, И кука переданы)
        // неавторизованный юзер имеер user_id = 0 по требованиям в нашем API
        assertEquals(0, responseForCheck.getInt("user_id"), "'user_id' should be 0 for unauth user");
    }



}

