package gr.abiss.calipso.model;

import java.io.Serializable;
import javax.persistence.*;

import gr.abiss.calipso.model.User;
import gr.abiss.calipso.model.entities.AbstractAuditable;
import gr.abiss.calipso.tiers.annotation.ModelResource;

import java.math.BigDecimal;
import java.util.Date;
import java.sql.Timestamp;

/**
 * The persistent class for the payment database table.
 * 
 */
@Entity
@Table(name = "payment")
@ModelResource(path = "payments")
public class Payment extends AbstractAuditable<User> {

	private static final long serialVersionUID = 1L;

	private BigDecimal amount;

	// bi-directional many-to-one association to Customer
	@ManyToOne
	@JoinColumn(name = "customer_id")
	private User customer;

	// bi-directional many-to-one association to Rental
	@ManyToOne
	@JoinColumn(name = "rental_id")
	private Rental rental;

	// bi-directional many-to-one association to Staff
	@ManyToOne
	@JoinColumn(name = "staff_id")
	private User staff;

	public Payment() {
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

}