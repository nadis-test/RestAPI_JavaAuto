package tests;

import io.qameta.allure.*;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.BaseTestcase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static lib.AssertionsCustom.*;
import static lib.DataGenerator.getGenerationData;

public class UserDeleteTest extends BaseTestcase {
    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @Epic("Delete user cases")
    @Feature("User deletion")

    @Test
    @Description("This test tries to delete protected user")
    @DisplayName("Test negative: delete protected user")
    @Stories({@Story("Negative"), @Story("Acceptance")})
    @Severity(SeverityLevel.CRITICAL)
    @TmsLink(value = "CASE-103")
    public void deleteProtectedUser() {
        //LOGIN protected user and save his data
        Map<String, String> userData = new HashMap<>();
        userData.put("email", "vinkotov@example.com");
        userData.put("password", "1234");

        Response responseGetAuth = apiCoreRequests.makePostRequest("https://playground.learnqa.ru/api/user/login", userData);

        String cookie = this.getCookie(responseGetAuth, "auth_sid"); // вызываем getCookie из lib/BaseTestcase
        String token = this.getHeader(responseGetAuth,"x-csrf-token"); // вызываем getHeader из lib/BaseTestcase
        int userId = responseGetAuth.jsonPath().getInt("user_id");

        //try to DELETE protected user
        Response responseDeleteUser = apiCoreRequests.makeDeleteRequest(
                "https://playground.learnqa.ru/api/user/" + userId,
                token,
                cookie);
        assertResponseCodeEquals(responseDeleteUser, 400);
        assertResponseTextEquals(responseDeleteUser,"Please, do not delete test users with ID 1, 2, 3, 4 or 5.");

        //check that protected user data is available
        Response responseUserData = apiCoreRequests.makeGetRequestWithTokenAndCookie(
                "https://playground.learnqa.ru/api/user/" + userId,
                token,
                cookie);

        String[] fields = {"username", "email", "firstName", "lastName"};
        assertJsonHasFields(responseUserData, fields);
    }

    @Test
    @Description("This test deletes user")
    @DisplayName("Test positive: delete just created user")
    @Stories({@Story("Positive"), @Story("Acceptance")})
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "CASE-102")
    public void deleteJustCreatedUser() {
        //CREATE user
        Map<String, String> userData = getGenerationData();
        Response responseCreateAuth = apiCoreRequests.makePostRequest("https://playground.learnqa.ru/api/user/", userData);
        String userId = responseCreateAuth.jsonPath().getString("id");

        //LOGIN
        Map<String, String> authData = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));
        Response responseGetAuth = apiCoreRequests.makePostRequest("https://playground.learnqa.ru/api/user/login", authData);

        //SAVE user auth data
        String token = this.getHeader(responseGetAuth,"x-csrf-token");
        String cookie =this.getCookie(responseGetAuth,"auth_sid");

        //DELETE user
        Response responseDeleteUser = apiCoreRequests.makeDeleteRequest(
                "https://playground.learnqa.ru/api/user/" + userId,
                token,
                cookie);
        assertResponseCodeEquals(responseDeleteUser, 200);

        //check that user data is unavailable
        Response responseDeletedUserData = apiCoreRequests.makeGetRequestWithTokenAndCookie(
                "https://playground.learnqa.ru/api/user/" + userId,
                token,
                cookie);

        assertResponseTextEquals(responseDeletedUserData,"User not found");
    }

    @Test
    @Description("This test tries to delete user using other user auth data")
    @DisplayName("Test negative: delete user using other user auth data")
    @Stories({@Story("Negative"), @Story("Regression")})
    @Severity(SeverityLevel.NORMAL)
    @TmsLink(value = "CASE-101")
    @Issue(value = "ISSUE-001")
    public void deleteOtherUserData(){
        //GENERATE first user
        Map<String, String> userData = getGenerationData();
        Response responseCreateAuthFirst = apiCoreRequests.makePostRequest(
                "https://playground.learnqa.ru/api/user/",
                userData);
        String userIdFirst = responseCreateAuthFirst.jsonPath().getString("id");

        //LOGIN first user
        Map<String, String> authDataFirst = new HashMap<>();
        authDataFirst.put("email", userData.get("email"));
        authDataFirst.put("password", userData.get("password"));

        Response responseGetAuthFirst = apiCoreRequests.makePostRequest(
                "https://playground.learnqa.ru/api/user/login",
                authDataFirst);

        //SAVE first user data
        String userDataEndpointFirst = "https://playground.learnqa.ru/api/user/" + userIdFirst;
        String tokenFirst = this.getHeader(responseGetAuthFirst,"x-csrf-token");
        String cookieFirst =this.getCookie(responseGetAuthFirst,"auth_sid");
        Response responseUserDataFirst = apiCoreRequests.makeGetRequestWithTokenAndCookie(userDataEndpointFirst, tokenFirst, cookieFirst);

        //GENERATE second user
        userData = getGenerationData();
        Response responseCreateAuthSecond = apiCoreRequests.makePostRequest("https://playground.learnqa.ru/api/user/", userData);
        String userIdSecond = responseCreateAuthSecond.jsonPath().getString("id");

        // LOGIN second user
        Map<String, String> authDataSecond = new HashMap<>();
        authDataSecond.put("email", userData.get("email"));
        authDataSecond.put("password", userData.get("password"));

        Response responseGetAuthSecond = apiCoreRequests.makePostRequest("https://playground.learnqa.ru/api/user/login", authDataSecond);

        //SAVE second user data
        String userDataEndpointSecond = "https://playground.learnqa.ru/api/user/" + userIdSecond;
        String token = this.getHeader(responseGetAuthSecond,"x-csrf-token");
        String cookie =this.getCookie(responseGetAuthSecond,"auth_sid");
        Response responseUserDataSecond = apiCoreRequests.makeGetRequestWithTokenAndCookie(userDataEndpointSecond, token, cookie);

        //try to DELETE first user with token and cookie from second user

        Response responseDeleteUser = apiCoreRequests.makeDeleteRequest(
                userDataEndpointFirst,
                token,
                cookie);

        //assertResponseCodeNotEquals(responseDeleteUser, 200);

        //check that user data for both users wasn't changed
        Response responseUserData = apiCoreRequests.makeGetRequestWithTokenAndCookie(userDataEndpointFirst, tokenFirst, cookieFirst);
        System.out.println("check first user is available...");
        assertJsonValues(responseUserData, responseUserDataFirst);

        responseUserData = apiCoreRequests.makeGetRequestWithTokenAndCookie(userDataEndpointSecond, token, cookie);
        System.out.println("check second user is available...");
        assertJsonValues(responseUserData, responseUserDataSecond);
    }

}
