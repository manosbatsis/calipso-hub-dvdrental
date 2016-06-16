package gr.abiss.calipso.controller;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import gr.abiss.calipso.model.dto.Order;
import gr.abiss.calipso.service.OrderService;

@Controller
@RequestMapping(value = "/api/rest/orders", produces = { "application/json", "application/xml" })
public class OrderConteoller {

	private static final Logger LOGGER = LoggerFactory.getLogger(OrderConteoller.class);
	
	private OrderService orderService;
	
	@Inject
	@Qualifier("orderService")
	public void setFilmInventoryOrderService(OrderService orderService) {
		this.orderService = orderService;
	}

	/**
	 * Create an appropriate order DTO and return it
	 * @param filmInventoryEntryId
	 * @param days
	 * @return
	 */
	@RequestMapping(value = "{filmInventoryEntryId}", method = RequestMethod.GET)
	@ResponseBody
	public Order buildOrder(@PathVariable String filmInventoryEntryId,
			@RequestParam(value = "days", required = false) Integer days) {
		return this.orderService.buildOrder(filmInventoryEntryId, days);
	}
	

}
