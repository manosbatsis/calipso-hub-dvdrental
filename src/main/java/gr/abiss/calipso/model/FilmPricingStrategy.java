package gr.abiss.calipso.model;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import gr.abiss.calipso.model.User;
import gr.abiss.calipso.model.entities.AbstractAuditable;
import gr.abiss.calipso.tiers.annotation.ModelResource;

/**
 * <p>Entity that persists a pricing strategy. Includes an implementation of the builder pattern.</p>
 * p>The price of rentals is based type of film rented and how many days the
 * film is rented for. The customers say when renting for how many days they
 * want to rent for and pay up front. If the film is returned late, then rent
 * for the extra days is charged when returning.
 * </p>
 * 
 * <p>
 * The store has three types of films.
 * </p>
 * <ul>
 * <li>New releases – Price is <premium price> times number of days rented.</li>
 * <li>Regular films – Price is <basic price> for the fist 3 days and then
 * <basic price> times the number of days over 3.</li>
 * <li>Old film - Price is <basic price> for the fist 5 days and then <basic
 * price> times the number of days over 5</li>
 * </ul>
 * <p>
 * This is modeled in three properties:
 * <dl>
 * <dt>initialPrice</dt>
 * <dd>the price to rent a film</dd>
 * <dt>daysFree</dt>
 * <dd>the number of days included in the initialPrice</dd>
 * <dt>dailyPrice</dt>
 * <dd>the price per day (not counting daysFree)</dd>
 * </dl>
 * 
 */
@Entity
@Table(name = "film_pricing_strategy")
@ModelResource(path = "filmPricingStrategies")
public class FilmPricingStrategy extends AbstractAuditable<User> {

	private static final long serialVersionUID = 1L;

	private String name;

	private BigDecimal initialPrice;

	private Integer daysFree;

	private BigDecimal dailyPrice;

	private Integer bonusPoints = 0;

	public FilmPricingStrategy() {
	}

	public FilmPricingStrategy(String name, BigDecimal initialPrice, Integer daysFree, BigDecimal dailyPrice,
			Integer bonusPoints) {
		super();
		this.name = name;
		this.initialPrice = initialPrice;
		this.daysFree = daysFree;
		this.dailyPrice = dailyPrice;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public BigDecimal getInitialPrice() {
		return initialPrice;
	}

	public void setInitialPrice(BigDecimal initialPrice) {
		this.initialPrice = initialPrice;
	}

	public Integer getDaysFree() {
		return daysFree;
	}

	public void setDaysFree(Integer daysFree) {
		this.daysFree = daysFree;
	}

	public BigDecimal getDailyPrice() {
		return dailyPrice;
	}

	public void setDailyPrice(BigDecimal dailyPrice) {
		this.dailyPrice = dailyPrice;
	}

	public Integer getBonusPoints() {
		return bonusPoints;
	}

	public void setBonusPoints(Integer bonusPoints) {
		this.bonusPoints = bonusPoints;
	}

	/**
	 * Builder pattern implementation
	 */
	public static class Builder {
		private String name;
		private BigDecimal initialPrice;
		private Integer daysFree;
		private BigDecimal dailyPrice;
		private Integer bonusPoints;

		public Builder name(String name) {
			this.name = name;
			return this;
		}

		public Builder initialPrice(BigDecimal initialPrice) {
			this.initialPrice = initialPrice;
			return this;
		}

		public Builder daysFree(Integer daysFree) {
			this.daysFree = daysFree;
			return this;
		}

		public Builder dailyPrice(BigDecimal dailyPrice) {
			this.dailyPrice = dailyPrice;
			return this;
		}

		public Builder bonusPoints(Integer bonusPoints) {
			this.bonusPoints = bonusPoints;
			return this;
		}

		public FilmPricingStrategy build() {
			return new FilmPricingStrategy(this);
		}
	}

	private FilmPricingStrategy(Builder builder) {
		this.name = builder.name;
		this.initialPrice = builder.initialPrice;
		this.daysFree = builder.daysFree;
		this.dailyPrice = builder.dailyPrice;
		this.bonusPoints = builder.bonusPoints;
	}
}