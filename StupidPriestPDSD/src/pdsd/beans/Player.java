package pdsd.beans;

import java.util.ArrayList;

public class Player {

	private Integer userId;
	
	private Player nextPlayer;
	
	private ArrayList<Card> cards;
	
	private Card receivedCard;
	
	private Card giveAwayCard;

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Player getNextPlayer() {
		return nextPlayer;
	}

	public void setNextPlayer(Player nextPlayer) {
		this.nextPlayer = nextPlayer;
	}

	public ArrayList<Card> getCards() {
		return cards;
	}

	public void setCards(ArrayList<Card> cards) {
		this.cards = cards;
	}

	public Card getReceivedCard() {
		return receivedCard;
	}

	public void setReceivedCard(Card receivedCard) {
		this.receivedCard = receivedCard;
	}

	public Card getGiveAwayCard() {
		return giveAwayCard;
	}

	public void setGiveAwayCard(Card giveAwayCard) {
		this.giveAwayCard = giveAwayCard;
	}
	
	
	
}
