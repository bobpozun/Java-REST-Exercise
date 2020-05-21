import io.restassured.RestAssured;
import org.json.simple.JSONObject;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import java.io.IOException;

import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;

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

        //  Create a booking using the above json object
        var bookingid = given().log().all().contentType("application/json").body(createBody.toJSONString()).when()
                .post("/booking").then().log().all().extract().path("bookingid");
        System.out.println("Created Booking Id: " + bookingid);    
        
        // Get the booking using the id returned and assert firstname == Robert
        var getBookingUrl = "/booking/" + bookingid;
        var getBookingFirstName = given().log().all().contentType("application/json").when()
                .get(getBookingUrl).then().log().all().extract().path("firstname");
        System.out.println("Get Booking First Name: " + getBookingFirstName);
        assertEquals("Robert", getBookingFirstName);

        // Update the firstname to James    
        var updateBookingUrl = "/booking/" + bookingid;

        //Override first name.
        JSONObject patchBody = new JSONObject();
        patchBody.put("firstname", "James");
        patchBody.put("lastname", createBody.get("lastname"));

        var updatedBookingResponse = given().log().all().contentType("application/json")
                .cookie("token", authToken)
                .body(patchBody.toJSONString())
                .when().patch(getBookingUrl).then()
                .log().all();
        System.out.println("Successfully Updated Booking First Name for BookingId: " + bookingid);
        
        // Get the booking again and assert firstname == James
        var getUpdatedBookingFirstName = given().log().all().contentType("application/json").when().get(getBookingUrl).then()
                .log().all().extract().path("firstname");
        System.out.println("Get Updated Booking First Name: " + getUpdatedBookingFirstName);
        assertEquals("James", getUpdatedBookingFirstName);

    }
}
