package gr.abiss.calipso.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import gr.abiss.calipso.model.User;
import gr.abiss.calipso.model.entities.AbstractAuditable;
import gr.abiss.calipso.tiers.annotation.ModelResource;

/**
 * The persistent class for the inventory entries
 * 
 */
@Entity
@Table(name = "film_inventory_entry")
@ModelResource(path = "filmInventoryEntries")
public class FilmInventoryEntry  extends AbstractAuditable<User> {
	private static final long serialVersionUID = 1L;

	// bi-directional many-to-one association to Film
	@ManyToOne
	@JoinColumn(name = "film_id")
	private Film film;

	// bi-directional many-to-one association to Rental
	@OneToMany(mappedBy = "inventory")
	private List<Rental> rentals;

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

	public List<Rental> getRentals() {
		return rentals;
	}

	public void setRentals(List<Rental> rentals) {
		this.rentals = rentals;
	}

}