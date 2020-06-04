import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;

import org.hamcrest.Matchers;
import org.json.simple.JSONObject;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import java.io.IOException;

import io.restassured.response.*;
import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

class TestClass {
    String authToken;

    @BeforeSuite
    public void setup(){
        RestAssured.baseURI = "https://restful-booker.herokuapp.com/";
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();

        get("/ping").then().statusCode(201);

        JSONObject authBody = new JSONObject();
        authBody.put("username", "admin");
        authBody.put("password", "password123");
        authToken= given().
                log().all().
                contentType("application/json").
                body(authBody.toJSONString()).
                when().
                post("/auth").
                then().log().all().
                extract().path("token");
    }

    @Test
    public void ExampleTest() throws IOException, InterruptedException {
        var checkin = "2020-09-01";
        var checkout = "2020-09-10";
        var firstname = "Robert";
        var lastname = "Smith";
        var totalprice = 500;
        var depositpaid = false;
        var additionalneeds = "Breakfast";

        var bookingdates = new JSONObject();
        bookingdates.put("checkin", checkin);
        bookingdates.put("checkout", checkout);

        JSONObject createBody = new JSONObject();
        createBody.put("firstname", firstname);
        createBody.put("lastname", lastname);
        createBody.put("totalprice", totalprice);
        createBody.put("depositpaid", depositpaid);
        createBody.put("bookingdates", bookingdates);
        createBody.put("additionalneeds", additionalneeds);

        String createBookingJson= createBody.toJSONString();

        // 1) Create a booking using the above json object
        
        Response response = given().log().all().header("Content-Type", "application/json")
                .header("Accept","application/json")
                .body(createBookingJson).when().
                post("booking").then().log().all().assertThat().
                statusCode(200).extract().response();
        JsonPath path = response.jsonPath();

        Integer id = path.getInt("bookingid");
 

        // 2) Get the booking using the id returned and assert firstname == Robert
        given().log().all().header("Accept", "application/json").pathParam("idparam", id)
                .when()
                .get("booking/{idparam}").
                then().log().all().assertThat().statusCode(200).body("firstname", Matchers.equalTo("Robert"));
                
          

        // 3) Update the firstname to James

        JSONObject update= new JSONObject();
        update.put("firstname","James");
        String updateBookingJson= update.toJSONString();

Response response2= given().log().all().header("Content-Type", "application/json").header("Accept",
        "application/json").pathParam("idparam", id).body(updateBookingJson).
         when().cookie("token", authToken).patch("booking/{idparam}").then().assertThat().statusCode(200).extract().response();
 JsonPath path2 = response2.jsonPath();

        // 4) Get the booking again and assert firstname == James
 given().log().all().header("Accept", "application/json").pathParam("idparam", id).when().get("booking/{idparam}")
         .then().log().all().assertThat().statusCode(200).body("firstname", Matchers.equalTo("James"));
    }
}
