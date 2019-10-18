package ch.toast.whitejack.controller;

import java.util.ArrayList;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.google.gson.Gson;

import ch.toast.whitejack.model.CardHandler;
import ch.toast.whitejack.model.Deck;
import ch.toast.whitejack.model.Player;
import ch.toast.whitejack.model.Playingcard;

@Controller
public class MainController {
	private CardHandler cardHandler = new CardHandler();
	private Player player;
	private Player dealer;
	private Boolean gameRunning = false;
	private Boolean endTurn = false;
	private ArrayList<Playingcard> drawnCards;
	private ArrayList<Playingcard> dealerCards;
	private Deck deck;

	
	@GetMapping("/")
	public String index(Model model) {
		drawnCards = null;
		return "index";
	}

	@GetMapping("game")
	public String playGame(Model model) {
		if (drawnCards == null) {
			drawnCards = new ArrayList<>();
			if(player != null) {
				player.setCards(drawnCards);
			}
		}

		if (dealerCards == null) {
			dealerCards = new ArrayList<>();
			if(dealer != null) {
				dealer.setCards(dealerCards);
			}
		}
		
		if(player == null) {
			player = new Player(drawnCards);
		}
		
		if(dealer == null) {
			dealer = new Player(dealerCards);
		}
		
		model.addAttribute("DrawnCards", drawnCards);
		model.addAttribute("DealerCards", dealerCards);
		model.addAttribute("player", player);
		model.addAttribute("dealer", dealer);
		model.addAttribute("gameRunning", gameRunning);
		model.addAttribute("endTurn", endTurn);
		return "game";
	}
	
	@PostMapping("game")
	public String setBet(Model model, @ModelAttribute Player player) {
		if(!gameRunning) {
			this.player.setBet(player.getBet());
			model.addAttribute("player", this.player);
		}

		return "game";
	}

	@GetMapping("game/start")
	public String startGame(Model model) {
		Gson gson = new Gson();
		deck = gson.fromJson(cardHandler.shuffle(), Deck.class);
		cardHandler.drawCard(deck, drawnCards, 1);
		cardHandler.drawCard(deck, dealerCards, 1);
		cardHandler.drawCard(deck, drawnCards, 1);
		dealerCards.add(new Playingcard(cardHandler.drawCard(deck, true)));
		gameRunning = true;
		return "redirect:../game";
	}

	@GetMapping("game/draw")
	public String drawCards(Model model) {
		cardHandler.drawCard(deck, drawnCards, 1);
		
		player.setCards(drawnCards);
		player.updateCurrentHandValue();
		int playerPoints = player.getCurrentHandValue();
		int playerDifferenceTo21 = 21 - playerPoints;
		
		if(playerDifferenceTo21 < 0) {
			return finish(model);
		} else {
			return "redirect:../game";
		}
	}

	@GetMapping("game/reset")
	public String resetGame(Model model) {
		player = null;
		dealer = null;
		return endGame(model);
	}
	
	@GetMapping("game/end")
	public String endGame(Model model) {
		drawnCards = null;
		dealerCards = null;
		gameRunning = false;
		endTurn = false;
		return "redirect:../game";
	}
	
	@GetMapping("game/finish")
	public String finish(Model model) {
		endTurn = true;
		boolean won = false;

		dealerCards.set(1, dealerCards.get(1).getTurnedCard());
		dealerCards.get(1).setImage("/images/" + dealerCards.get(1).getImage().substring(dealerCards.get(1).getImage().length() - 7));
		
		player.setCards(drawnCards);
		dealer.setCards(dealerCards);
		
		player.updateCurrentHandValue();
		dealer.updateCurrentHandValue();
		
		int playerPoints = player.getCurrentHandValue();
		int playerDifferenceTo21 = 21 - playerPoints;

		while(dealer.getCurrentHandValue() < 17 && playerDifferenceTo21 >= 0 && dealer.getCurrentHandValue() < player.getCurrentHandValue()) {
			cardHandler.drawCard(deck, dealerCards, 1);
			dealer.updateCurrentHandValue();
		}
		

		int dealerPoints = dealer.getCurrentHandValue();
		int dealerDifferenceTo21 = 21 - dealerPoints;
		
		
		if(playerDifferenceTo21 >= 0) {
			if(dealerDifferenceTo21 < 0) {
				won = true;
			} else if(playerDifferenceTo21 < dealerDifferenceTo21) {
				won = true;
			} else if (playerDifferenceTo21 == dealerDifferenceTo21) {
				// Draw
				return "redirect:../game";
			}
		}
		
		
		if(won) {
			player.setBalance(player.getBalance() + player.getBet());
		} else {
			player.setBalance(player.getBalance() - player.getBet());
		}
		return "redirect:../game";
	}
}