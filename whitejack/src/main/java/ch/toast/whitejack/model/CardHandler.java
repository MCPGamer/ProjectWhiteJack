package ch.toast.whitejack.model;

import java.util.ArrayList;
import java.util.Arrays;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class CardHandler {
	public String requestCards(Deck deck, int cardCount) {
		try {
			String url = "https://deckofcardsapi.com/api/deck/" + deck.getDeck_id() + "/draw/?count=" + cardCount;
			String result = request(url);
			return result != null ? result : "";
		} catch (Exception e) {
			return "";
		}
	}

	public String shuffle() {
		try {
			String url = "https://deckofcardsapi.com/api/deck/new/shuffle/?deck_count=1";
			String result = request(url);
			return result != null ? result : "";
		} catch (Exception e) {
			return "";
		}
	}

	public void drawCard(Deck deck, ArrayList<Playingcard> drawnCards, int count) {
		String json = requestCards(deck, count);

		JSONObject deckObj = new JSONObject(json);
		JSONArray cardsArray = deckObj.getJSONArray("cards");

		ArrayList<JSONObject> cards = new ArrayList<>();
		for (int i = 0; i < cardsArray.length(); i++) {
			cards.add((JSONObject) cardsArray.get(i));
		}

		for (JSONObject card : cards) {
			Playingcard playingcard = new Playingcard();

			String image = card.getString("image");
			while(image.contains("/")) {
				image = image.substring(image.indexOf("/") + 1);
			}
			
			playingcard.setImage("/images/" + image);
			playingcard.setCode(card.getString("code"));
			playingcard.setSuit(card.getString("suit"));
			playingcard.setValue(card.getString("value"));

			drawnCards.add(playingcard);
		}
	}

	public Playingcard drawCard(Deck deck, boolean isCoverUp) {
		String json = requestCards(deck, 1);

		JSONObject deckObj = new JSONObject(json);
		JSONArray cardsArray = deckObj.getJSONArray("cards");

		ArrayList<JSONObject> cards = new ArrayList<>();
		for (int i = 0; i < cardsArray.length(); i++) {
			cards.add((JSONObject) cardsArray.get(i));
		}

		Playingcard playingcard = new Playingcard();
		for (JSONObject card : cards) {

			playingcard.setImage(card.getString("image"));
			playingcard.setCode(card.getString("code"));
			playingcard.setSuit(card.getString("suit"));
			playingcard.setValue(card.getString("value"));

		}
		return playingcard;
	}

	public String request(String url) {
		RestTemplate rt = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();

		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		headers.add("user-agent",
				"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36");

		HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
		ResponseEntity<String> res = rt.exchange(url, HttpMethod.GET, entity, String.class);

		return res.getBody();
	}
}
