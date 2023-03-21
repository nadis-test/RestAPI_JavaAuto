package lib;

import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.http.Header;
import io.restassured.response.Response;

import java.util.Map;

import static io.restassured.RestAssured.given;

public class ApiCoreRequests {
    @Step("Make a GET request with token and cookie")
    public Response makeGetRequestWithTokenAndCookie(String url, String token, String cookie){
        return given()
                .filter(new AllureRestAssured())
                .header(new Header("x-csrf-token", token))
                .cookie("auth_sid", cookie)
                .get(url)
                .andReturn();
    }

        @Step("Make a GET request with auth cookie only")
        public Response makeGetRequestWithCookie(String url, String cookie){
            return given()
                    .filter(new AllureRestAssured())
                    .cookie("auth_sid", cookie)
                    .get(url)
                    .andReturn();
        }

    @Step("Make a GET request with token only")
    public Response makeGetRequestWithToken(String url, String token){
        return given()
                .filter(new AllureRestAssured())
                .header(new Header("x-csrf-token", token))
                .get(url)
                .andReturn();
    }

    @Step("Make a GET request with url only")
    public Response makeGetRequest(String url){
        return given()
                .filter(new AllureRestAssured())
                .get(url)
                .andReturn();
    }

    @Step("Make a POST request")
    public Response makePostRequest(String url, Map<String, String> authData){
        return given()
                .filter(new AllureRestAssured())
                .body(authData)
                .post(url)
                .andReturn();
    }

    @Step("Make a PUT request with auth-token and auth-cookie")
    public Response makePutRequestWithTokenAndCookie(String url, Map<String, String> userData, String token, String cookie){
        return given()
                .filter(new AllureRestAssured())
                .header("x-csrf-token", token)
                .cookie("auth_sid", cookie)
                .body(userData)
                .put(url)
                .andReturn();
    }

    @Step("Make a PUT request without auth-token and auth-cookie")
    public Response makePutRequest(String url, Map<String, String> userData){
        return given()
                .filter(new AllureRestAssured())
                .body(userData)
                .put(url)
                .andReturn();
    }

    @Step("Make an authorized DELETE request: with auth-token and auth-cookie")
    public Response makeDeleteRequest(String url, String token, String cookie){
        return given()
                .filter(new AllureRestAssured())
                .header("x-csrf-token", token)
                .cookie("auth_sid", cookie)
                .delete(url)
                .andReturn();
    }

    @Step("Make an unauthorized DELETE request: without auth-token and auth-cookie")
    public Response makeDeleteRequest(String url){
        return given()
                .filter(new AllureRestAssured())
                .delete(url)
                .andReturn();
    }

}
