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
package gr.abiss.calipso.model;

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import gr.abiss.calipso.model.entities.AbstractAuditable;
import gr.abiss.calipso.tiers.annotation.ModelResource;
import io.swagger.annotations.ApiModel;

/**
 * The persistent class for the payment database table.
 * 
 */
@Entity
@Table(name = "payment")
@ModelResource(path = "payments", apiName = "Payments", apiDescription = "Operations about payments")
@ApiModel(value = "Payment", description = "A model representing a payment")
public class Payment extends AbstractAuditable<User> {

	private static final long serialVersionUID = 1L;

	/**
	 * The days paid for
	 */
	private Integer days;

	/**
	 * The amount paid
	 */
	private BigDecimal amount;

	// bi-directional many-to-one association to Customer
	@ManyToOne(optional = false)
	@JoinColumn(name = "customer_id")
	private User customer;

	// bi-directional many-to-one association to Rental
	@ManyToOne(optional = false)
	@JoinColumn(name = "rental_id")
	private Rental rental;

	// bi-directional many-to-one association to Staff
	@ManyToOne(optional = false)
	@JoinColumn(name = "staff_id")
	private User staff;

	public Payment() {
	}

	public Integer getDays() {
		return days;
	}

	public void setDays(Integer days) {
		this.days = days;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public User getCustomer() {
		return customer;
	}

	public void setCustomer(User customer) {
		this.customer = customer;
	}

	public Rental getRental() {
		return rental;
	}

	public void setRental(Rental rental) {
		this.rental = rental;
	}

	public User getStaff() {
		return staff;
	}

	public void setStaff(User staff) {
		this.staff = staff;
	}

	public static class Builder {
		private Integer days;
		private BigDecimal amount;
		private User customer;
		private Rental rental;
		private User staff;

		public Builder days(Integer days) {
			this.days = days;
			return this;
		}

		public Builder amount(BigDecimal amount) {
			this.amount = amount;
			return this;
		}

		public Builder customer(User customer) {
			this.customer = customer;
			return this;
		}

		public Builder rental(Rental rental) {
			this.rental = rental;
			return this;
		}

		public Builder staff(User staff) {
			this.staff = staff;
			return this;
		}

		public Payment build() {
			return new Payment(this);
		}
	}

	private Payment(Builder builder) {
		this.days = builder.days;
		this.amount = builder.amount;
		this.customer = builder.customer;
		this.rental = builder.rental;
		this.staff = builder.staff;
	}
}