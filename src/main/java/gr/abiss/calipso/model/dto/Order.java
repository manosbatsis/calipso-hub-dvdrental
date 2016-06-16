package gr.abiss.calipso.model.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.Days;

import gr.abiss.calipso.model.Film;
import gr.abiss.calipso.model.FilmInventoryEntry;
import gr.abiss.calipso.model.FilmPricingStrategy;
import gr.abiss.calipso.model.Rental;
import gr.abiss.calipso.model.enums.MpaaRating;

/**
 * DTO used by OrderConteoller. Includes an implementation of
 * the builder pattern.
 */
public class Order implements Serializable {

	private static final long serialVersionUID = 1L;
	
	/**
	 * he UUID of the inventory entry
	 */
	private String inventoryId;

	/**
	 * The film title
	 */
	private String filmTitle;

	/**
	 * The film MPAA rating (unrated, G, PG, PG-13, R, NC-17)
	 */
	private MpaaRating filmMpaa;

	/**
	 * The UUID of the current inventory rental or null if new (i.e. if the
	 * inventory entry is available)
	 */
	private String rentalId;

	/**
	 * The initial or pending cost for the inventory entry rental
	 */
	private BigDecimal cost;

	/**
	 * The number of days included in the cost
	 */
	private Integer days;

	/**
	 * The pricing strategy
	 */
	private FilmPricingStrategy pricingStrategy;

	public Order() {
		super();
	}

	public String getInventoryId() {
		return inventoryId;
	}

	public void setInventoryId(String inventoryId) {
		this.inventoryId = inventoryId;
	}

	public String getFilmTitle() {
		return filmTitle;
	}

	public void setFilmTitle(String filmTitle) {
		this.filmTitle = filmTitle;
	}

	public MpaaRating getFilmMpaa() {
		return filmMpaa;
	}

	public void setFilmMpaa(MpaaRating filmMpaa) {
		this.filmMpaa = filmMpaa;
	}

	public String getRentalId() {
		return rentalId;
	}

	public void setRentalId(String rentalId) {
		this.rentalId = rentalId;
	}

	public BigDecimal getCost() {
		return cost;
	}

	public void setCost(BigDecimal cost) {
		this.cost = cost;
	}

	public Integer getDays() {
		return days;
	}

	public void setDays(Integer days) {
		this.days = days;
	}

	public FilmPricingStrategy getPricingStrategy() {
		return pricingStrategy;
	}

	public void setPricingStrategy(FilmPricingStrategy pricingStrategy) {
		this.pricingStrategy = pricingStrategy;
	}

	/**
	 * Builder pattern implementation
	 */
	public static class Builder {
		private String inventoryId;
		private String filmTitle;
		private MpaaRating filmMpaa;
		private Rental currentRental;
		private BigDecimal cost;
		private Integer days;
		private FilmPricingStrategy pricingStrategy;

		public Builder filmInventoryEntry(FilmInventoryEntry filmInventoryEntry) {
			return this.inventoryId(filmInventoryEntry.getId())
					.rental(filmInventoryEntry.getCurrentRental())
					.film(filmInventoryEntry.getFilm());
		}

		public Builder rental(Rental currentRental) {
			this.currentRental = currentRental;
			return this;
		}

		public Builder film(Film film) {
			return this.filmMpaa(film.getMpaaRating()).pricingStrategy(film.getPricingStrategy());
		}

		public Builder inventoryId(String inventoryId) {
			this.inventoryId = inventoryId;
			return this;
		}

		public Builder filmTitle(String filmTitle) {
			this.filmTitle = filmTitle;
			return this;
		}

		public Builder filmMpaa(MpaaRating filmMpaa) {
			this.filmMpaa = filmMpaa;
			return this;
		}


		public Builder days(Integer days) {
			this.days = days;
			return this;
		}

		public Builder pricingStrategy(FilmPricingStrategy pricingStrategy) {
			this.pricingStrategy = pricingStrategy;
			return this;
		}

		public Order build() {
			return new Order(this);
		}
	}

	private Order(Builder builder) {
		this.inventoryId = builder.inventoryId;
		this.filmTitle = builder.filmTitle;
		this.filmMpaa = builder.filmMpaa;
		this.rentalId = builder.currentRental != null ? builder.currentRental.getId() : null;
		this.pricingStrategy = builder.pricingStrategy;
		this.days = builder.days;
		
		// if new rental
		if(builder.currentRental == null){
			// min rent paid in advance is one day
			if (this.days < 1) {
				this.days = 1;
			}
			// calculate cost
			this.cost = this.pricingStrategy.getInitialPrice();
			// if client requested more days than those included for free in initial rent price
			if(this.days > this.pricingStrategy.getDaysFree()){
				this.cost.add(
					this.pricingStrategy.getDailyPrice().multiply(
						new BigDecimal(this.days - this.pricingStrategy.getDaysFree())));
			}
		}
		// if return
		else{
			// how many days have passed since rental?
			DateTime today = new DateTime();
			int daysKept = Days.daysBetween(builder.currentRental.getCreatedDate(), today).getDays();
			int daysPaid = builder.currentRental.getPayments().get(0).getDays();
			// calculate days late/pending payment
			this.days = daysKept - daysPaid;
			if(this.days > 0){
				this.cost = this.pricingStrategy.getDailyPrice().multiply(
					new BigDecimal(this.days));
			}
			else{
				this.cost = BigDecimal.ZERO;
			}
		}
	}
}
