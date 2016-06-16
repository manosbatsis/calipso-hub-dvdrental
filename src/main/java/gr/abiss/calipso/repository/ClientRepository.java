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
package gr.abiss.calipso.repository;

import javax.inject.Named;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import gr.abiss.calipso.model.Client;
import gr.abiss.calipso.model.User;
import gr.abiss.calipso.tiers.repository.ModelRepository;

@Named("clientRepository")
public interface ClientRepository  extends ModelRepository<Client, String> {

	@Modifying
	@Query("UPDATE Client AS c SET c.bonusPoints = c.bonusPoints + ?2 WHERE c.user = ?1")
	public void addBonusPointsToClient(User user, int points);

	@Query("SELECT c.bonusPoints FROM Client AS c WHERE c.user = ?1")
	public Integer getBonusPointsForUser(User user);
	
	@Query("SELECT c FROM Client AS c WHERE c.user = ?1")
	public Client getClientForUser(User user);

}