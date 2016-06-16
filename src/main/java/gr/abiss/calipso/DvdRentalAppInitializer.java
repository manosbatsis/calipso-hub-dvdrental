package gr.abiss.calipso;


import java.math.BigDecimal;
import java.util.Date;

import javax.inject.Inject;
import javax.inject.Named;
import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.resthub.common.util.PostInitialize;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import gr.abiss.calipso.model.Film;
import gr.abiss.calipso.model.FilmActor;
import gr.abiss.calipso.model.FilmInventoryEntry;
import gr.abiss.calipso.model.FilmPricingStrategy;
import gr.abiss.calipso.model.Role;
import gr.abiss.calipso.model.User;
import gr.abiss.calipso.model.dto.Orders;
import gr.abiss.calipso.model.dto.Payments;
import gr.abiss.calipso.model.enums.MpaaRating;
import gr.abiss.calipso.service.OrderService;
import gr.abiss.calipso.service.RoleService;
import gr.abiss.calipso.service.UserService;
import gr.abiss.calipso.tiers.service.ModelService;



@Named("dvdRentalAppInitializer")
public class DvdRentalAppInitializer extends gr.abiss.calipso.AppInitializer {

	private static final Logger LOGGER = LoggerFactory.getLogger(AppInitializer.class);
	

	@Inject
	@Named("dataSource")
    private DataSource dataSource;

	protected void initDatabaseMigration() {
        // Create the Flyway instance
        Flyway flyway = new Flyway();
        // Point to the database
        flyway.setDataSource(this.dataSource);
        // misc config
        flyway.setBaselineOnMigrate(true);
        flyway.setValidateOnMigrate(false);
        // Start the migration
        flyway.migrate();
	}

	@Inject
	@Named("userService")
	private UserService userService;	

	@Inject
	@Named("roleService")
	private RoleService roleService;
	
	@Inject
	@Named("orderService")
	private OrderService orderService;

	// service is created by calipso using using javassist at runtime so we use a generic definition
	@Inject
	@Named("filmService")
	private ModelService<Film, String> filmService;

	// service is created by calipso using using javassist at runtime so we use a generic definition
	@Inject
	@Named("filmActorService")
	private ModelService<FilmActor, String> filmActorService;
	
	// service is created by calipso using using javassist at runtime so we use a generic definition
	@Inject
	@Named("filmPricingStrategyService")
	private ModelService<FilmPricingStrategy, String> filmPricingStrategyService;

	// service is created by calipso using using javassist at runtime so we use a generic definition
	@Inject
	@Named("filmInventoryEntryService")
	private ModelService<FilmInventoryEntry, String> filmInventoryEntryService;
	
