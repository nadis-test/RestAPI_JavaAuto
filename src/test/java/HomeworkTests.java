import io.restassured.RestAssured;
import io.restassured.http.Headers;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class HomeworkTests {

    @Test
    public void parseJSON() {
        JsonPath response = RestAssured
                .get("https://playground.learnqa.ru/api/get_json_homework")
                .jsonPath();
        System.out.println("\nJSON response");
        response.prettyPrint();

        String message = response.get("messages.message[1]");
        if (message != null) {
            System.out.println("\nsecond message text:");
            System.out.println(message);
        } else {
            System.out.println("\nsecond message does not exist");
        }
    }

    @Test
    public void printAllRedirects() {
        int redirects_number = 0;
        int statusCode = 0;
        String url = "https://playground.learnqa.ru/api/long_redirect";
        String location;
        while (statusCode != 200) {
            Response response = RestAssured
                    .given()
                    .redirects()
                    .follow(false)
                    .when()
                    .get(url)
                    .andReturn();

            Headers headers = response.getHeaders();
            System.out.println("\nHeaders print on iteration = " + redirects_number);
            System.out.println(headers);

            statusCode = response.getStatusCode();
            System.out.println("\nStatusCode: " + statusCode);

            url = response.getHeader("location");
            System.out.println("Redirect address (location): " + url);

            redirects_number++;
        }

        System.out.println("Final redirects number: " + (redirects_number - 1));
    }

    @Test
    public void checkLongJobStatus() throws InterruptedException {
        String status_ready = "Job is ready";
        String status_not_ready = "Job is NOT ready";
        String error = "";
        String status = status_not_ready;
        int time = 0;

        // 1 заводим задачу
        JsonPath response = RestAssured
                .get("https://playground.learnqa.ru/ajax/api/longtime_job")
                .jsonPath();

        //получаем токен
        String token = response.getString("token");
        System.out.println("Token value: " + token);
        //получаем время джобы
        time = response.getInt("seconds");
        System.out.println("Time value: " + time);

        while ((status.compareTo(status_not_ready) == 0)) {
            //выполняем запрос с токеном
            JsonPath response1 = RestAssured
                    .given()
                    .queryParam("token", token)
                    .when()
                    .get("https://playground.learnqa.ru/ajax/api/longtime_job")
                    .jsonPath();
            //извлекаем ошибку из ответа
            error = response1.getString("error");
            //если оишбки нет
            if (error == null) {
                // печатаем ответ
                System.out.println("\nResponse print");
                response1.prettyPrint();
                // узнаем статус
                status = response1.getString("status");
                System.out.println("\nJob status: " + status);
                // если job is not ready, то запускаем ожидание
                if (status.compareTo(status_not_ready) == 0) {
                    // запускаем ожидание
                    System.out.println("Time wait millisec: " + (time) * 1000);
                    Thread.sleep((time) * 1000);
                }
                // если job is ready, то проверяем наличие результата и печатаем его
                else if (status.compareTo(status_ready) == 0) {
                    String result = response1.getString("result");
                    if (result != null) {
                        System.out.println("\nJob result: " + result);
                    } else {
                        System.out.println("\nError: 'result' is missing in response");
                    }
                }
            } else {
                System.out.println("\nError: " + error);
            }

        }
    }

    @Test
    public void findPassword() throws FileNotFoundException {

        Set<String> passwords = new HashSet<>();

        String fileName = "/Users/n.porotkova/GitHub/RestAPI_JavaAuto/top_passwords_SplashData1.csv";

        Scanner sc = new Scanner(new File(fileName));
        sc.useDelimiter(",");
        while (sc.hasNext())
            {
                passwords.add(sc.next());
            }
        sc.close();

        System.out.println("\n Top passwords list: " + passwords);

        String[] password_strings = new String[passwords.size()];
        passwords.toArray(password_strings);

        String login = "super_admin";
        Map<String, String> payload = new HashMap<>();
        payload.put("login", login);

        String status_no_auth = "You are NOT authorized";
        String status_auth = "You are authorized";
        String cookie_status = status_no_auth;
        String current_password = "";
        int password_number = 0;

        while ((cookie_status.compareTo(status_no_auth) == 0) && (password_number < passwords.size()) ) {

            //берем пароль из массива
            payload.put("password", password_strings[password_number]);

            current_password = payload.get("password");
            System.out.println("\nlogin: " + login);
            System.out.println("password: " + current_password);
            //делаем запрос на авторизацию с этим паролем
            Response response = RestAssured
                    .given()
                    .body(payload)
                    .when()
                    .post("https://playground.learnqa.ru/ajax/api/get_secret_password_homework")
                    .andReturn();
            int response_code = response.getStatusCode();
            if (response_code != 200) {
                System.out.println("login is wrong or missing");
            }
            else {
                //получаем значение куки
                String authCookie = response.getCookie("auth_cookie");
                //создаем куки для передачи в запрос
                Map<String, String> cookies = new HashMap<>();
                cookies.put("auth_cookie", authCookie);
                System.out.println("auth_cookie value: " + authCookie);

                //вызываем запрос проверки куки и передаем туда созданную куки
                Response response_check_cookie = RestAssured
                        .given()
                        .cookies(cookies)
                        .when()
                        .post("https://playground.learnqa.ru/ajax/api/check_auth_cookie")
                        .andReturn();

                cookie_status = response_check_cookie.htmlPath().getNode("body").toString();
                System.out.println("check_auth_cookie message: " + cookie_status);
                password_number++;
            }
        }

        if (cookie_status == "You are NOT authorized") {
            System.out.println("\ncorrect password was NOT found");
        }
        else {
            System.out.println("\ncorrect password was found:" + current_password);
        }

    }
}


