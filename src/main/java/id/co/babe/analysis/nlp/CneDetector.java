package id.co.babe.analysis.nlp;

import id.co.babe.analysis.model.Entity;
import id.co.babe.analysis.util.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.flakks.spelling.service.SpellApp;

import edu.stanford.nlp.util.Characters;

public class CneDetector {

	public static String[] web_suffix = { ".com", ".co", ".id", ".net",
			".co.id" };

	public static String[] month = { "januari", "februari", "pebruari","february",
			"maret", "april", "mei", "juni", "juli", "agustus", "september",
			"oktober", "november", "desember" };

	public static final Set<String> set_month = new HashSet<String>(
			Arrays.asList(month));

	public static String[] week_day_time = { "wib","senin", "sabtu", "jumat", "selasa",
			"rabu", "kamis", "minggu" };

	public static final Set<String> set_day = new HashSet<String>(
			Arrays.asList(week_day_time));

	public static String[] currency = { "rp", "usd" };

	public static void init() {
		// SpellApp.initIndo("nlp_data/indo_dict/id_full.txt");
		SpellApp.initDict("nlp_data/indo_dict/id_full.txt");
		SpellApp.initStop("nlp_data/indo_dict/stop_word.txt");
		SpellApp.initTag("nlp_data/indo_dict/wiki_tag.txt");
		SpellApp.initRedirect("nlp_data/indo_dict/redirect_entity_map.txt");
		TextParser.init();
	}

	public static void init(String dict, String sentBin, String tokenBin) {
		SpellApp.initIndo(dict);
		TextParser.init(sentBin, tokenBin);
	}

	public static Map<String, Double> processFreq(String text) {
		Map<String, Double> result = new HashMap<String, Double>();

		List<List<String>> p = parse(text);

		for (List<String> s : p) {
			for (String w : s) {
				if (result.containsKey(w)) {
					result.put(w, result.get(w) + 1);
				} else {
					result.put(w, 1.0);
				}
			}
		}

		return result;
	}

	public static long docLen(String text) {
		List<List<String>> p = parse(text);
		long len = 0;
		for (List<String> s : p) {
			len += s.size();
		}

		return len;
	}

	public static String preProcess(String text) {
		String result = text.replace("/", " , ").replace(",", " , ")
				.replace("\"", "  ").replace("(", " , ").replace(")", " , ")
				.replace("\"", " , ").replace("“", " , ").replace("”", " , ")
				.replace("”", " , ").replace("‘", " , ").replace("’", " , ");

		result = removePunctuation(result);

		return result;
	}

	public static List<List<String>> parse(String text) {

		List<List<String>> result = sentWordParse(preProcess(text));
		result = sentPhraseParse(result);
		result = removeStopPhrase(result);

		return result;
	}

	public static String removePunctuation(String sent) {
		String result = sent;

		if (result.length() > 0) {
			Character c = result.charAt(result.length() - 1);
			if (Characters.isPunctuation(c)) {
				result = result.substring(0, result.length() - 1);
			}
		}

		return result;
	}

	public static List<List<String>> sentWordParse(String text) {
		List<List<String>> sentWord = new ArrayList<List<String>>();

		String[] sents = TextParser.sentenize(text);
		for (int i = 0; i < sents.length; i++) {
			sents[i] = removePunctuation(sents[i]);
			//System.out.println(sents[i]);

			String[] word = TextParser.tokenize(sents[i]);
			List<String> sent = new ArrayList<String>(Arrays.asList(word));

			sentWord.add(sent);
		}

		return sentWord;
	}

	public static List<List<String>> sentPhraseParse(List<List<String>> sW) {
		List<List<String>> sentPhrase = new ArrayList<List<String>>();

		for (List<String> s : sW) {
			List<String> p = phraseParse(s);
			sentPhrase.add(p);
		}

		return sentPhrase;
	}

