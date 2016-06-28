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
package gr.abiss.calipso.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import gr.abiss.calipso.model.entities.AbstractAuditable;
import gr.abiss.calipso.tiers.annotation.ModelResource;
import io.swagger.annotations.ApiModel;

/**
 * The persistent class for clients. Used to hang custom information like bonus points.
 */
@Entity
@Table(name = "client")
@ModelResource(path = "clients", apiName = "Clients", apiDescription = "Operations about clients")
@ApiModel(value = "Client", description = "A model representing a store client")
public class Client extends AbstractAuditable<User> {

	private static final long serialVersionUID = 1L;

	@Column(name = "bonus_points", nullable = false)
	private Integer bonusPoints = 0;

	@OneToOne(optional = false)
	@JoinColumn(name = "user_id")
	private User user;

	public Client() {
		super();
	}

	public Client(User user) {
		this();
		this.user = user;
	}

	public Integer getBonusPoints() {
		return bonusPoints;
	}

	public void setBonusPoints(Integer bonusPoints) {
		this.bonusPoints = bonusPoints;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

}
