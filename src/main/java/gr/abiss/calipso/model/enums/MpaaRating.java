package gr.abiss.calipso.model.enums;

public enum MpaaRating {
	UNRATED("unrated"), 
	G, 
	PG, 
	PG13("PG-13"), 
	R, 
	NC17("NC-17");

	private final String mpaa;

	MpaaRating() {
		mpaa = this.name();
	}

	MpaaRating(String alt) {
		mpaa = alt;
	}

	public String mpaa() {
		return mpaa;
	}

	public static MpaaRating getFromMpaa(String mpaa) {
		if (mpaa == null) {
			return null;
		}
		for (MpaaRating rating : values()) {
			if (rating.mpaa().equals(mpaa)) {
				return rating;
			}
		}
		throw new IllegalArgumentException("no Movie Rating mapping for " + mpaa);
	}
}