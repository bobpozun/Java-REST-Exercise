import io.restassured.RestAssured;
import io.restassured.http.Header;
import io.restassured.http.Headers;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
 
import org.json.simple.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;
import org.testng.asserts.Assertion;

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

        Header h1 = new Header("Content-Type", "application/json");
         Header h2 = new Header("Accept", "application/json");
        Headers headers = new Headers(h1,h2);


        
        Response response = given().headers(headers).body(createBody.toJSONString()).when().post("/booking");
       // Assertion assert = new Assertion();
       // assert.assertEquals(response.getStatusCode(), 200)
       System.out.println(response.getStatusCode());
       JsonPath path = new JsonPath(response.getBody().asString());
        System.out.println("he post response is" + response.getBody().asString());
       System.out.println("The first name is: " + path.getString("booking.firstname"));
      
        String bookingid = path.getString("bookingid");

        Response getresponse = given().headers(headers).when().get("/booking/"+bookingid);
       // Assertion assert = new Assertion();
        System.out.println("Get reponse status code" + getresponse.getStatusCode());
        JsonPath path1 = new JsonPath(getresponse.getBody().asString());
        System.out.println("the get response is" + getresponse.getBody().asString());
         Assert.assertEquals(path1.getString("firstname"), firstname);

        Header h3 = new Header("Content-Type", "application/json");
         Header h4 = new Header("Accept", "application/json");
        Header h5 = new Header("Cookie", "token="+authToken);
        Headers headers1 = new Headers(h3,h4,h5);
         
        JSONObject createBody1 = new JSONObject();
        createBody1.put("firstname", "James");
    
        Response putResponse = given().headers(headers1).body(createBody1.toJSONString()).when().patch("/booking/"+bookingid);
        System.out.println("put reponse status code" + putResponse.getStatusCode());
        JsonPath path2 = new JsonPath(putResponse.getBody().asString());
        System.out.println("the put response is" + putResponse.getBody().asString());
         Assert.assertEquals(path2.getString("firstname"), "James");


         Response getresponse1 = given().headers(headers).when().get("/booking/"+bookingid);
       // Assertion assert = new Assertion();
        System.out.println("Get reponse1 status code" + getresponse1.getStatusCode());
        JsonPath path3 = new JsonPath(getresponse1.getBody().asString());
        System.out.println("the get response1 is" + getresponse1.getBody().asString());
         Assert.assertEquals(path3.getString("firstname"), "James");




        /* 
            Create a booking using the above json object
            Get the booking using the id returned and assert firstname == Robert
            Update the firstname to James
            Get the booking again and assert firstname == James
        */

    }
}
