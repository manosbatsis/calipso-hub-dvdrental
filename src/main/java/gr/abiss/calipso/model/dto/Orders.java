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
	
	public Orders(User customer) {
		this();
		this.customer = customer;
	}

	public boolean addItem(Order order) {
		if (this.items == null) {
			this.items = new LinkedList<Order>();
			this.totalCost = new BigDecimal(0);
		}
		this.totalCost = this.totalCost.add(order.getCost());
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
