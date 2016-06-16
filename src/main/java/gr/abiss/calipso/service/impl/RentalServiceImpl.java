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

import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import gr.abiss.calipso.model.Client;
import gr.abiss.calipso.model.Rental;
import gr.abiss.calipso.repository.ClientRepository;
import gr.abiss.calipso.service.RentalService;
import gr.abiss.calipso.tiers.repository.ModelRepository;
import gr.abiss.calipso.tiers.service.AbstractModelServiceImpl;

/**
 * Custom (VS calipso-generated) SCRUD service implementation, 
 * currently needed only to persist client bonus points
 *
 */
@Named("rentalService")
@Transactional(readOnly = true)
public class RentalServiceImpl extends AbstractModelServiceImpl<Rental, String, ModelRepository<Rental, String>>
		implements RentalService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(RentalServiceImpl.class);

	@Autowired
	protected ClientRepository clientRepository;
	
	/**
	 * Persist the rental and add the bonus points to the client.
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = false)
	public Rental create(Rental resource) {
		
		// create client if one does not exist
		Client client = this.clientRepository.getClientForUser(resource.getCustomer());
		if(client == null){
			client = this.clientRepository.save(new Client(resource.getCustomer()));
		}
		
		// persist rental
		resource = super.create(resource);
		
		// update points if needed
		int points = resource.getInventory().getFilm().getPricingStrategy().getBonusPoints();
		if(points > 0){
			this.clientRepository.addBonusPointsToClient(resource.getCustomer(), points);
		}
		
		// return rental
		return resource;
	}

}