	public static List<List<String>> removeStopPhrase(List<List<String>> sP) {
		List<List<String>> sentPhrase = new ArrayList<List<String>>();

		for (List<String> s : sP) {
			List<String> p = removeStop(s);
			sentPhrase.add(p);
		}

		return sentPhrase;
	}

	public static List<String> removeStop(List<String> phrases) {
		List<String> result = new ArrayList<String>();

		for (String p : phrases) {
			if (checkLetterNumeric(p) && !checkStop(p)) {
				result.add(p);
			}
		}

		return result;
	}

	public static List<String> phraseParse(List<String> words) {
		List<String> phrases = new ArrayList<String>();

		phrases = capPhraseParse(words);

		return phrases;
	}

	public static List<String> capPhraseParse(List<String> word) {
		List<String> result = new ArrayList<String>();

		int j = 0;

		while (j < word.size()) {

			if (j == 0) {
				String start = word.get(j);
				if (!checkCorrect(start) && checkCapital(start)) {
					int next = j + 1;
					String candidate = start;
					while (next < word.size()
							&& checkCapitalorNumeric(word.get(next))) {

						candidate += " " + word.get(next);
						next++;
					}

					j = next;
					if (candidateFilter(candidate)) {
						//System.out.println(j + " " + next + " : " + candidate);
						result.add(postProcess(candidate));

					}
				} else {
					result.add(word.get(j));
					j++;
				}
			} else {
				String start = word.get(j);
				if (checkCapital(start)) {
					int next = j + 1;
					String candidate = start;
					while (next < word.size()
							&& checkCapitalorNumeric(word.get(next))) {

						candidate += " " + word.get(next);
						next++;
					}

					j = next;
					if (candidateFilter(candidate)) {
						//System.out.println(j + " " + next + " : " + candidate);
						result.add(postProcess(candidate));

					}
				} else {
					result.add(word.get(j));
					j++;
				}
			}

		}

		return result;
	}

	public static boolean candidateFilter(String w) {
		String phrase = w.toLowerCase();
		boolean result = 
				phrase.length() > 1 && !SpellApp.checkStop(phrase)
				&& !checkDatePhrase(phrase) 
				&& !checkMoneyPhrase(phrase)
				&& !checkWebPhrase(phrase)
				&& !checkRomanNumeral(w);

		return result;
	}

	public static Set<String> processCapitalized(String text) {
		Set<String> result = new HashSet<String>();

		String[] sents = TextParser.sentenize(text);

		for (int i = 0; i < sents.length; i++) {
			//System.out.println(sents[i]);
			sents[i] = preProcess(sents[i]);

			String[] word = TextParser.tokenize(sents[i]);

			int j = 0;

			while (j < word.length) {

				if (j == 0) {
					String start = word[j];
					if (!checkCorrect(start) && checkCapital(start)) {
						int next = j + 1;
						String candidate = start;
						while (next < word.length
								&& checkCapitalorNumeric(word[next])) {

							candidate += " " + word[next];
							next++;
						}

						// System.out.println(j + " " + next + " : " +
						// candidate);
						j = next;
						if (candidateFilter(candidate)) {
							result.add(postProcess(candidate));
						}
					} else {
						j++;
					}
				} else {
					String start = word[j];
					if (checkCapital(start)) {
						int next = j + 1;
						String candidate = start;
						while (next < word.length
								&& checkCapitalorNumeric(word[next])) {

							candidate += " " + word[next];
							next++;
						}
						// System.out.println(j + " " + next + " : " +
						// candidate);
						j = next;
						if (candidateFilter(candidate)) {
							result.add(postProcess(candidate));
						}
					} else {
						j++;
					}
				}

			}

		}

		//Utils.printArray(result);

		return result;
	}

