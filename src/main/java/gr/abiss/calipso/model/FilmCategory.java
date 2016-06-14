package gr.abiss.calipso.model;

import javax.persistence.Entity;
import javax.persistence.Table;

import gr.abiss.calipso.model.base.AbstractCategory;
import gr.abiss.calipso.tiers.annotation.ModelResource;


/**
 * The persistent class for the film_category/genre.
 * 
 */
@Entity
@Table(name="film_category")
@ModelResource(path = "filmCategories")
public class FilmCategory extends AbstractCategory<FilmCategory>{
	
	private static final long serialVersionUID = 1L;

	public FilmCategory() {
	}

}