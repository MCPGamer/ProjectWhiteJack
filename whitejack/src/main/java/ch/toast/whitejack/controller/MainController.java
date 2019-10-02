package ch.toast.whitejack.controller;

import java.util.ArrayList;
import java.util.Arrays;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;

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
			RestTemplate rt = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
			headers.add("user-agent",
					"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36");
			HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
			String url = "https://deckofcardsapi.com/api/deck/" + deck.getDeck_id() + "/draw/?count=" + cardCount;
			ResponseEntity<String> res = rt.exchange(url, HttpMethod.GET, entity, String.class);

			return res.getBody();
		} catch (Exception e) {
			return "";
		}
	}

	private String shuffle() {
		try {
			RestTemplate rt = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
			headers.add("user-agent",
					"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36");
			HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
			String url = "https://deckofcardsapi.com/api/deck/new/shuffle/?deck_count=1";
			ResponseEntity<String> res = rt.exchange(url, HttpMethod.GET, entity, String.class);

			return res.getBody();
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