	public static Set<String> genComb(String word) {
		Set<String> result = new HashSet<String>();

		String[] tokens = word.split(" ");
		for (int i = 0; i < tokens.length; i++) {
			for (int start = 0; start < tokens.length - i; start++) {
				String c = tokens[start];
				for (int j = 1; j <= i; j++) {
					c += " " + tokens[start + j];
				}

				if (candidateFilter(c)) {
					result.add(c);
					//System.out.println(c);
				}
			}
		}

		return result;
	}

	public static Map<String, Integer> processCombination(Set<String> ws) {
		Map<String, Integer> r = new HashMap<String, Integer>();

		for (String w : ws) {
			Set<String> comb = genComb(w);
			for (String c : comb) {
				if (r.containsKey(c)) {
					r.put(c, r.get(c) + 1);
				} else {
					r.put(c, 1);
				}
			}
		}

		return r;
	}

	public static Map<String, Set<String>> searchTaggedEntity(Set<String> ws) {
		Map<String, Set<String>> r = new HashMap<String, Set<String>>();

		for (String w : ws) {
			Set<String> tagged = SpellApp.suffixEntity(w);
			if (tagged.size() > 0)
				r.put(w, tagged);
		}

		return r;
	}

	public static Map<String, Integer> countTaggedEntity(Set<String> ws) {
		Map<String, Integer> r = new HashMap<String, Integer>();

		for (String w : ws) {
			int tagged = SpellApp.countSuffix(w);
			if (tagged > 0)
				r.put(w, tagged);
		}

		return r;
	}

	public static Set<String> filterShort(Set<String> candidate) {
		Set<String> r = new HashSet<String>();

		Set<String> filtered = new HashSet<String>();
		for (String c : candidate) {
			int eC = SpellApp.countExact(c);
			if (SpellApp.countExact(c) > 0) {
				//System.out.println(c + " --- " + eC);
				filtered.add(c);
				r.add(c);
			} else {
				int tagged = SpellApp.countSuffix(c);
				if (tagged > 0 && tagged < 100) {
					//System.out.println(c + " --- " + tagged);
					filtered.add(c);
				}

			}
		}

		for (String c : filtered) {

			boolean check = true;
			for (String p : filtered) {

				if (p.contains(c) && p.length() > c.length()) {
					check = false;
					break;
				}
			}

			if (check) {
				r.add(c);
				
			}

		}

		return r;
	}

	public static double countFreq(String can, String text) {
		int lastIndex = 0;
		int count = 0;

		while (lastIndex != -1) {
			lastIndex = text.indexOf(can, lastIndex);
			if (lastIndex != -1) {
				count++;
				lastIndex += can.length();
			}
		}

		return count;
	}

	public static double countDecay(String can, String text) {
		int lastIndex = 0;
		double count = 0;

		while (lastIndex != -1) {
			lastIndex = text.indexOf(can, lastIndex);
			if (lastIndex != -1) {
				count += Math.exp((text.length() - lastIndex) * 0.5
						/ text.length());
				lastIndex += can.length();
			}
		}

		return count;
	}

	public static Map<String, Double> countCan(Set<String> longCan, String text) {
		Map<String, Double> result = new HashMap<String, Double>();
		for (String can : longCan) {
			result.put(can, countDecay(can, text));
		}
		return result;
	}

	public static Map<String, Double> groupCan(Set<String> canSet,
			Map<String, Double> countSet) {
		Map<String, Double> result = new HashMap<String, Double>();
		for (String can : canSet) {
			double c = 0;
			for (String w : countSet.keySet()) {
				if (can.contains(w)) {
					if (can.length() == w.length())
						c += countSet.get(w) * 0.8 * (can.split(" ").length);
					else
						c += countSet.get(w) * 0.2 / (can.split(" ").length);
				}
			}
			result.put(can, c);
		}
		return result;
	}
	
