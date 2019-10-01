package ch.toast.whitejack.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.google.gson.Gson;

import ch.toast.whitejack.model.Deck;
import ch.toast.whitejack.model.Playingcard;

@Controller
public class MainController {
	private ArrayList<Playingcard> drawnCards;
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
		}
		model.addAttribute("DrawnCards", drawnCards);
		return "game";
	}

	@GetMapping("game/start")
	public String startGame(Model model) {
		Gson gson = new Gson();
		deck = gson.fromJson(shuffle(), Deck.class);
		drawCard(2);
		return "redirect:../game";
	}

	@GetMapping("game/draw")
	public String drawCards(Model model) {
		drawCard(1);
		return "redirect:../game";
	}

	@GetMapping("game/reset")
	public String resetGame(Model model) {
		drawnCards = null;
		return "redirect:../game";
	}

	private String requestCards(int cardCount) {
		try {
			String url = "https://deckofcardsapi.com/api/deck/" + deck.getDeck_id() + "/draw/?count=" + cardCount;
			URL objU = new URL(url);
			HttpURLConnection con = (HttpURLConnection) objU.openConnection();
			int responseCode = con.getResponseCode();
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

			return in.readLine();
		} catch (Exception e) {
			return "";
		}
	}

	private String shuffle() {
		try {
			String url = "https://deckofcardsapi.com/api/deck/new/shuffle/?deck_count=1";
			URL objU = new URL(url);
			HttpURLConnection con = (HttpURLConnection) objU.openConnection();
			con.setRequestMethod("GET");
			int responseCode = con.getResponseCode();
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

			return in.readLine();
		} catch (Exception e) {
			return "";
		}
	}

	private void drawCard(int count) {
		String json = requestCards(count);

		JSONObject deckObj = new JSONObject(json);
		JSONArray cardsArray = deckObj.getJSONArray("cards");

		ArrayList<JSONObject> cards = new ArrayList<>();
		for (int i = 0; i < cardsArray.length(); i++) {
			cards.add((JSONObject) cardsArray.get(i));
		}

		for (JSONObject card : cards) {
			Playingcard playingcard = new Playingcard();

			playingcard.setImage(card.getString("image"));
			playingcard.setCode(card.getString("code"));
			playingcard.setSuit(card.getString("suit"));
			playingcard.setValue(card.getString("value"));

			drawnCards.add(playingcard);
		}
	}
}