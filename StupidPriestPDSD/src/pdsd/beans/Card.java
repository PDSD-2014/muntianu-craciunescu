package pdsd.beans;

import pdsd.enums.CardColor;

public class Card {

	private CardColor color;

	private Integer value;

	public CardColor getColor() {
		return color;
	}

	public void setColor(CardColor color) {
		this.color = color;
	}

	public Integer getValue() {
		return value;
	}

	public void setValue(Integer value) {
		this.value = value;
	}

}