	public static Map<String, Double> redirectCan(Map<String, Double> input) {
		Map<String, Double> r = new HashMap<String, Double>();
		
		for(String key : input.keySet()) {
			String root = SpellApp.getRedirect(key);
			
			if(r.containsKey(root)) {
				r.put(root, r.get(root) + input.get(key));
			} else {
				r.put(root, input.get(key));
			}
			
			System.out.println(key + " -- " + root + " -- " + r.get(root));
		}
		
		return r;
	}
	
	public static Map<String, Double> redirectCandidate(Map<String, Double> input) {
		Map<String, Double> r = new HashMap<String, Double>();
		
		Map<String, Set<String>> redirect = new HashMap<String, Set<String>>();
		for(String key : input.keySet()) {
			String root = SpellApp.getRedirect(key);
			
			if(r.containsKey(root)) {
				Set<String> val = redirect.get(root);
				val.add(key);
				redirect.put(root, val);
			} else {
				Set<String> val = new HashSet<String>();
				val.add(key);
				redirect.put(root, val);
			}
			
		}
		
		Map<String, Double> reScore = new HashMap<String, Double>();
		for(String key : redirect.keySet()) {
			double score = 0;
			for(String can : redirect.get(key)) {
				score += input.get(can);
			}
			
			reScore.put(key, score);
		}
		
		Map<String, String> rootCan = new HashMap<String, String>();
		for(String key: redirect.keySet()) {
			int len = 0;
			String can = "";
			for(String c : redirect.get(key)) {
				if(c.length() > len) {
					len = c.length();
					can = c;
				}
			}
			rootCan.put(key, can);
		}
		
		
		for(String key: redirect.keySet()) {
			r.put(rootCan.get(key), reScore.get(key));
		}

		
		return r;
	}
	
	
	public static Map<String, Double> unmatchCandidate(String text) {
		Map<String, Double> r = new HashMap<String, Double>();
		
		Set<String> capCan = processCapitalized(text);
		Map<String, Integer> combCan = processCombination(capCan);
		
		for(String key: combCan.keySet()) {
			if(combCan.get(key) > 0) {
				r.put(key, combCan.get(key) * 1.0);
			}
		}
		
		return r;
	}
	
	public static List<String> genUnmatchCan(String text) {
		Map<String, Double> canScore = unmatchCandidate(text);
		
		List<String> r = new ArrayList<String>();
		
		for(String key: canScore.keySet()) {
			r.add(key + " -- " + canScore.get(key));
		}
		
		return r;
	}
	
	public static List<Entity> getEntity(String text) {
		List<Entity> result = new ArrayList<Entity>();
		
		Map<String, Double> cans = genCandidate(text);
		
		for(String c : cans.keySet()) {
			Entity e = new Entity(c, cans.get(c), Entity.type_unknow);
			result.add(e);
		}
		
		return result;
	}
	
	public static List<Entity> getEntity(Map<String, Double> cans) {
		List<Entity> result = new ArrayList<Entity>();
		
		for(String c : cans.keySet()) {
			Entity e = new Entity(c, cans.get(c), Entity.type_unknow);
			result.add(e);
		}
		
		return result;
	}
	
	public static List<Entity> getEntity(Map<String, Integer> cans, Set<String> matched) {
		List<Entity> result = new ArrayList<Entity>();
		
		for(String c : cans.keySet()) {
			if(!matched.contains(c)) {
				Entity e = new Entity(c, cans.get(c), Entity.type_unknow);
				result.add(e);
			}
		}
		
		return result;
	}
	
	
	public static Map<String, List<Entity>> genGroupCan(String text) {
		Map<String, List<Entity>> result = new HashMap<String, List<Entity>>();
		
		
		Map<String, Double> matched = new HashMap<String, Double>();

		Set<String> capCan = processCapitalized(text);
		Map<String, Integer> combCan = processCombination(capCan);
		Set<String> longCan = filterShort(combCan.keySet());
		

		Map<String, Double> r = countCan(combCan.keySet(), text);
		Map<String, Double> res = groupCan(longCan, r);
		
		matched = redirectCandidate(res);
		matched = Utils.MapUtil.sortByValue(matched);
		
		List<Entity> matchedEntity = getEntity(matched);
		List<Entity> unmatchedEntity = getEntity(combCan, matched.keySet());
		
		result.put("matched", matchedEntity);
		result.put("unmatched", unmatchedEntity);

		return result;
	}
	
