import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import jdk.nashorn.internal.parser.JSONParser;

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
    public void setup() {
        RestAssured.baseURI = "https://restful-booker.herokuapp.com/";
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();

        get("/ping").then().statusCode(201);

        JSONObject authBody = new JSONObject();
        authBody.put("username", "admin");
        authBody.put("password", "password123");
        authToken = given().log().all().contentType("application/json").body(authBody.toJSONString()).when()
                .post("/auth").then().log().all().extract().path("token");
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

        // Create a booking using the above json object
        // Get the booking using the id returned and assert firstname == Robert
        // Update the firstname to James
        // Get the booking again and assert firstname == James

        RequestSpecification request = given().relaxedHTTPSValidation().auth().basic("", "").log().all();
        request.contentType("application/json");
        request.body(createBody.toJSONString());
        Response response = request.post("/booking");
        response.prettyPrint();

        JsonPath jsonpath = new JsonPath(response.then().extract().asString());
        int bookingID= jsonpath.getInt("bookingid");
        System.out.println(bookingID);



        RequestSpecification getBookingID= given().relaxedHTTPSValidation().auth().basic("", "").log().all();
        Response getBookingIDResponse = getBookingID.get("/booking/"+bookingID);
        getBookingIDResponse.prettyPrint();
        String firstName_Actual =getBookingIDResponse.path("firstname");
        Assert.assertEquals(firstname,firstName_Actual,"The String did not Match");

        RequestSpecification updatingFNRequest = given().relaxedHTTPSValidation().auth().basic("", "").log().all();
        updatingFNRequest.header("Content-Type","application/json");
        updatingFNRequest.header("Accept","application/json");
        updatingFNRequest.cookie("token",authToken);
        updatingFNRequest.body("{\"firstname\" : \"James\"}");
        Response updateResponse = updatingFNRequest.patch("/booking/"+bookingID);
        updateResponse.prettyPrint();


        RequestSpecification recallGet= given().relaxedHTTPSValidation().auth().basic("", "").log().all();
        Response recallGetResponse = recallGet.get("/booking/"+bookingID);
        recallGetResponse.prettyPrint();
        String UpdatedfirstName =recallGetResponse.path("firstname");
        Assert.assertEquals(UpdatedfirstName,"James","FN validation");


    }






}
