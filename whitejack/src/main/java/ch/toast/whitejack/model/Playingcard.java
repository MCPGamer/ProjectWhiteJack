package ch.toast.whitejack.model;

public class Playingcard {
	private String image;
	private String value;
	private String suit;
	private String code;
	
	// Used for turned over Cards (where u see cardback)
	private Playingcard turnedCard;

	public Playingcard() {

	}
	
	public Playingcard(Playingcard playingcard) {
		image = "/images/Cardback.png";
		value = "0";
		value = "0";
		value = "0";
		turnedCard = playingcard;
	}
	
	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getSuit() {
		return suit;
	}

	public void setSuit(String suit) {
		this.suit = suit;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Playingcard getTurnedCard() {
		return turnedCard;
	}

	public void setTurnedCard(Playingcard turnedCard) {
		this.turnedCard = turnedCard;
	}
}
