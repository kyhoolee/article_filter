package com.flakks.spelling.service;


import id.co.babe.analysis.util.TextfileIO;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;
import org.magnos.trie.Trie;
import org.magnos.trie.TrieMatch;
import org.magnos.trie.Tries;

import com.flakks.spelling.Dictionary;
import com.flakks.spelling.QueryMapper;
import com.flakks.spelling.SpecialRule;
import com.flakks.spelling.SpellingLookup;
import com.flakks.spelling.SpellingSuggestor;
import com.flakks.spelling.Suggestion;
import com.flakks.spelling.TrieNode;

public class SpellApp {
	public static Trie<String, Boolean> trieDict;
	public static Set<String> exactTag;
	
	
	public static Map<String, Dictionary> dictionaries;
	public static Map<String, TrieNode> trieNodes;
	public static Map<String, String> indoRootDict;
	
	public static Map<String, String> redirectDict;
	
	public static Map<String, Integer> indoDict;
	public static Set<String> indoStopDict;
	
	
	public static String getRedirect(String word) {
		if(redirectDict.containsKey(word.toLowerCase())) {
			return redirectDict.get(word.toLowerCase());
		}
		return word;
	}
	
	public static void initRedirect(String path) {
		redirectDict = new HashMap<String, String>();
		
		List<String[]> redirect = TextfileIO.readCsv(path);
		System.out.println("redirect size " + redirect.size());
		for(String[] r : redirect) {
			if(r.length == 2)
				redirectDict.put(r[0].toLowerCase(), r[1]);
		}
	}
	
	public static void printRedirect() {
		for(String key: redirectDict.keySet()) {
			System.out.println(key + " -- " + redirectDict.get(key));
		}
	}
	
	public static void initTag(String tagDict) {
		trieDict = Tries.forInsensitiveStrings(Boolean.FALSE);
		exactTag = new HashSet<String>();
		List<String> dictionary = TextfileIO.readFile(tagDict);
		for (String word : dictionary) {
			if(word!=null && word.trim().length() > 0)
				trieDict.put(word.trim().toLowerCase(), Boolean.TRUE);
		}

	}
	
	
	public static Set<String> suffixEntity(String word) {
		Set<String> keys = trieDict.keySet(word, TrieMatch.EXACT);
		return keys;
	}
	public static int countSuffix(String word) {
		Set<String> keys = trieDict.keySet(word, TrieMatch.EXACT);
		if(keys == null)
			return 0;
		return keys.size();
	}
	
	public static int countExact(String word) {
//		Set<String> keys = trieDict.keySet(word, TrieMatch.EXACT);
//		if(keys == null)
//			return 0;
//		return keys.size();
		
		if(exactTag.contains(word.toLowerCase()))
			return 1;
		else 
			return 0;
	}
	
	
	
	public static boolean checkStop(String word) {
		return indoStopDict.contains(word.toLowerCase());
	}
	
	public static void initStop(String indoStopPath) {
		indoStopDict = new HashSet<String>();
		List<String> lines = TextfileIO.readFile(indoStopPath);
		for(String line : lines) {
			try {
				indoStopDict.add(line.toLowerCase());
			} catch (Exception e) {
				
			}
		}
	}
	
	public static boolean checkCorrect(String word) {
		return indoDict.containsKey(word.toLowerCase());
	}
	
	public static void initDict(String indoDictPath) {
		indoDict = new HashMap<String, Integer>();
		
		List<String> lines = TextfileIO.readFile(indoDictPath);
		for(String line : lines) {
			try {
				String[] tokens = line.split(" ");
				indoDict.put(tokens[0], Integer.parseInt(tokens[1]));
			} catch (Exception e) {
				
			}
		}
	}
	

	public static void initIndo(String indo_dict_path, String root_indo_dict) {
		initIndo(indo_dict_path);
		initRootIndo(root_indo_dict);
	}
	public static void initIndo(String indo_dict_path) {
		List<String> lines = new ArrayList<String>();

		try {
			lines.addAll(Files.readAllLines(Paths.get(indo_dict_path),StandardCharsets.UTF_8));
		} catch (IOException e) {
			e.printStackTrace();
		}

		dictionaries = createIndoDictionary(lines);
		trieNodes = createTrieNodes(dictionaries);
	}
	
