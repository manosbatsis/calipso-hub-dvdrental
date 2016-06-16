package gr.abiss.calipso;


import gr.abiss.calipso.model.Film;
import gr.abiss.calipso.model.FilmActor;
import gr.abiss.calipso.model.FilmInventoryEntry;
import gr.abiss.calipso.model.FilmPricingStrategy;
import gr.abiss.calipso.model.Role;
import gr.abiss.calipso.model.User;
import gr.abiss.calipso.model.enums.MpaaRating;
import gr.abiss.calipso.service.RoleService;
import gr.abiss.calipso.service.UserService;
import gr.abiss.calipso.tiers.service.ModelService;
import gr.abiss.calipso.utils.ConfigurationFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;
import javax.sql.DataSource;

import org.apache.commons.configuration.Configuration;
import org.flywaydb.core.Flyway;
import org.resthub.common.util.PostInitialize;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



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
		
		// create data for integration tests
		// ---------------------------
//		Matrix 11 (New release) 1 days 40 SEK
//		Spider Man (Regular rental) 5 days 90 SEK
//		Spider Man 2 (Regular rental) 2 days 30 SEK
//		Out of Africa (Old film) 7 days 90 SEK
//		Total price: 250 SEK
		
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
		film = filmService.create(film);
		// create an inventory entry that corresponds to a physical copy
		filmInventoryEntryService.create(new FilmInventoryEntry(film));
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
