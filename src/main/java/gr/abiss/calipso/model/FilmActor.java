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
import javax.persistence.Table;

import gr.abiss.calipso.model.User;
import gr.abiss.calipso.model.entities.AbstractAuditable;
import gr.abiss.calipso.tiers.annotation.ModelResource;


/**
 * The persistent class for the film_actor database table.
 */
@Entity
@Table(name="film_actor")
@ModelResource(path = "filmActors")
public class FilmActor extends AbstractAuditable<User> {
	private static final long serialVersionUID = 1L;


	@Column(name="first_name")
	private String firstName;

	@Column(name="last_name")
	private String lastName;

	public FilmActor() {
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}


}