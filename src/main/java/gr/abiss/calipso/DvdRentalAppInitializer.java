/**
 * Copyright (c) 2007 - 2013 Manos Batsis
 *
 * Calipso is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Calipso is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Calipso. If not, see http://www.gnu.org/licenses/agpl.html
 */
package gr.abiss.calipso;

import java.math.BigDecimal;
import java.util.Calendar;
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
import gr.abiss.calipso.repository.ClientRepository;
import gr.abiss.calipso.service.OrderService;
import gr.abiss.calipso.service.RentalService;
import gr.abiss.calipso.service.RoleService;
import gr.abiss.calipso.service.UserService;
import gr.abiss.calipso.tiers.service.ModelService;

@Named("dvdRentalAppInitializer")
public class DvdRentalAppInitializer extends gr.abiss.calipso.AppInitializer {

	private static final Logger LOGGER = LoggerFactory.getLogger(AppInitializer.class);

	@Inject
	@Named("dataSource")
	private DataSource dataSource;


	// service is created by calipso using using javassist at runtime so we use
	// a generic definition
	@Inject
	@Named("filmService")
	private ModelService<Film, String> filmService;

	// service is created by calipso using using javassist at runtime so we use
	// a generic definition
	@Inject
	@Named("filmActorService")
	private ModelService<FilmActor, String> filmActorService;

	// service is created by calipso using using javassist at runtime so we use
	// a generic definition
	@Inject
	@Named("filmPricingStrategyService")
	private ModelService<FilmPricingStrategy, String> filmPricingStrategyService;

	// service is created by calipso using using javassist at runtime so we use
	// a generic definition
	@Inject
	@Named("filmInventoryEntryService")
	private ModelService<FilmInventoryEntry, String> filmInventoryEntryService;


	@Inject
	@Named("userService")
	private UserService userService;

	@Inject
	@Named("roleService")
	private RoleService roleService;

	@Inject
	@Named("orderService")
	private OrderService orderService;

	@Inject
	@Named("rentalService")
	private RentalService rentalService;
	
	@Inject
	@Named("clientRepository")
	private ClientRepository clientRepository;
	
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
		
		User client = new User();
		client.setEmail("client@abiss.gr");
		client.setFirstName("Client");
		client.setLastName("User");
		client.setUsername("client");
		client.setPassword("client");
		client.setLastVisit(new Date());
		client = userService.createActive(client);

		// prices
		// ---------------------------
		BigDecimal premiumPrice = new BigDecimal(40.0);
		BigDecimal basicPrice = new BigDecimal(30.0);

		// create pricing strategies
		// ---------------------------
		// New releases – Price is <premium price> times number of days rented.
		// 2 bonus points.
		FilmPricingStrategy newReleases = this.filmPricingStrategyService
				.create(new FilmPricingStrategy("New releases", premiumPrice, 1, premiumPrice, 2));
		// Regular films – Price is <basic price> for the fist 3 days and then
		// <basic price> times the number of days over 3. 1 bonus point.
		FilmPricingStrategy regularFilms = this.filmPricingStrategyService
				.create(new FilmPricingStrategy("Regular films", basicPrice, 3, basicPrice, 1));
		// Old film - Price is <basic price> for the fist 5 days and then <basic
		// price> times the number of days over 5. 1 bonus point.
		FilmPricingStrategy oldFilms = this.filmPricingStrategyService
				.create(new FilmPricingStrategy("Regular films", basicPrice, 5, basicPrice, 1));

		// create actors
		// ---------------------------
		FilmActor keanuReeves = createActor("Keanu", "Reeves");
		FilmActor tobeyMaguire = createActor("Tobey", "Maguire");
		FilmActor merylStreep = createActor("Meryl", "Streep");

		// create films
		// ---------------------------
		Film matrix11 = createFilm("Matrix 11", "Neo tries canned beans", newReleases, MpaaRating.R, keanuReeves);
		Film spiderMan = createFilm("Spider-Man",
				"When bitten by a genetically modified spider, a nerdy, shy, and awkward high school student gains spider-like abilities that he eventually must use to fight evil as a superhero after tragedy befalls his family.",
				regularFilms, MpaaRating.PG13, tobeyMaguire);
		Film spiderMan2 = createFilm("Spider-Man 2",
				"Peter Parker is beset with troubles in his failing personal life as he battles a brilliant scientist named Doctor Otto Octavius.",
				regularFilms, MpaaRating.PG13, tobeyMaguire);

		Film outOfAfrica = createFilm("Out of Africa",
				"In 20th-century colonial Kenya, a Danish baroness/plantation owner has a passionate love affair with a free-spirited big-game hunter.",
				oldFilms, MpaaRating.PG, merylStreep);

	}


	/**
	 * Create a film and an inventory entry
	 * 
	 * @param title
	 * @param desc
	 * @param mpaaRating
	 * @param keanuReeves
	 * @return
	 */
	private Film createFilm(String title, String desc, FilmPricingStrategy pricingStrategy, MpaaRating mpaaRating,
			FilmActor keanuReeves) {
		Film film;
		film = new Film();
		film.setTitle(title);
		film.setDescription(desc);
		film.setPricingStrategy(pricingStrategy);
		film.setMpaaRating(mpaaRating);
		film.addActor(keanuReeves);
		film = this.filmService.create(film);
		// create inventory entries (i.e. rentable physical copies) for the
		// given film
		film.addInventory(filmInventoryEntryService.create(new FilmInventoryEntry(film)));
		return film;
	}

	/**
	 * Create a film actor
	 * 
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
}
