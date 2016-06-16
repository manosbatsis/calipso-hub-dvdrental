package gr.abiss.calipso.service;

import gr.abiss.calipso.model.User;
import gr.abiss.calipso.model.dto.Order;
import gr.abiss.calipso.model.dto.Orders;
import gr.abiss.calipso.model.dto.Payments;

public interface OrderService {

	/**
	 * Create an Order DTO given a filmInventoryEntryId and days to pay in advance 
	 * (for new rentals only, ignored for returns)
	 * @param filmInventoryEntryId
	 * @param days
	 * @return
	 */
	Order buildOrder(String filmInventoryEntryId, Integer days);

	/**
	 * Finalize the given orders by persisting the appropriate rentals/payments. User the current principal as the performing staff
	 * @param orders
	 * @return
	 */
	Payments finalizeOrders(Orders orders);

	/**
	 * Finalize the given orders by persisting the appropriate rentals/payments
	 * @param orders
	 * @return
	 */
	Payments finalizeOrders(Orders orders, User staff);

}