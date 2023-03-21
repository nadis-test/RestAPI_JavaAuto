package tests;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.BaseTestcase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static lib.AssertionsCustom.*;
import static lib.DataGenerator.getGenerationData;

public class UserGetTest extends BaseTestcase {
    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();
    @Epic("Get user data cases")
    @Feature("User data")

    @Test
    @Description("This test tries to get user data without necessary parameters: token and auth-cookie")
    @DisplayName("Test negative: get user data without token and auth-cookie")
    public void testGetUserDataNotAuth() {
        Response responseUserData = apiCoreRequests.makeGetRequest("https://playground.learnqa.ru/api/user/2");

        assertJsonHasField(responseUserData, "username");
        assertJsonHasNotField(responseUserData, "email");
        assertJsonHasNotField(responseUserData, "firstName");
        assertJsonHasNotField(responseUserData, "lastName");
    }

    @Test
    @Description("This test gets user data")
    @DisplayName("Test positive: get user data")
    public void testGetUserDetailsAuthAsSameUser(){
        Map<String, String> userData = new HashMap<>();
        userData.put("email", "vinkotov@example.com");
        userData.put("password", "1234");

        Response responseGetAuth = apiCoreRequests.makePostRequest("https://playground.learnqa.ru/api/user/login", userData);

        String cookie = this.getCookie(responseGetAuth, "auth_sid"); // вызываем getCookie из lib/BaseTestcase
        String header = this.getHeader(responseGetAuth,"x-csrf-token"); // вызываем getHeader из lib/BaseTestcase
        int user_id = responseGetAuth.jsonPath().getInt("user_id");

        Response responseUserData = apiCoreRequests.makeGetRequestWithTokenAndCookie(
                "https://playground.learnqa.ru/api/user/" + user_id,
                header,
                cookie);

        System.out.println(responseUserData.asString());
        String[] fields = {"username", "email", "firstName", "lastName"};
        assertJsonHasFields(responseUserData, fields);
    }

    @Test
    @Description("This test tries to get user data without necessary parameters: token and auth-cookie")
    @DisplayName("Test negative: get user data without token and auth-cookie")
    public void testGetUserDataWithOtherUserId() {
        //авторизуемся существующем пользователем и получаем токен и куку
        Map<String, String> userData = new HashMap<>();
        userData.put("email", "vinkotov@example.com");
        userData.put("password", "1234");

        Response responseGetAuth = apiCoreRequests.makePostRequest("https://playground.learnqa.ru/api/user/login", userData);

        String cookie = this.getCookie(responseGetAuth, "auth_sid"); // вызываем getCookie из lib/BaseTestcase
        String header = this.getHeader(responseGetAuth,"x-csrf-token"); // вызываем getHeader из lib/BaseTestcase
        
        //создаем нового пользователя, чтобы получить существующий id
        userData = getGenerationData();
        Response responseCreateNewAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/", userData);
        int user_id_new = responseCreateNewAuth.jsonPath().getInt("id");

        //делаем запрос на эндпойнт авторизации с кукой и токеном первого юзера и с id второго юзера
        Response responseUserData = apiCoreRequests.makeGetRequestWithTokenAndCookie(
                "https://playground.learnqa.ru/api/user/" + user_id_new,
                header,
                cookie);

        assertJsonHasField(responseUserData, "username");
        assertJsonHasNotField(responseUserData, "email");
        assertJsonHasNotField(responseUserData, "firstName");
        assertJsonHasNotField(responseUserData, "lastName");
    }
}
