package com.flakks.spelling.lucene;

import id.co.babe.analysis.util.TextfileIO;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.spell.Dictionary;
import org.apache.lucene.search.spell.LuceneDictionary;
import org.apache.lucene.search.suggest.Lookup;
import org.apache.lucene.search.suggest.analyzing.AnalyzingInfixSuggester;
import org.apache.lucene.search.suggest.analyzing.AnalyzingSuggester;
import org.apache.lucene.search.suggest.analyzing.FuzzySuggester;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

public class LuceneSpell {
	private static final String TAG_FIELD = "content";
	private  static Directory luceneDirectory = null;
	
	
	private static AnalyzingInfixSuggester analyzingInfixSuggester = null;
	private static AnalyzingSuggester analyzingSuffixSuggester = null;
	private static FuzzySuggester fuzzySuggester = null;
	
	
	public static void init() {
		try {
			init("nlp_data/indo_dict/wiki_tag.txt");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void init(String path) throws IOException {
		List<String> data = TextfileIO.readFile(path);
		
		StandardAnalyzer analyzer = new StandardAnalyzer();
		luceneDirectory = new RAMDirectory();
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        IndexWriter indexWriter = new IndexWriter(luceneDirectory, config);

        
        for(String word: data) {
	        Document doc = new Document();
	        doc.add(new StringField(TAG_FIELD, word, Field.Store.YES));
	        indexWriter.addDocument(doc);
        }
        
        indexWriter.commit();
        indexWriter.close();
        IndexReader indexReader = DirectoryReader.open(luceneDirectory);
        Dictionary dictionary = new LuceneDictionary(indexReader, TAG_FIELD);

        analyzingInfixSuggester = new AnalyzingInfixSuggester(Version.LATEST, luceneDirectory, analyzer);
        analyzingInfixSuggester.build(dictionary);
        
        
        analyzingSuffixSuggester = new AnalyzingSuggester(new StandardAnalyzer());
        analyzingSuffixSuggester.build(dictionary);
        
        
        fuzzySuggester = new FuzzySuggester(new StandardAnalyzer());
        fuzzySuggester.build(dictionary);
	}
	
	public static boolean checkInfix(String w) {
		if(countInfix(w) > 0)
			return true;
		
		return false;
	}
	
	public static boolean checkPrefix(String w) {
		if(countPrefix(w) > 0)
			return true;
		
		return false;
	}
	
	public static boolean checkFuzzy(String w) {
		if(countFuzzy(w) > 0)
			return true;
		
		return false;
	}
	
	
	public static int countInfix(String w) {
		try {
			Map<String, Double> infix = getInfix(w);
			return infix.size();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	public static int countPrefix(String w) {
		try {
			Map<String, Double> prefix = getPrefix(w);
			return prefix.size();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	
	public static int countFuzzy(String w) {
		try {
			Map<String, Double> fuzzy = getFuzzy(w);
			return fuzzy.size();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	//Set<String>
	public static Map<String, Double> getInfix(String w) throws IOException {
		Map<String, Double> r = new HashMap<String, Double>();
        List<Lookup.LookupResult> lookupResultList = analyzingInfixSuggester.lookup(w, false, 10);
        for (Lookup.LookupResult lookupResult : lookupResultList) {
            System.out.println(lookupResult.key + ": " + lookupResult.value);
            r.put(lookupResult.key.toString(), new Double(lookupResult.value));
        }
		return r;
	}
	
	
	public static Map<String, Double> getPrefix(String w) throws IOException {
		Map<String, Double> r = new HashMap<String, Double>();
		List<Lookup.LookupResult> lookupResultList = analyzingSuffixSuggester.lookup(w, false, 10);
        for (Lookup.LookupResult lookupResult : lookupResultList) {
            System.out.println(lookupResult.key + ": " + lookupResult.value);
            r.put(lookupResult.key.toString(), new Double(lookupResult.value));
        }
		return r;
	}
	
	
	public static Map<String, Double> getFuzzy(String w) throws IOException {
		Map<String, Double> r = new HashMap<String, Double>();
		List<Lookup.LookupResult> lookupResultList = fuzzySuggester.lookup(w, false, 10);
        for (Lookup.LookupResult lookupResult : lookupResultList) {
            System.out.println(lookupResult.key + ": " + lookupResult.value);
            r.put(lookupResult.key.toString(), new Double(lookupResult.value));
        }
		return r;
	}

}
