package gr.abiss.calipso.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

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

	@Column(nullable = false)
	private Boolean available = true;

	// bi-directional many-to-one association to Film
	@ManyToOne
	@JoinColumn(name = "film_id", nullable = false)
	private Film film;

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