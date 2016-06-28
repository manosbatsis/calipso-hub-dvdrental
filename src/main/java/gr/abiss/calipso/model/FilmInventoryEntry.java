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
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Formula;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import gr.abiss.calipso.model.entities.AbstractAuditable;
import gr.abiss.calipso.tiers.annotation.ModelResource;
import io.swagger.annotations.ApiModel;

/**
 * The persistent class for the inventory entries
 * 
 */
@Entity
@Table(name = "film_inventory_entry")
@ModelResource(path = "filmInventoryEntries", apiName = "Film inventory entries", apiDescription = "Operations about film inventory entries")
@ApiModel(value = "Film Inventory", description = "A model representing a film inventory entry")
public class FilmInventoryEntry  extends AbstractAuditable<User> {
	private static final long serialVersionUID = 1L;


	@Formula(" current_rental_id IS NULL ")
	private Boolean available = true;

	// bi-directional many-to-one association to Film
	@ManyToOne
	@JoinColumn(name = "film_id", nullable = false)
	private Film film;

	
	// ignore in break possible cyclic ref during serialization
	@JsonIgnore
	@OneToOne
	@JoinColumn(name="current_rental_id")
	private Rental currentRental;

	public FilmInventoryEntry() {
		super();
	}

	public FilmInventoryEntry(Film film) {
		this();
		this.film = film;
	}

	public Film getFilm() {
		return film;
	}

	public void setFilm(Film film) {
		this.film = film;
	}

	public Rental getCurrentRental() {
		return currentRental;
	}

	public void setCurrentRental(Rental currentRental) {
		this.currentRental = currentRental;
	}

}