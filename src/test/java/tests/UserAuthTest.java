package tests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lib.BaseTestcase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import java.util.HashMap;
import java.util.Map;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import org.junit.jupiter.api.DisplayName;

import lib.AssertionsCustom;
import lib.ApiCoreRequests;


@Epic("Authorization cases")
@Feature("Authorization")
public class UserAuthTest extends BaseTestcase {

    String cookie;
    String header;
    int userIdOnAuth;

    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @BeforeEach
    public void loginUser(){
        Map<String, String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");

        // выполняем запрос на авторизацию
        Response responseGetAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/login", authData);

        this.cookie = this.getCookie(responseGetAuth, "auth_sid"); // вызываем getCookie из lib/BaseTestcase
        this.header = this.getHeader(responseGetAuth,"x-csrf-token"); // вызываем getHeader из lib/BaseTestcase
        this.userIdOnAuth = this.getIntFromJson(responseGetAuth, "user_id"); // вызываем getIntFromJson из lib/BaseTestcase

    }
    @Test
    @Description("This test successfully authorise user by email and password")
    @DisplayName("Test positive auth user")
    public void testAuthUser(){

        // выполняем запрос на проверку того, авторизован ли пользователь
        // используем куку и хэдер из полей класса
        // значения для куки и хэдера мы получили в методе loginUser

        Response responseCheckAuth = apiCoreRequests
                .makeGetRequest("https://playground.learnqa.ru/api/user/auth", this.header, this.cookie);

        //сравниваем id при проверке и при авторизации, если они равны - то юзер авторизован
        //используя наш собственный класс Assertions
        //в метод assertJsonByName передаем наш ответ, название поля "user_id" и эталонное значение user_id, которое мы получили от метода авторизации
        //метод внутри из ответа вытасткивает значение "user_id" и производит сравнение с эатлонным
        AssertionsCustom.assertJsonByName(responseCheckAuth, "user_id", this.userIdOnAuth);
    }

    @Description("This test check auth without sending cookie and header")
    @DisplayName("Test negative auth user")
    @ParameterizedTest
    @ValueSource(strings = {"cookie", "headers"})
    public void testNegativeAuthUser(String condition){

        if (condition.equals("cookie")) {  //если в condition из ValueSource пришло "cookie"
            Response responseForCheck = apiCoreRequests
                    .makeGetRequestWithCookie("https://playground.learnqa.ru/api/user/auth", this.cookie);
            AssertionsCustom.assertJsonByName(responseForCheck, "user_id", 0);
        }
        else if (condition.equals("headers")) {  //если в condition из ValueSource пришло "header"
            Response responseForCheck = apiCoreRequests
                    .makeGetRequestWithToken("https://playground.learnqa.ru/api/user/auth", this.header);
            AssertionsCustom.assertJsonByName(responseForCheck, "user_id", 0);
        }
        else {
            //это на случай, если мы неправильно вдруг параметры указали и ни одно узловие if не выполнилось
            throw new IllegalArgumentException("Condition value is not known: " + condition);
        }
    }
}

