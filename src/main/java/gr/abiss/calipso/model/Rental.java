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

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonIgnore;

import gr.abiss.calipso.model.entities.AbstractAuditable;
import gr.abiss.calipso.tiers.annotation.ModelResource;

/**
 * The persistent class for the rental database table.
 * 
 */
@Entity
@Table(name = "rental")
@ModelResource(path = "rentals")
public class Rental extends AbstractAuditable<User> {
	private static final long serialVersionUID = 1L;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "rental_date")
	private Date rentalDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "return_date")
	private Date returnDate;

	// bi-directional many-to-one association to Payment
	@JsonIgnore
	@OneToMany(mappedBy = "rental")
	private List<Payment> payments;

	// bi-directional many-to-one association to Customer
	@ManyToOne
	@JoinColumn(name = "customer_id")
	private User customer;

	@ManyToOne
	@JoinColumn(name = "inventory_id")
	private FilmInventoryEntry inventory;

	public Rental() {
	}

	public Date getRentalDate() {
		return rentalDate;
	}

	public void setRentalDate(Date rentalDate) {
		this.rentalDate = rentalDate;
	}

	public Date getReturnDate() {
		return returnDate;
	}

	public void setReturnDate(Date returnDate) {
		this.returnDate = returnDate;
	}

	public List<Payment> getPayments() {
		return payments;
	}

	public void setPayments(List<Payment> payments) {
		this.payments = payments;
	}

	public User getCustomer() {
		return customer;
	}

	public void setCustomer(User customer) {
		this.customer = customer;
	}

	public FilmInventoryEntry getInventory() {
		return inventory;
	}

	public void setInventory(FilmInventoryEntry inventory) {
		this.inventory = inventory;
	}

	public static class Builder {
		private Date rentalDate;
		private Date returnDate;
		private List<Payment> payments;
		private User customer;
		private FilmInventoryEntry inventory;

		public Builder rentalDate(Date rentalDate) {
			this.rentalDate = rentalDate;
			return this;
		}

		public Builder returnDate(Date returnDate) {
			this.returnDate = returnDate;
			return this;
		}

		public Builder payments(List<Payment> payments) {
			this.payments = payments;
			return this;
		}

		public Builder customer(User customer) {
			this.customer = customer;
			return this;
		}

		public Builder inventory(FilmInventoryEntry inventory) {
			this.inventory = inventory;
			return this;
		}

		public Rental build() {
			return new Rental(this);
		}
	}

	private Rental(Builder builder) {
		this.rentalDate = builder.rentalDate;
		this.returnDate = builder.returnDate;
		this.payments = builder.payments;
		this.customer = builder.customer;
		this.inventory = builder.inventory;
	}
}