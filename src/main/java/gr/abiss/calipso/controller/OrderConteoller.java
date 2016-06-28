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
package gr.abiss.calipso.controller;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import gr.abiss.calipso.model.User;
import gr.abiss.calipso.model.dto.Order;
import gr.abiss.calipso.model.dto.Orders;
import gr.abiss.calipso.model.dto.Payments;
import gr.abiss.calipso.service.OrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * Provides "facade" endpoints for building and persisting rental/return orders 
 */
@Controller
@Api(tags = "Orders", description = "Operations about orders")
@RequestMapping(value = "/api/rest/orders", produces = { "application/json", "application/xml" })
public class OrderConteoller {

	private static final Logger LOGGER = LoggerFactory.getLogger(OrderConteoller.class);
	
	private OrderService orderService;
	
	@Inject
	@Qualifier("orderService")
	public void setOrderService(OrderService orderService) {
		this.orderService = orderService;
	}

	/**
	 * Create an appropriate order DTO and return it. Nothing is persisted during this call.
	 * @param filmInventoryEntryId the inventory id (path variable)
	 * @param days the number of days intended to rent (request parameter)
	 * @return an order with calculated cost
	 */
    @ApiOperation(value = "Build order")
	@RequestMapping(value = "{filmInventoryEntryId}", method = RequestMethod.GET)
	@ResponseBody
	public Order buildOrder(@PathVariable String filmInventoryEntryId,
			@RequestParam(value = "days", required = false) Integer days) {
		return this.orderService.buildOrder(filmInventoryEntryId, days);
	}


	/**
	 * Finalize the given orders by persisting the appropriate rentals/payments. 
	 * @param orders
	 * @return
	 */
    @ApiOperation(value = "Finilize orders")
	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	public Payments finalizeOrders(@RequestBody Orders orders) {
		return this.orderService.finalizeOrders(orders);
	}
	
}
