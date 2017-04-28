package id.co.babe.analysis;

import id.co.babe.analysis.nlp.CneDetector;
import id.co.babe.analysis.nlp.TextParser;

import java.util.Map;

import com.flakks.spelling.service.SpellApp;

public class CneAPI {
	public static String id_word = "nlp_data/indo_dict/id_full.txt";
	public static String stop_word = "nlp_data/indo_dict/stop_word.txt";
	public static String tag_word = "nlp_data/indo_dict/stop_word.txt";
	public static String redirect_word = "nlp_data/indo_dict/redirect_entity_map.txt";
	
	public static String sent_parser = "nlp-model/en-sent.bin";
	public static String token_parser = "nlp-model/en-token.bin";
	
	public static void initDict() {
		//CneDetector.init();
		SpellApp.initDict(id_word);
		SpellApp.initStop(stop_word);
		SpellApp.initTag(tag_word);
		SpellApp.initRedirect(redirect_word);
		//TextParser.init();
		TextParser.init(sent_parser, token_parser);
	}
	
	public static void initDict(String id_word, String stop_word, String tag_word, String redirect_word, String sent_parser, String token_parser) {
		//CneDetector.init();
		SpellApp.initDict(id_word);
		SpellApp.initStop(stop_word);
		SpellApp.initTag(tag_word);
		SpellApp.initRedirect(redirect_word);
		//TextParser.init();
		TextParser.init(sent_parser, token_parser);
	}
	
	public Map<String, Double> extractEntity(String text) {
		return CneDetector.genCandidate(text);
	}

}
