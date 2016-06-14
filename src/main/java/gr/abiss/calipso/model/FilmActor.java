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