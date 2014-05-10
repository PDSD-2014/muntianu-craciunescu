package pdsd.beans;

import pdsd.enums.CardColor;
import pdsd.enums.CardNumber;

public class Card {

	private CardColor color;

	private CardNumber number;

	public CardColor getColor() {
		return color;
	}

	public void setColor(CardColor color) {
		this.color = color;
	}

	public CardNumber getNumber() {
		return number;
	}

	public void setNumber(CardNumber number) {
		this.number = number;
	}

}
