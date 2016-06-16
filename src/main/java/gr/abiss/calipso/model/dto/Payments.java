package gr.abiss.calipso.model.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gr.abiss.calipso.model.Payment;
import gr.abiss.calipso.model.User;

/**
 * DTO used to return payments during rental/returns
 */
public class Payments implements Serializable {

	private static final Logger LOGGER = LoggerFactory.getLogger(Payments.class);
			
	private static final long serialVersionUID = 1L;

	/**
	 * The enclosed payments to finalize
	 */
	private List<Payment> items;

	/**
	 * The total cost of enclosed payments
	 */
	private BigDecimal totalCost = new BigDecimal(0);

	/**
	 * The customer performing the given payments
	 */
	private User customer;

	public Payments() {
		super();
	}

	/**
	 * Add the item and update the total cost
	 * @param payment
	 * @return
	 */
	public boolean addItem(Payment payment) {
		if (this.items == null) {
			this.items = new LinkedList<Payment>();
		}
		this.totalCost = this.totalCost.add(payment.getAmount());
		return this.items.add(payment);
	}

	public List<Payment> getItems() {
		return items;
	}

	public BigDecimal getTotalCost() {
		return totalCost;
	}

	public User getCustomer() {
		return customer;
	}

	public void setCustomer(User customer) {
		this.customer = customer;
	}

}
