package gr.abiss.calipso.model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonIgnore;

import gr.abiss.calipso.model.User;
import gr.abiss.calipso.model.entities.AbstractAuditable;
import gr.abiss.calipso.model.enums.MpaaRating;
import gr.abiss.calipso.tiers.annotation.ModelResource;

/**
 * The persistent class for films
 */
@Entity
@Table(name = "film")
@ModelResource(path = "films")
public class Film extends AbstractAuditable<User> {
	private static final long serialVersionUID = 1L;

	@Lob
	private String description;

	@Column(name = "mpaa_rating")
	private MpaaRating mpaaRating = MpaaRating.UNRATED;

	@Column(name = "runtime_seconds")
	private int runtimeInSeconds;

	@Temporal(TemporalType.DATE)
	@Column(name = "release_year")
	private Date releaseYear;

	@Column(name = "replacement_cost")
	private BigDecimal replacementCost;

	private String title;

	@Column(name = "locale", nullable = false)
	private String primaryLanguage = "en";

	@ManyToOne(optional = false)
	@JoinColumn(name = "pricing_strategy_id")
	private FilmPricingStrategy pricingStrategy;

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "film_filmactors", joinColumns = { @JoinColumn(name = "actor_id") }, inverseJoinColumns = {
			@JoinColumn(name = "film_id") })
	private List<FilmActor> filmActors;

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "film_categories", joinColumns = { @JoinColumn(name = "category_id") }, inverseJoinColumns = {
			@JoinColumn(name = "film_id") })
	private List<FilmCategory> filmCategories;

	// bi-directional many-to-one association to Inventory
	@JsonIgnore
	@OneToMany(mappedBy = "film")
	private List<FilmInventoryEntry> inventories;

	public Film() {
	}

	public boolean addActor(FilmActor actor) {
		if (this.getFilmActors() == null) {
			this.filmActors = new LinkedList<FilmActor>();
		}
		return this.filmActors.add(actor);
	}

	public boolean addCategory(FilmCategory category) {
		if (this.getFilmCategories() == null) {
			this.filmCategories = new LinkedList<FilmCategory>();
		}
		return this.filmCategories.add(category);
	}
	
	public boolean addInventory(FilmInventoryEntry inventoryEntry) {
		if (this.getFilmCategories() == null) {
			this.inventories = new LinkedList<FilmInventoryEntry>();
		}
		return this.inventories.add(inventoryEntry);
	}

	public Film(String title) {
		super();
		this.title = title;
	}

	public MpaaRating getMpaaRating() {
		return mpaaRating;
	}

	public void setMpaaRating(MpaaRating mpaaRating) {
		this.mpaaRating = mpaaRating;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getRuntimeInSeconds() {
		return runtimeInSeconds;
	}

	public void setRuntimeInSeconds(int runtimeInSeconds) {
		this.runtimeInSeconds = runtimeInSeconds;
	}

	public Date getReleaseYear() {
		return releaseYear;
	}

	public void setReleaseYear(Date releaseYear) {
		this.releaseYear = releaseYear;
	}

	public BigDecimal getReplacementCost() {
		return replacementCost;
	}

	public void setReplacementCost(BigDecimal replacementCost) {
		this.replacementCost = replacementCost;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getPrimaryLanguage() {
		return primaryLanguage;
	}

	public void setPrimaryLanguage(String primaryLanguage) {
		this.primaryLanguage = primaryLanguage;
	}

	public FilmPricingStrategy getPricingStrategy() {
		return pricingStrategy;
	}

	public void setPricingStrategy(FilmPricingStrategy pricingStrategy) {
		this.pricingStrategy = pricingStrategy;
	}

	public List<FilmActor> getFilmActors() {
		return filmActors;
	}

	public void setFilmActors(List<FilmActor> filmActors) {
		this.filmActors = filmActors;
	}

	public List<FilmCategory> getFilmCategories() {
		return filmCategories;
	}

	public void setFilmCategories(List<FilmCategory> filmCategories) {
		this.filmCategories = filmCategories;
	}

	public List<FilmInventoryEntry> getInventories() {
		return inventories;
	}

	public void setInventories(List<FilmInventoryEntry> inventories) {
		this.inventories = inventories;
	}

}