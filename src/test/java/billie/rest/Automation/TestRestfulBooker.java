/**
 * 
 */
package billie.rest.Automation;

import static io.restassured.RestAssured.given;

import java.util.HashMap;
import java.util.Map;

import org.hamcrest.Matchers;
import org.testng.Assert;
import org.testng.annotations.Test;

import io.restassured.http.ContentType;
import io.restassured.internal.http.HttpResponseException;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

/**
 * @author ahmetizgi
 *
 */
public class TestRestfulBooker {

	private static int bookId;

	@Test
	public void test_0001_createBooking() {

		Map<String, Object> booking = getSampleBookingData();

		// Send request and check status code is correct
		Response response = given().contentType(ContentType.JSON).with().body(booking).when()
				.post("https://restful-booker.herokuapp.com/booking").then().statusCode(200)
				.body("bookingid", Matchers.is(Matchers.notNullValue())).extract().response();

		// Extract data from reponsebody
		Map<String, Object> responseBodyMap = response.jsonPath().getMap("booking");
		bookId = response.jsonPath().getInt("bookingid");

		// Check request and body booking details correct
		for (Map.Entry bookingField : booking.entrySet()) {
			Assert.assertEquals(bookingField.getValue(), responseBodyMap.get(bookingField.getKey()),
					"Posted \"" + bookingField.getKey() + "\" not found in response body. ");
		}

	}

	@Test
	public void test_0002_updateBooking() {

		Map<String, Object> booking = getSampleBookingData();
		// Update sample data
		booking.put("firstname", "Bugra");

		// Send request and check status code is correct
		Response response = given().baseUri("https://restful-booker.herokuapp.com/booking/" + bookId)
				.cookie("token", generateToken()).contentType(ContentType.JSON).body(booking).when().put().then()
				.statusCode(200).extract().response();

		// Extract data from reponsebody
		Map<String, Object> responseBodyMap = response.jsonPath().getMap("");

		// Check request and body booking details correct
		for (Map.Entry bookingField : booking.entrySet()) {
			Assert.assertEquals(bookingField.getValue(), responseBodyMap.get(bookingField.getKey()),
					"Put \"" + bookingField.getKey() + "\" not found in response body. ");
		}
	}

	@Test
	public void test_0003_getBooking() {

		Map<String, Object> booking = getSampleBookingData();
		// Update sample data
		booking.put("firstname", "Bugra");

		// Send request and check status code is correct
		Response response = given().baseUri("https://restful-booker.herokuapp.com/booking/" + bookId)
				.contentType(ContentType.JSON).body(booking).when().get().then().statusCode(200).extract().response();

		// Extract data from reponsebody
		Map<String, Object> responseBodyMap = response.jsonPath().getMap("");

		// Check request and body booking details correct
		for (Map.Entry bookingField : booking.entrySet()) {
			Assert.assertEquals(bookingField.getValue(), responseBodyMap.get(bookingField.getKey()),
					"Expected \"" + bookingField.getKey() + "\" not found in response body. ");
		}
	}

	@Test
	public void test_0004_deleteBooking() {

		// Send delete request and check status code is correct
		given().baseUri("https://restful-booker.herokuapp.com/booking/" + bookId).cookie("token", generateToken())
				.contentType(ContentType.JSON).when().delete().then().statusCode(201).extract().response();

		// Check deleted item is not accessible
		try {
			given().accept(ContentType.ANY).get("https://restful-booker.herokuapp.com/booking/" + bookId).then()
					.statusCode(404).extract().response();
		} catch (Exception ex) {
			Assert.assertTrue(ex instanceof HttpResponseException && ex.getMessage().contains("Not Found"),
					"Expected 'Not Found' but actual is different!");
		}

	}

	/**
	 * Authenticates with default username and password and generates token.
	 * 
	 * @return token string.
	 */
	private String generateToken() {

		// Create data for request
		Map<String, Object> credentials = new HashMap<String, Object>();
		credentials.put("username", "admin");
		credentials.put("password", "password123");

		// Send request and check status code is correct
		Response response = given().contentType(ContentType.JSON).with().body(credentials).when()
				.post("https://restful-booker.herokuapp.com/auth").then().statusCode(200).extract().response();

		// Extract data from response body
		String token = response.jsonPath().get("token");

		// Check request and body booking details correct

		return token;
	}

	/**
	 * Creates sample booking data for tests. (We could also use POJOs instead of
	 * using Maps)
	 * 
	 * @return Map<String, Object> with related data.
	 */
	private Map<String, Object> getSampleBookingData() {
		Map<String, Object> bookingDates = new HashMap<String, Object>();
		bookingDates.put("checkin", "2020-01-01");
		bookingDates.put("checkout", "2020-05-01");

		Map<String, Object> booking = new HashMap<String, Object>();
		booking.put("firstname", "Ahmet");
		booking.put("lastname", "Izgi");
		booking.put("totalprice", 693);
		booking.put("depositpaid", true);
		booking.put("bookingdates", bookingDates);
		booking.put("additionalneeds", "Breakfast");

		return booking;
	}

}
