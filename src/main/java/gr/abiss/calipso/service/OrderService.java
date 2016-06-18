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
package gr.abiss.calipso.service;

import gr.abiss.calipso.model.User;
import gr.abiss.calipso.model.dto.Order;
import gr.abiss.calipso.model.dto.Orders;
import gr.abiss.calipso.model.dto.Payments;

/**
 * Provides "facade" methods for building and persisting rental/return orders 
 */
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


}