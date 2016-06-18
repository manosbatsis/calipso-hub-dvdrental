package gr.abiss.calipso.controller;

import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

import java.math.BigDecimal;

import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;

import gr.abiss.calipso.model.Client;
import gr.abiss.calipso.model.Film;
import gr.abiss.calipso.model.FilmInventoryEntry;
import gr.abiss.calipso.model.User;
import gr.abiss.calipso.model.dto.Order;
import gr.abiss.calipso.model.dto.Orders;
import gr.abiss.calipso.model.dto.Payments;
import io.restassured.RestAssured;
import io.restassured.config.ObjectMapperConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.mapper.factory.Jackson2ObjectMapperFactory;

@Test(singleThreaded = true, description = "Test orders and payments: cost calculation, validation and bonus points")
public class OrderControllerIT {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(OrderControllerIT.class);

	private static final String JSON_UTF8 = "application/json; charset=UTF-8";
	// configure the underlying Jackson object mapper as needed
	static {
		RestAssured.config = RestAssuredConfig.config().objectMapperConfig(
				new ObjectMapperConfig().jackson2ObjectMapperFactory(new Jackson2ObjectMapperFactory() {
					@Override
					public ObjectMapper create(Class aClass, String s) {
						ObjectMapper objectMapper = new ObjectMapper();
						objectMapper.registerModule(new JodaModule());
						objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
						return objectMapper;
					}
				}));
	}

	@Test(priority = 10, description = "Test new rentals")
	public void testNewRentals() throws Exception {

		// use a user as both staff and client
		User staff = this.getUser("client");

		// get film inentory entries, they are all available at this point
		FilmInventoryEntry matrix11 = this.getInventoryEntryByTitle("Matrix 11");
		FilmInventoryEntry spiderMan = this.getInventoryEntryByTitle("Spider-Man");
		FilmInventoryEntry spiderMan2 = this.getInventoryEntryByTitle("Spider-Man 2");
		FilmInventoryEntry outOfAfrica = this.getInventoryEntryByTitle("Out of Africa");

		// Test new rental orders
		// =========================
		LOGGER.info("");
		LOGGER.info("---------------------------------");
		LOGGER.info("Test Orders");
		LOGGER.info("---------------------------------");
		LOGGER.info("");
		LOGGER.info("Examples of price calculations");
		LOGGER.info("---------------------------------");
		Orders orders = new Orders(staff);
		// Matrix 11 (New release) 1 days 40 SEK
		orders.addItem(this.buildOrderAndTestCost(matrix11, 1, new BigDecimal(40)));
		// Spider Man (Regular rental) 5 days 90 SEK
		orders.addItem(this.buildOrderAndTestCost(spiderMan, 5, new BigDecimal(90)));
		// Spider Man 2 (Regular rental) 2 days 30 SEK
		orders.addItem(this.buildOrderAndTestCost(spiderMan2, 2, new BigDecimal(30)));
		// Out of Africa (Old film) 7 days 90 SEK
		orders.addItem(this.buildOrderAndTestCost(outOfAfrica, 7, new BigDecimal(90)));
		// Total price: 250 SEK
		this.saveOrdersAndTestCost(orders, new BigDecimal(250));

	}

	@Test(priority = 20, description = "Test rental returns")
	public void testRentalReturns() throws Exception {

		// use a user as both staff and client
		User staff = this.getUser("client");

		// get film inentory entries, they are all available at this point
		FilmInventoryEntry matrix11 = this.getInventoryEntryByTitle("Matrix 11");
		FilmInventoryEntry spiderMan = this.getInventoryEntryByTitle("Spider-Man");

		// Test rental returns
		// ========================
		LOGGER.info("");
		LOGGER.info("When returning films late");
		LOGGER.info("---------------------------------");
		Orders orders = new Orders(staff);
		// Matrix 11 (New release) 2 extra days 80 SEK
		orders.addItem(this.buildOrderAndTestCost(matrix11, 2, new BigDecimal(80)));
		// Spider Man (Regular rental) 1 days 30 SEK
		orders.addItem(this.buildOrderAndTestCost(spiderMan, 1, new BigDecimal(30)));
		// Total late charge: 110 SEK
		this.saveOrdersAndTestCost(orders, new BigDecimal(110));
		LOGGER.info("");

	}

	@Test(priority = 30, description = "Test bonus points")
	public void testBonusPoints() throws Exception {
		// get a client user
		Client client = this.getClient("client");
		
		// Test bonus points
		// ========================
		Assert.assertEquals(client.getBonusPoints(), new Integer(5));
		LOGGER.info("");
		LOGGER.info("Bonus points: " + client.getBonusPoints());
		LOGGER.info("---------------------------------");
		LOGGER.info("");
	}
	
	private Order buildOrderAndTestCost(FilmInventoryEntry inventoryEntry, int days, BigDecimal expectedCost) {
		// build the order
		Order order = given().param("days", days)
				.get("/calipso/api/rest/orders/{filmInventoryEntryId}", inventoryEntry.getId()).as(Order.class);
		// do a nice log
		LOGGER.info(inventoryEntry.getFilm().getTitle() + " (" + inventoryEntry.getFilm().getPricingStrategy().getName()
				+ "), cost for " + days + " days: " + order.getCost());
		// test cost matches expectations
		Assert.assertTrue(order.getCost().compareTo(expectedCost) == 0);
		// return the order for later use
		return order;
	}

	private void saveOrdersAndTestCost(Orders orders, BigDecimal expectedCost) {
		LOGGER.info("Total cost for " + orders.getItems().size() + " orders: " + orders.getTotalCost());
		
		// validate calculated order total cost
		Assert.assertTrue(orders.getTotalCost().compareTo(expectedCost) == 0);
		Payments payments = given().
			cookie("calipso-sso", "YWRtaW46YWRtaW4=").
			contentType("application/json; charset=UTF-16").
			body(orders).
		post("/calipso/api/rest/orders").
			as(Payments.class);
		
		// validate payments total cost
		Assert.assertTrue(payments.getTotalCost().compareTo(expectedCost) == 0);
	}

	private User getUser(String userNameOrEmail) {
		return get("/calipso/api/rest/users/byUserNameOrEmail/{userNameOrEmail}", userNameOrEmail).as(User.class);
	}

	public FilmInventoryEntry getInventoryEntryByTitle(String filmTitle) {
		Film film = this.getFilm(filmTitle);
		return this.getInventoryEntry(film);
	}

	public Film getFilm(String filmTitle) {
		String filmId = given().param("title", filmTitle).get("/calipso/api/rest/films").then().assertThat()
				.body("content[0].id", notNullValue()).extract().path("content[0].id");
		return get("/calipso/api/rest/films/{filmId}", filmId).as(Film.class);

	}

	public FilmInventoryEntry getInventoryEntry(Film film) {
		String id = given().param("film", film.getId()).get("/calipso/api/rest/filmInventoryEntries").then()
				.assertThat().body("content[0].id", notNullValue()).extract().path("content[0].id");
		return get("/calipso/api/rest/filmInventoryEntries/{id}", id).as(FilmInventoryEntry.class);
	}

	public Client getClient(User user) {
		String id = given().param("user", user.getId()).get("/calipso/api/rest/clients").then()
				.assertThat().body("content[0].id", notNullValue()).extract().path("content[0].id");
		return get("/calipso/api/rest/clients/{id}", id).as(Client.class);
	}

	public Client getClient(String userNameOrEmail) {
		User user = this.getUser(userNameOrEmail);
		return this.getClient(user);
	}

}