	public static void printResult(Map<String, List<Entity>> r) {
		for(String key : r.keySet()) {
			System.out.println(key);
			printEntity(r.get(key));
			System.out.println("\n\n");
		}
	}
	
	public static void printMap(Map<?, ?> map) {
		for(Object k : map.keySet()) {
			System.out.println(k + " : " + map.get(k));
		}
	}
	
	public static void printEntity(List<Entity> entity) {
		for(Entity e: entity) {
			printEntity(e);
		}
	}
	
	public static void printEntity(Entity e) {
		System.out.println(e.name + " : " + e.occFreq);
	}
	

	public static Map<String, Double> genCandidate(String text) {
		Map<String, Double> result = new HashMap<String, Double>();

		Set<String> capCan = processCapitalized(text);
		Map<String, Integer> combCan = processCombination(capCan);

		// Map<String, Integer> tagCan = countTaggedEntity(combCan.keySet());

		Set<String> longCan = filterShort(combCan.keySet());
		
		System.out.println("---------");
		Utils.printCollection(combCan.keySet());
		System.out.println("---------");
		System.out.println("---------");
		Utils.printCollection(longCan);
		System.out.println("---------");

		Map<String, Double> r = countCan(combCan.keySet(), text);
		
		Map<String, Double> res = groupCan(longCan, r);
		result = redirectCandidate(res);

		result = Utils.MapUtil.sortByValue(result);
		int count = 0;
		System.out.println("---------");
		for (String can : result.keySet()) {
			System.out.println(can + " -- " + result.get(can));
			++count;

			if (count == 5) {
				System.out.println();
			}
//			if (count > 10) {
//				break;
//			}
		}
		System.out.println("---------");

		return result;
	}
	
	public static List<String> genCanScore(String text) {
		Map<String, Double> canScore = genCandidate(text);
		
		List<String> r = new ArrayList<String>();
		
		for(String key: canScore.keySet()) {
			r.add(key + " -- " + canScore.get(key));
		}
		
		return r;
	}

	public static String postProcess(String can) {
		String[] ws = can.split(" ");
		String result = "";
		for (int i = 0; i < ws.length; i++) {
			if (!checkWeb(ws[i])) {
				result += ws[i] + " ";
			}
		}

		if (result.length() > 0) {
			result = result.substring(0, result.length() - 1);
		}

		return result;
	}
	
	
	public static boolean checkRomanNumeral(String input) {
		Pattern pattern = Pattern.compile("(M|m){0,4}(CM|CD|D?C{0,3})(XC|XL|L?X{0,3})(IX|IV|V?(I|i){0,3})");
		Matcher matcher = pattern.matcher(input);
		while (matcher.find()) {
			String str = matcher.group();
			//System.out.println(str);
			if(str.equals(input)) {
				return true;
			}
		}
		return false;
	}

	public static boolean checkSpecialWord(String w) {
		boolean result = false;

		result = checkDate(w) || checkMoney(w) || checkWeb(w);

		return result;
	}

	public static boolean checkStopPhrase(String phrase) {
		boolean result = false;

		result = checkDatePhrase(phrase) || checkWebPhrase(phrase)
				|| checkMoneyPhrase(phrase);

		return result;
	}

	public static boolean checkDatePhrase(String w) {
		String word = w.toLowerCase();
		String[] ws = word.split(" ");
		for (int i = 0; i < ws.length; i++) {
			if (!checkDate(ws[i]))
				return false;
		}

		return true;
	}

