package pdsd.service;

import java.util.ArrayList;
import java.util.Collections;
import pdsd.beans.Card;
import pdsd.enums.CardColor;
import pdsd.enums.CardNumber;

public class Util {

	public static ArrayList<ArrayList<Card>> shuffle() {
		ArrayList<Card> cards = new ArrayList<Card>();
		for (CardNumber number : CardNumber.values()) {
			if (number == CardNumber.KING) {
				continue;
			}
			for (CardColor color : CardColor.values()) {
				Card card = new Card();
				card.setColor(color);
				card.setNumber(number);
				cards.add(card);
			}
		}
		Collections.shuffle(cards);
		ArrayList<ArrayList<Card>> cardLists = new ArrayList<ArrayList<Card>>();
		for (int i = 0; i < 15; i += 4) {
			ArrayList<Card> part = new ArrayList<Card>();
			part.add(cards.get(i));
			part.add(cards.get(i + 1));
			part.add(cards.get(i + 2));
			part.add(cards.get(i + 3));
			cardLists.add(part);
		}
		double random = Math.random();
		Card stupid = new Card();
		stupid.setColor(CardColor.HEARTS);
		stupid.setNumber(CardNumber.KING);
		if (random <= 0.25) {
			cardLists.get(0).add(stupid);
		} else if (random <= 0.5) {
			cardLists.get(0).add(stupid);
		} else if (random <= 0.75) {
			cardLists.get(0).add(stupid);
		} else {
			cardLists.get(0).add(stupid);
		}
		return cardLists;
	}

}
