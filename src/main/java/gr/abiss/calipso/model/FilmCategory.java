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

import javax.persistence.Entity;
import javax.persistence.Table;

import gr.abiss.calipso.model.base.AbstractCategory;
import gr.abiss.calipso.tiers.annotation.ModelResource;
import io.swagger.annotations.ApiModel;


/**
 * The persistent class for the film_category/genre.
 * 
 */
@Entity
@Table(name="film_category")
@ModelResource(path = "filmCategories", apiName = "Film categories", apiDescription = "Operations about film categories")
@ApiModel(value = "Film Category", description = "A model representing a film category")
public class FilmCategory extends AbstractCategory<FilmCategory>{
	
	private static final long serialVersionUID = 1L;

	public FilmCategory() {
	}

}