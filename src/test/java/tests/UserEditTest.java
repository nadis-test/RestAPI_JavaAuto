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
import static lib.DataGenerator.*;

public class UserEditTest extends BaseTestcase {
    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @Epic("Edit user data cases")
    @Feature("Edit user data")

    @Test
    @Description("This test edit user data just after creation")
    @DisplayName("Test positive: edit user data just after creation")
    @Stories({@Story("Positive"), @Story("Acceptance")})
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "CASE-301")
    public void editJustCreatedTest(){
        //GENERATE user
        Map<String, String> userData = getGenerationData();
        Response responseCreateAuth = apiCoreRequests.makePostRequest("https://playground.learnqa.ru/api/user/", userData);
        String userId = responseCreateAuth.jsonPath().getString("id");

        //LOGIN
        Map<String, String> authData = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));
        Response responseGetAuth = apiCoreRequests.makePostRequest("https://playground.learnqa.ru/api/user/login", authData);

        //EDIT first name
        String newName = "changedName";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);
        String userDataEndpoint = "https://playground.learnqa.ru/api/user/" + userId;
        String token = this.getHeader(responseGetAuth,"x-csrf-token");
        String cookie =this.getCookie(responseGetAuth,"auth_sid");
        Response responseEditData = apiCoreRequests.makePutRequestWithTokenAndCookie(
                userDataEndpoint,
                editData,
                token,
                cookie);

        //GET user data - проверям изменились ли данные
        Response responseUserData = apiCoreRequests.makeGetRequestWithTokenAndCookie(userDataEndpoint, token, cookie);

        assertJsonByName(responseUserData, "firstName", newName);
    }

    @Test
    @Description("This test tries to edit non-authorised user data")
    @DisplayName("Test negative: edit non-authorised user data")
    @Stories({@Story("Negative"), @Story("Regression")})
    @Severity(SeverityLevel.CRITICAL)
    @TmsLink(value = "CASE-302")
    public void editNonAuthUser(){
        //GENERATE user
        Map<String, String> userData = getGenerationData();
        Response responseCreateAuth = apiCoreRequests.makePostRequest("https://playground.learnqa.ru/api/user/", userData);
        String userId = responseCreateAuth.jsonPath().getString("id");

        //try to EDIT first name
        String newName = "changedName";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);
        String userDataEndpoint = "https://playground.learnqa.ru/api/user/" + userId;

        Response responseEditData = apiCoreRequests.makePutRequest(
                userDataEndpoint,
                editData);

        assertResponseTextEquals(responseEditData, "Auth token not supplied");
    }

    @Test
    @Description("This test tries to edit user data using other user auth data")
    @DisplayName("Test negative: edit user data using other user auth data")
    @Stories({@Story("Negative"), @Story("Regression")})
    @Severity(SeverityLevel.CRITICAL)
    @TmsLink(value = "CASE-303")
    @Issue(value = "ISSUE-002")
    public void editOtherUserData(){
        //GENERATE first user
        Map<String, String> userData = getGenerationData();
        Response responseCreateAuthFirst = apiCoreRequests.makePostRequest("https://playground.learnqa.ru/api/user/", userData);
        String userIdFirst = responseCreateAuthFirst.jsonPath().getString("id");

        //LOGIN first user
        Map<String, String> authDataFirst = new HashMap<>();
        authDataFirst.put("email", userData.get("email"));
        authDataFirst.put("password", userData.get("password"));

        Response responseGetAuthFirst = apiCoreRequests.makePostRequest("https://playground.learnqa.ru/api/user/login", authDataFirst);

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

        //try to EDIT first user with  token and cookie from second user
        String newName = "changedName";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);

        Response responseEditData = apiCoreRequests.makePutRequestWithTokenAndCookie(
                userDataEndpointFirst,
                editData,
                token,
                cookie);

        //check change data response status code
        assertResponseCodeNotEquals(responseEditData, 200);

        //check that user data for both users wasn't changed
        Response responseUserData = apiCoreRequests.makeGetRequestWithTokenAndCookie(userDataEndpointFirst, tokenFirst, cookieFirst);
        System.out.println("check first user data wasn't changed:");
        assertJsonValues(responseUserData, responseUserDataFirst);

        responseUserData = apiCoreRequests.makeGetRequestWithTokenAndCookie(userDataEndpointSecond, token, cookie);
        System.out.println("check second user data wasn't changed:");
        assertJsonValues(responseUserData, responseUserDataSecond);
    }

    @Test
    @Description("This test tries to change user email to incorrect email withoou '@' symbol")
    @DisplayName("Test negative: change user email to incorrect data")
    @Stories({@Story("Negative"), @Story("Regression")})
    @Severity(SeverityLevel.NORMAL)
    @TmsLink(value = "CASE-304")
    public void editUserEmailToIncorrectData(){
        //GENERATE user
        Map<String, String> userData = getGenerationData();
        Response responseCreateAuth = apiCoreRequests.makePostRequest("https://playground.learnqa.ru/api/user/", userData);
        String userId = responseCreateAuth.jsonPath().getString("id");

        //LOGIN
        Map<String, String> authData = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));
        Response responseGetAuth = apiCoreRequests.makePostRequest("https://playground.learnqa.ru/api/user/login", authData);

        //EDIT email
        String newEmail = getRandomEmail().replace("@","");
        Map<String, String> editData = new HashMap<>();
        editData.put("email", newEmail);

        String userDataEndpoint = "https://playground.learnqa.ru/api/user/" + userId;
        String token = this.getHeader(responseGetAuth,"x-csrf-token");
        String cookie =this.getCookie(responseGetAuth,"auth_sid");
        Response responseEditData = apiCoreRequests.makePutRequestWithTokenAndCookie(
                userDataEndpoint,
                editData,
                token,
                cookie);

        assertResponseCodeEquals(responseEditData, 400);
        assertResponseTextEquals(responseEditData, "Invalid email format");

        //GET user data - проверям изменились ли данные
        Response responseUserData = apiCoreRequests.makeGetRequestWithTokenAndCookie(userDataEndpoint, token, cookie);
        assertJsonByName(responseUserData, "email", userData.get("email"));
    }

    @Test
    @Description("This test tries to edit user name, replacing it by one symbol ")
    @DisplayName("Test negative: replace username by too short value")
    @Stories({@Story("Negative"), @Story("Regression")})
    @Severity(SeverityLevel.NORMAL)
    @TmsLink(value = "CASE-305")
    public void editUsernameToIncorrectShortValue(){
        //GENERATE user
        Map<String, String> userData = getGenerationData();
        Response responseCreateAuth = apiCoreRequests.makePostRequest("https://playground.learnqa.ru/api/user/", userData);
        String userId = responseCreateAuth.jsonPath().getString("id");

        //LOGIN
        Map<String, String> authData = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));
        Response responseGetAuth = apiCoreRequests.makePostRequest("https://playground.learnqa.ru/api/user/login", authData);

        //EDIT first name
        String newName = getRandomUsername(1);
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);

        String userDataEndpoint = "https://playground.learnqa.ru/api/user/" + userId;
        String token = this.getHeader(responseGetAuth,"x-csrf-token");
        String cookie =this.getCookie(responseGetAuth,"auth_sid");
        Response responseEditData = apiCoreRequests.makePutRequestWithTokenAndCookie(
                userDataEndpoint,
                editData,
                token,
                cookie);

        assertResponseCodeEquals(responseEditData, 400);
        assertJsonByName(responseEditData, "error", "Too short value for field firstName");

        //GET user data - проверям изменились ли данные
        Response responseUserData = apiCoreRequests.makeGetRequestWithTokenAndCookie(userDataEndpoint, token, cookie);
        assertJsonByName(responseUserData, "firstName", userData.get("firstName"));
    }

}
