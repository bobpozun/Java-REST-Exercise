import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import org.json.simple.JSONObject;
import org.testng.Assert;
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

        /* 
            Create a booking using the above json object
            Get the booking using   the id returned and assert firstname == Robert
            Update the firstname to James
            Get the booking again and assert firstname == James
        */

        RequestSpecification httpRequest=RestAssured.given().header("Content-Type","application/json");
        Response httpResponse;
        //Create a booking using the above json object
        httpResponse=httpRequest.body(createBody.toJSONString()).request(Method.POST,"/booking");
        Assert.assertEquals(httpResponse.getStatusCode(),200, "Booking details Creation Failed:: ");
        int bookingId=httpResponse.jsonPath().get("bookingid");

        //Get the booking using the id returned and assert firstname == Robert
        httpResponse=httpRequest.request(Method.GET,"/booking/"+bookingId);
        Assert.assertEquals(httpResponse.getStatusCode(),200, "Booking details Retreival Failed:: ");
       
        //Update the firstname to James
        httpRequest.cookie("token",authToken);
        httpResponse=httpRequest.body("{\"firstname\" : \"James\"}").request(Method.PATCH,"/booking/"+bookingId);
        Assert.assertEquals(httpResponse.getStatusCode(),200, "Booking details Update Failed:: ");

        //Get the booking using the id returned and assert firstname == Robert
        httpResponse=httpRequest.request(Method.GET,"/booking/"+bookingId);
        Assert.assertEquals(httpResponse.getStatusCode(),200, "Updated Booking details Retreival Failed:: ");
        String firstNamefromResponse=httpResponse.jsonPath().get("firstname");
        Assert.assertEquals(firstNamefromResponse, "James","Mismatch between the expectedFirstName & firstNamefromResponse::");

    }
}