	public static boolean checkDate(String w) {
		boolean result = false;
		String word = w.toLowerCase();
		if (set_day.contains(word) || set_month.contains(word)
				|| checkNumber(word)) {
			return true;
		}

		return result;
	}

	public static boolean checkMoneyPhrase(String w) {
		String word = w.toLowerCase();
		String[] ws = word.split("\\s+");
		for (int i = 0; i < ws.length; i++) {
			if (!checkMoney(ws[i]))
				return false;
		}

		return true;
	}

	public static boolean checkMoney(String w) {
		boolean result = false;
		String word = w.toLowerCase();
		for (int i = 0; i < currency.length; i++) {
			String prefix = currency[i];
			if (word.startsWith(prefix))
				return true;
		}

		if (checkNumber(word))
			return true;

		return result;
	}

	public static boolean checkWebPhrase(String w) {
		String word = w.toLowerCase();
		String[] ws = word.split("\\s+");
		for (int i = 0; i < ws.length; i++) {
			if (!checkWeb(ws[i]))
				return false;
		}

		return true;
	}

	public static boolean checkWeb(String w) {
		boolean result = false;
		String word = w.toLowerCase();
		for (int i = 0; i < web_suffix.length; i++) {
			String suffix = web_suffix[i];
			if (word.endsWith(suffix))
				return true;
		}

		return result;
	}

	public static boolean checkAllCapitalized(String word) {
		boolean result = true;

		for (int i = 0; i < word.length(); i++) {
			boolean c = Character.isUpperCase(word.charAt(0));
			if (!c) {
				return false;
			}
		}

		return result;
	}

	public static boolean checkStop(String word) {
		boolean result = !checkAllCapitalized(word)
				&& (SpellApp.checkStop(word) || !checkLetterNumeric(word) || (word
						.length() < 2));

		return result;
	}

	public static boolean checkCorrect(String word) {
		return SpellApp.checkCorrect(word);
	}

	public static boolean checkCapital(String word) {
		//boolean result = false;

		if (word != null && word.length() > 0) {
			for(int i = 0 ; i < word.length() ; i ++) {
				if(Character.isUpperCase(word.charAt(i)))
					return true;
			}
		}
		return false;
	}

	public static boolean checkNumeric(String word) {
		boolean result = false;

		if (word != null && word.length() > 0) {
			return Character.isDigit(word.charAt(0));
		}
		return result;
	}

	public static boolean checkNumber(String word) {
		boolean result = true;

		if (word == null || word.length() == 0)
			return false;
		for (int i = 0; i < word.length(); i++) {
			if (!Character.isDigit(word.charAt(0)))
				return false;
		}
		return result;
	}

	public static boolean checkCapitalorNumeric(String word) {
		boolean result = false;

		if (word != null && word.length() > 0) {
			return Character.isUpperCase(word.charAt(0))
					|| Character.isDigit(word.charAt(0));
		}
		return result;
	}

	public static boolean checkLetterNumeric(String word) {
		boolean result = false;

		if (word != null && word.length() > 0) {
			return Character.isLetter(word.charAt(0))
					|| Character.isDigit(word.charAt(0));
		}
		return result;
	}

	public static List<String> sentParse(String sent) {
		String[] word = TextParser.tokenize(sent);
		List<String> result = new ArrayList<String>(Arrays.asList(word));

		return result;
	}

	public static void main(String[] args) {
		//testGenComb();
		//System.out.println(checkRomanNumeral("Viiii"));
	}

	public static void testGenComb() {
		String word = "1 2 3";
		Set<String> r = genComb(word);
		System.out.println("---");
		Utils.printCollection(r);
		System.out.println("---");
	}

	public static void sample() {
		init();

		String data = "Indonesia torehkan namanya di panggung dunia lewat rancangan indah yang membalut sang Puteri Indonesia dalam ajang kecantikan dunia ";
		List<String> w = sentParse(data);
		List<String> p = capPhraseParse(w);
		Utils.printArray(p);
	}

}
