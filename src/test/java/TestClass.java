import io.restassured.RestAssured;
import jdk.nashorn.internal.parser.JSONParser;

import org.json.simple.JSONObject;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;
import java.util.*;

import java.io.IOException;

import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;

class TestClass {
    String authToken;

    List<String> createBookingIdList;

    @BeforeSuite
    public void setup(){
        RestAssured.baseURI = "https://restful-booker.herokuapp.com/";
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();

        createBookingIdList = new ArrayList<>();

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

    /**
     * Create a booking using the above json object
     */
    @Test
    public void createBooking() throws IOException, InterruptedException {
        var firstname = "Robert";
        var lastname = "Smith";
        var totalprice = 100;

        //create booking #1
        var createBody = buildRequest(firstname, lastname, totalprice);
        var createBookingResponse = createBooking(createBody);
        createBookingIdList.add(createBookingResponse.path("bookingid"));

        assertEquals(firstname, createBookingResponse.path("booking.firstname"));
        assertEquals(lastname, createBookingResponse.path("booking.lastname"));
        assertEquals(new Integer(totalprice), createBookingResponse.path("booking.totalprice"));
    }

    private Response createBooking(JSONObject createBody) {
        return given().log().all().contentType("application/json").body(createBody.toJSONString()).when()
                .post("/booking").then().log().all().extract().response();
    }

    private JSONObject updateBooking(JSONObject createBody, String bookingId) {
        return given().log().all().contentType("application/json")
                .cookie("token", authToken)
                .body(patchBody.toJSONString())
                .when().patch("/booking/" + bookingid).then()
                .log().all().extract().response();
    }
    
    /**
     * Build request
     * @return
     */
    private JSONObject buildRequest(String firstName, String lastName, int totalPrice) {
        var checkin = "2020-09-01";
        var checkout = "2020-09-10";
        var firstname = firstName;
        var lastname = lastName;
        var totalprice = totalPrice;
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

        return createBody;
    }

    public void ExampleTest() throws IOException, InterruptedException {
        

            
        
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

        /*
         * 1) Split tests into separate idempotent test cases - CreateBooking and UpdateBooking. 
         * 2) Data drive the create booking test case. Create 4 dummy test data scenarios. 
         * 3) Implement object serialization / deserialization for request and response objects 4) Expand on what is asserted in each test case
         * 5) Delete all bookings created during test run 
         * 6) Run the tests in a parallelized fashion
         */

    }
}
