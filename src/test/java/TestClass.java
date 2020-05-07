import io.restassured.RestAssured;
import org.json.simple.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;
import io.restassured.specification.RequestSpecification;
import java.io.IOException;
import io.restassured.response.Response;
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
        var bookingid = 0;

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
       /* RequestSpecification reqspec;
        reqspec =given().contentType("application/json");
        reqspec.body(createBody);*/
        bookingid = given().log().all().contentType("application/json").body(
                createBody.toJSONString()).when().post("/booking").then()
                .log().all().extract().path("bookingid");
        
        // Get the booking using the id returned and assert firstname == Robert
        var firstname_out = given().log().all().contentType("application/json").when()
                 .get("/booking/" + bookingid).then().log().all().extract().path("firstname");
        Assert.assertEquals(firstname, firstname_out.toString());
        firstname = "James";
        createBody.remove("firstname");
        createBody.put("firstname", firstname);
       // Cookie requestCookie = new Cookie.Builder("token", authToken)
        // Update the firstname to James
        bookingid = given().log().all().contentType("application/json").accept("application/json").
        cookie("token" , authToken).body(createBody.toString()).when().put("/booking/" + bookingid).then().statusCode(200).log().all()
                .extract().path("bookingid");
        // Get the booking again and assert firstname == James
         firstname_out = given().log().all().contentType("application/json").when().get("/booking/" + bookingid)
                .then().log().all().extract().path("firstname");
        Assert.assertEquals(firstname, firstname_out.toString());

    }
    
}
