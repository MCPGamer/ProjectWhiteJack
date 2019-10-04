package ch.toast.whitejack.controller;

import java.util.ArrayList;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.google.gson.Gson;

import ch.toast.whitejack.model.CardHandler;
import ch.toast.whitejack.model.Deck;
import ch.toast.whitejack.model.Playingcard;

@Controller
public class MainController {
	private ArrayList<Playingcard> drawnCards;
	private Deck deck;
	private CardHandler cardHandler = new CardHandler();

	@GetMapping("/")
	public String index(Model model) {
		drawnCards = null;
		return "index";
	}

	@GetMapping("game")
	public String playGame(Model model) {
		if (drawnCards == null) {
			drawnCards = new ArrayList<>();
		}
		model.addAttribute("DrawnCards", drawnCards);
		return "game";
	}

	@GetMapping("game/start")
	public String startGame(Model model) {
		Gson gson = new Gson();
		deck = gson.fromJson(cardHandler.shuffle(), Deck.class);
		cardHandler.drawCard(deck, drawnCards, 2);
		return "redirect:../game";
	}

	@GetMapping("game/draw")
	public String drawCards(Model model) {
		cardHandler.drawCard(deck, drawnCards, 1);
		return "redirect:../game";
	}

	@GetMapping("game/reset")
	public String resetGame(Model model) {
		drawnCards = null;
		return "redirect:../game";
	}
}