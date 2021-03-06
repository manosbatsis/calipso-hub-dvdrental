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
package gr.abiss.calipso.service.impl;

import java.util.Date;

import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import gr.abiss.calipso.model.FilmInventoryEntry;
import gr.abiss.calipso.model.Payment;
import gr.abiss.calipso.model.Rental;
import gr.abiss.calipso.model.User;
import gr.abiss.calipso.model.dto.Order;
import gr.abiss.calipso.model.dto.Orders;
import gr.abiss.calipso.model.dto.Payments;
import gr.abiss.calipso.service.OrderService;
import gr.abiss.calipso.tiers.service.ModelService;
import gr.abiss.calipso.userDetails.util.SecurityUtil;

/**
 *  {@inheritDoc}
 */
@Named("orderService")
@Transactional(readOnly = true)
public class OrderServiceImpl implements OrderService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(OrderServiceImpl.class);
	
	private ModelService<FilmInventoryEntry, String> filmInventoryEntryService;

	private ModelService<Rental, String> rentalService;
	
	private ModelService<Payment, String> paymentService;
	
	@Inject
	public void setFilmInventoryEntryService(ModelService<FilmInventoryEntry, String> filmInventoryEntryService) {
		this.filmInventoryEntryService = filmInventoryEntryService;
	}

	@Inject
	public void setRentalService(ModelService<Rental, String> rentalService) {
		this.rentalService = rentalService;
	}

	@Inject
	public void setPaymentService(ModelService<Payment, String> paymentService) {
		this.paymentService = paymentService;
	}
	
	/**
	 *  {@inheritDoc}
	 */
	@Override
	public Order buildOrder(String filmInventoryEntryId, Integer days) {

		FilmInventoryEntry filmInventoryEntry = this.filmInventoryEntryService.findById(filmInventoryEntryId);
		return new Order.Builder().filmInventoryEntry(filmInventoryEntry).days(days).build();
	}

	/**
	 *  {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = false)
	public Payments finalizeOrders(Orders orders) {
		Payments payments = new Payments();
		try{
			
			Date now = new Date();
			
			// process order items 
			for(Order givenOrder : orders.getItems()){
				
				// validate given order
				FilmInventoryEntry filmInventoryEntry = this.filmInventoryEntryService.findById(givenOrder.getInventoryId());
				Order validatedOrder = new Order.Builder().filmInventoryEntry(filmInventoryEntry).days(givenOrder.getDays()).build();
				if(validatedOrder.getCost().compareTo(givenOrder.getCost()) != 0){
					throw new RuntimeException("Invalid order cost: " + givenOrder.getCost() + " for film: '" + givenOrder.getFilmTitle() + "', days: " + givenOrder.getDays());
				}
				
				Rental rental = filmInventoryEntry.getCurrentRental();
				int days = givenOrder.getDays();
				
				
				// if new rental
				if(rental == null){

					// create a new rental
					rental = new Rental.Builder()
						.customer(orders.getCustomer())
						.inventory(filmInventoryEntry)
						.rentalDate(now)
						.build();
					rental = this.rentalService.create(rental);
					
					//  attach the new rental in the inventory entry
					filmInventoryEntry.setCurrentRental(rental);
					this.filmInventoryEntryService.update(filmInventoryEntry);
				}
				// else close current rental and remove from inventory entry
				else{
					rental.setReturnDate(now);
					this.rentalService.update(rental);
					
					filmInventoryEntry.setCurrentRental(null);
					this.filmInventoryEntryService.update(filmInventoryEntry);
				}
				
				// create the corresponding payment
				Payment payment = new Payment.Builder()
						.amount(givenOrder.getCost())
						.days(givenOrder.getDays())
						.customer(orders.getCustomer())
						.staff(new User(SecurityUtil.getPrincipal().getId()))
						.rental(rental)
						.build();
				payment = this.paymentService.create(payment);
				payments.addItem(payment);
			}
		}
		catch(Exception e){
			throw new IllegalArgumentException("Faild validating and proccessing orders", e);
		}
		return payments;
	}

	
	
}
