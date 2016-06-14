package gr.abiss.calipso.model;

import java.io.Serializable;
import javax.persistence.*;

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
	@OneToMany(mappedBy = "rental")
	private List<Payment> payments;

	// bi-directional many-to-one association to Customer
	@ManyToOne
	@JoinColumn(name = "customer_id")
	private User customer;

	// bi-directional many-to-one association to Inventory
	@ManyToOne
	@JoinColumn(name = "inventory_id")
	private FilmInventoryEntry inventory;

	// bi-directional many-to-one association to Staff
	@ManyToOne
	@JoinColumn(name = "staff_id")
	private User staff;

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

	public User getStaff() {
		return staff;
	}

	public void setStaff(User staff) {
		this.staff = staff;
	}

}