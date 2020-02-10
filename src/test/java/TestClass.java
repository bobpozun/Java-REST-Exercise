import io.restassured.RestAssured;
import org.json.simple.JSONObject;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import java.io.IOException;

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


        //Create a booking using the above json object createBody

       // jsonResponse = rest().given.when().body(authBody.toJSONString().post(/auth));


        int bookingid = given().
                log().all().
                contentType("application/json").
                body(createBody.toJSONString()).
                when().
                post("/booking").
                then().log().all().
                extract().path("bookingid");
            //String authToken = then().extract().body().getString("","43");

        //Get the booking using the id returned and assert firstname == Robert


        
         given().
                log().all().
                contentType("application/json").
                body(createBody.toJSONString()).
                when().
                get("/booking/43").
                then().log().all().
                extract().path("bookingid").equals(bookingid);


        //Update the firstname to James
        //Get the booking again and assert firstname == James

    }
}
