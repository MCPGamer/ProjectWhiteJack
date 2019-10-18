package ch.toast.whitejack.model;

import java.util.ArrayList;

public class Player {
	private int balance;
	private int bet;
	private ArrayList<Playingcard> cards;
	private int currentHandValue;

	public Player(ArrayList<Playingcard> cards) {
		balance = 100;
		this.cards = cards;
	}

	public int getBalance() {
		return balance;
	}

	public void setBalance(int balance) {
		this.balance = balance;
	}

	public int getBet() {
		return bet;
	}

	public void setBet(int bet) {
		this.bet = bet;
	}

	public ArrayList<Playingcard> getCards() {
		return cards;
	}

	public void setCards(ArrayList<Playingcard> cards) {
		this.cards = cards;
	}

	public int getCurrentHandValue() {
		return currentHandValue;
	}

	public void setCurrentHandValue(int currentHandValue) {
		this.currentHandValue = currentHandValue;
	}

	public void updateCurrentHandValue() {
		int count = 0;
		for (Playingcard card : cards) {
			switch (card.getValue()) {
				case "2":
				case "3":
				case "4":
				case "5":
				case "6":
				case "7":
				case "8":
				case "9":
				case "10":
					count += Integer.parseInt(card.getValue());
					break;
					
				case "KING":
				case "QUEEN":
				case "JACK":
					count += 10;
					break;
					
				case "ACE":
					if((count + 11) > 21) {
						count += 1;
						break;
					} else if ((count + 11) <= 21) {
						count += 11;
						break;
					}
				default:
					break;
			}
		}
		
		setCurrentHandValue(count);
	}
}
