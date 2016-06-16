package gr.abiss.calipso.model;

import java.io.Serializable;
import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

import gr.abiss.calipso.model.User;
import gr.abiss.calipso.model.entities.AbstractAuditable;
import gr.abiss.calipso.tiers.annotation.ModelResource;

import java.util.Date;
import java.sql.Timestamp;
import java.util.List;

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