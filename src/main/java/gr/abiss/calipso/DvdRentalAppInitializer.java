package gr.abiss.calipso;


import gr.abiss.calipso.model.Film;
import gr.abiss.calipso.model.FilmActor;
import gr.abiss.calipso.model.FilmPricingStrategy;
import gr.abiss.calipso.model.MpaaRating;
import gr.abiss.calipso.model.Role;
import gr.abiss.calipso.model.User;
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

	@Inject
	@Named("filmService")
	private ModelService<Film, String> filmService;
	
	@Inject
	@Named("filmActorService")
	private ModelService<FilmActor, String> filmActorService;

	@PostInitialize
	public void init() {
		// init calipso defaults
		super.init();
		

		BigDecimal premiumPrice = new BigDecimal(40.0); 
		BigDecimal basicPrice = new BigDecimal(30.0); 
		
		// create pricing strategies
		// ---------------------------
		// New releases – Price is <premium price> times number of days rented.
		FilmPricingStrategy newReleases = new FilmPricingStrategy("New releases", null, 0, premiumPrice);
		// Regular films – Price is <basic price> for the fist 3 days and then <basic price> times the number of days over 3. 
		FilmPricingStrategy regularFilms = new FilmPricingStrategy("Regular films", basicPrice, 3, basicPrice);
		// Old film - Price is <basic price> for the fist 5 days and then <basic price> times the number of days over 5
		FilmPricingStrategy oldFilms = new FilmPricingStrategy("Regular films", basicPrice, 5, basicPrice);

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
			MpaaRating.R, 
			keanuReeves
		);
		Film spiderMan = createFilm(
			"Spider-Man", 
			"When bitten by a genetically modified spider, a nerdy, shy, and awkward high school student gains spider-like abilities that he eventually must use to fight evil as a superhero after tragedy befalls his family.", 
			MpaaRating.PG13, 
			tobeyMaguire
		);
		Film spiderMan2 = createFilm(
			"Spider-Man", 
			"Peter Parker is beset with troubles in his failing personal life as he battles a brilliant scientist named Doctor Otto Octavius.",  
			MpaaRating.PG13, 
			tobeyMaguire
		);

		Film outOfAfrica = createFilm(
			"Out of Africa", 
			"In 20th-century colonial Kenya, a Danish baroness/plantation owner has a passionate love affair with a free-spirited big-game hunter.",  
			MpaaRating.PG, 
			merylStreep
		);
		
		Configuration config = ConfigurationFactory.getConfiguration();
		boolean initData = config.getBoolean(ConfigurationFactory.INIT_DATA, true);
		
		
		//this.initDatabaseMigration();
	}

	private Film createFilm(String title, String desc, MpaaRating mpaaRating, FilmActor keanuReeves) {
		Film film;
		film = new Film();
		film.setTitle(title);
		film.setDescription(desc);
		film.addActor(keanuReeves);
		return filmService.create(film);
	}

	private FilmActor createActor(String firstName, String lastName) {
		FilmActor actor = new FilmActor();
		actor.setFirstName(firstName);
		actor.setLastName(lastName);
		actor = filmActorService.create(actor);
		return actor;
	}

}
