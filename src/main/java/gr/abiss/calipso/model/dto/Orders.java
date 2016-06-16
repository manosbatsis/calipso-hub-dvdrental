package gr.abiss.calipso.model.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Days;

import gr.abiss.calipso.model.Film;
import gr.abiss.calipso.model.FilmInventoryEntry;
import gr.abiss.calipso.model.FilmPricingStrategy;
import gr.abiss.calipso.model.Rental;
import gr.abiss.calipso.model.User;
import gr.abiss.calipso.model.enums.MpaaRating;

/**
 * DTO used to submit payment during rental/returns
 */
public class Orders implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * The enclosed orders to finalize
	 */
	private List<Order> items;

	/**
	 * The total cost of enclosed orders
	 */
	private BigDecimal totalCost;

	/**
	 * The customer performing the given orders
	 */
	private User customer;

	public Orders() {
		super();
	}

	public boolean addItems(List<Order> orders) {
		if (this.items == null) {
			this.items = new LinkedList<Order>();
		}
		return this.items.addAll(orders);
	}

	public boolean addItem(Order order) {
		if (this.items == null) {
			this.items = new LinkedList<Order>();
		}
		return this.items.add(order);
	}

	public List<Order> getItems() {
		return items;
	}

	public void setItems(List<Order> items) {
		this.items = items;
	}

	public BigDecimal getTotalCost() {
		return totalCost;
	}

	public void setTotalCost(BigDecimal totalCost) {
		this.totalCost = totalCost;
	}

	public User getCustomer() {
		return customer;
	}

	public void setCustomer(User customer) {
		this.customer = customer;
	}

}