	public static void initRootIndo(String root_indo_dict) {
		indoRootDict = new HashMap<String, String>();
		
		List<String[]> lines = TextfileIO.readCsv(root_indo_dict);
		for(String[] line : lines) {
			if(line.length == 2) {
				if(line[0] != null && line[1] != null)
					indoRootDict.put(line[0], line[1]);
			}
		}
		
		
	}

	public static Map<String, Dictionary> createIndoDictionary(
			List<String> lines) {
		Map<String, Dictionary> dictionaries = new HashMap<String, Dictionary>();

		Dictionary dictionary;
		dictionary = new Dictionary();
		dictionaries.put("id", dictionary);

		for (String line : lines) {
			String[] columns = line.split(" ");
			dictionary.put(columns[0].trim().toLowerCase(),
					Integer.parseInt(columns[1]));
		}

		return dictionaries;
	}

	public static Map<String, Dictionary> createDictionaries(List<String> lines) {
		Map<String, Dictionary> dictionaries = new HashMap<String, Dictionary>();

		for (String line : lines) {
			String[] columns = line.split("\t");

			Dictionary dictionary = dictionaries.get(columns[0]);

			if (dictionary == null) {
				dictionary = new Dictionary();
				dictionaries.put(columns[0], dictionary);
			}

			dictionary.put(columns[1].trim().toLowerCase(),
					Integer.parseInt(columns[2]));
		}

		return dictionaries;
	}

	public static Map<String, TrieNode> createTrieNodes(
			Map<String, Dictionary> dictionaries) {
		Map<String, TrieNode> rootNodes = new HashMap<String, TrieNode>();

		for (Map.Entry<String, Dictionary> entry : dictionaries.entrySet()) {
			TrieNode rootNode = rootNodes.get(entry.getKey());

			if (rootNode == null) {
				rootNode = new TrieNode();
				rootNodes.put(entry.getKey(), rootNode);
			}

			for (Map.Entry<String, Integer> dictionaryEntry : entry.getValue()
					.entrySet()) {
				rootNode.insert(dictionaryEntry.getKey(),
						dictionaryEntry.getValue());
			}
		}

		return rootNodes;
	}
	
	public static void initAll(String[] files) {
		List<String> lines = new ArrayList<String>();

		for (int i = 0; i < files.length; i++) {
			try {
				lines.addAll(Files.readAllLines(Paths.get(files[i]),StandardCharsets.UTF_8));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		dictionaries = createDictionaries(lines);
		trieNodes = createTrieNodes(dictionaries);
	}

	
	public static String indoCorrect(String word) {
		String result = word;
		
		if(indoRootDict.containsKey(word)) {
			return indoRootDict.get(word);
		}
		
		return result;
	}
	
	public static String indoTrieCorrect(String word) {
		long time = System.currentTimeMillis();
		String locale = "id";
		String query = word.toLowerCase();
		
		query = SpecialRule.deduplicates(query);
		SpellingLookup spellingLookup = new SpellingLookup(locale);
		QueryMapper queryMapper = new QueryMapper(spellingLookup);
			
		String mappedQuery = queryMapper.map(query);
			
		time = System.currentTimeMillis() - time;
		
		//System.out.println(mappedQuery + " " + time + " " + spellingLookup.getSumDistance());
		
		return mappedQuery;
	}
	
	public static JSONObject indoSuggest(String word) {
		long time = System.currentTimeMillis();
		String locale = "id";
		String query = word;
		
		List<Suggestion> suggestions = new SpellingSuggestor(locale).suggest(query);
		JSONArray jsonSuggestions = new JSONArray();

		for(Suggestion suggestion : suggestions)
			jsonSuggestions.put(new JSONObject().put("query", suggestion.getToken()).put("frequency", suggestion.getFrequency()));
	
		time = System.currentTimeMillis() - time;
					
		JSONObject json = new JSONObject();
		json.put("suggestions", jsonSuggestions);
		json.put("took", time);
		
		return json;
	}

	public static void main(String[] args) throws Exception {
		String indo_dict_path = "nlp_data/indo_dict/id_full.txt";
		initIndo(indo_dict_path);

		new HttpServer().start();
	}
}