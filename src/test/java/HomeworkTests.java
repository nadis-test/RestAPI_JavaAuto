import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
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
}
