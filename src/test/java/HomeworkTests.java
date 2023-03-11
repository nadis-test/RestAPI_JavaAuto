import io.restassured.RestAssured;
import io.restassured.http.Headers;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.util.List;

public class HomeworkTests {
    @Test
    public void parseJSON(){
        JsonPath response = RestAssured
                .get("https://playground.learnqa.ru/api/get_json_homework")
                .jsonPath();
        System.out.println("\nJSON response");
        response.prettyPrint();

        String message = response.get("messages.message[1]");
        if (message != null) {
            System.out.println("\nsecond message text:");
            System.out.println(message);
        }
        else {
            System.out.println("\nsecond message does not exist");
        }
    }

    @Test
    public  void printAllRedirects(){
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
    }
}