	/**
	 * @PostInitialize sample data for films, inventory entries etc.
	 */
	@PostInitialize
	public void init() {
		
		// init calipso defaults
		// ---------------------------
		super.init();
		
		// create staff role
		// ---------------------------
		Role staffRole = new Role("ROLE_STAFF", "Store Staff.");
		staffRole = roleService.create(staffRole);
		
		// create a staff user

		User staff = new User();
		staff.setEmail("staff@abiss.gr");
		staff.setFirstName("Staff");
		staff.setLastName("User");
		staff.setUsername("staff");
		staff.setPassword("staff");
		staff.setLastVisit(new Date());
		staff.addRole(staffRole);
		staff = userService.createActive(staff);

		// prices
		// ---------------------------
		BigDecimal premiumPrice = new BigDecimal(40.0); 
		BigDecimal basicPrice = new BigDecimal(30.0); 
		
		// create pricing strategies
		// ---------------------------
		// New releases – Price is <premium price> times number of days rented. 2 bonus points.
		FilmPricingStrategy newReleases = this.filmPricingStrategyService.create(new FilmPricingStrategy("New releases", premiumPrice, 1, premiumPrice, 2));
		// Regular films – Price is <basic price> for the fist 3 days and then <basic price> times the number of days over 3. 1 bonus point.
		FilmPricingStrategy regularFilms = this.filmPricingStrategyService.create(new FilmPricingStrategy("Regular films", basicPrice, 3, basicPrice, 1));
		// Old film - Price is <basic price> for the fist 5 days and then <basic price> times the number of days over 5. 1 bonus point.
		FilmPricingStrategy oldFilms = this.filmPricingStrategyService.create(new FilmPricingStrategy("Regular films", basicPrice, 5, basicPrice, 1));

		// create actors
		// ---------------------------
		FilmActor keanuReeves = createActor("Keanu", "Reeves");
		FilmActor tobeyMaguire = createActor("Tobey", "Maguire");
		FilmActor merylStreep = createActor("Meryl", "Streep");
		
		// create films
		// ---------------------------
		Film matrix11 = createFilm(
			"Matrix 11", 
			"Neo tries canned beans", 
			newReleases,
			MpaaRating.R, 
			keanuReeves
		);
		Film spiderMan = createFilm(
			"Spider-Man", 
			"When bitten by a genetically modified spider, a nerdy, shy, and awkward high school student gains spider-like abilities that he eventually must use to fight evil as a superhero after tragedy befalls his family.",
			regularFilms, 
			MpaaRating.PG13, 
			tobeyMaguire
		);
		Film spiderMan2 = createFilm(
			"Spider-Man", 
			"Peter Parker is beset with troubles in his failing personal life as he battles a brilliant scientist named Doctor Otto Octavius.",
			regularFilms, 
			MpaaRating.PG13, 
			tobeyMaguire
		);

		Film outOfAfrica = createFilm(
			"Out of Africa", 
			"In 20th-century colonial Kenya, a Danish baroness/plantation owner has a passionate love affair with a free-spirited big-game hunter.",
			oldFilms,
			MpaaRating.PG, 
			merylStreep
		);
		
		// TODO: move to unit/integration tests
		// ---------------------------
//		Matrix 11 (New release) 1 days 40 SEK
//		Spider Man (Regular rental) 5 days 90 SEK
//		Spider Man 2 (Regular rental) 2 days 30 SEK
//		Out of Africa (Old film) 7 days 90 SEK
//		Total price: 250 SEK

		testRentFilm(matrix11, 1, new BigDecimal(40), staff);
		testRentFilm(spiderMan, 5, new BigDecimal(90), staff);
		testRentFilm(spiderMan2, 2, new BigDecimal(30), staff);
		testRentFilm(outOfAfrica, 7, new BigDecimal(90), staff);
		
	}

	private void testRentFilm(Film film, int days, BigDecimal expectedCost, User staff) {
		// just get an inventory entry, they're all available at this point
		FilmInventoryEntry inventoryEntry = film.getInventories().get(0);
		
		// create the order, using the staff user as a customer as well
		Orders orders = new Orders(staff);
		orders.addItem(this.orderService.buildOrder(inventoryEntry.getId(), days));
		
		// persist/finalize the order
		Payments payments = this.orderService.finalizeOrders(orders, staff);
		
		LOGGER.info(film.getTitle() + ", cost for " + days + " days: " + payments.getItems().get(0).getAmount());
		// test the payment amount
		Assert.isTrue(expectedCost.compareTo(payments.getTotalCost()) == 0);
	}

	/**
	 * Create a film and an inventory entry 
	 * @param title
	 * @param desc
	 * @param mpaaRating
	 * @param keanuReeves
	 * @return
	 */
	private Film createFilm(String title, String desc, FilmPricingStrategy pricingStrategy, MpaaRating mpaaRating, FilmActor keanuReeves) {
		Film film;
		film = new Film();
		film.setTitle(title);
		film.setDescription(desc);
		film.setPricingStrategy(pricingStrategy);
		film.setMpaaRating(mpaaRating);
		film.addActor(keanuReeves);
		film = this.filmService.create(film);
		// create inventory entries (i.e. rentable physical copies) for the given film
		film.addInventory(filmInventoryEntryService.create(new FilmInventoryEntry(film)));
		film.addInventory(filmInventoryEntryService.create(new FilmInventoryEntry(film)));
		return film;
	}

	/**
	 * Create a film actor
	 * @param firstName
	 * @param lastName
	 * @return
	 */
	private FilmActor createActor(String firstName, String lastName) {
		FilmActor actor = new FilmActor();
		actor.setFirstName(firstName);
		actor.setLastName(lastName);
		actor = filmActorService.create(actor);
		return actor;
	}

}
