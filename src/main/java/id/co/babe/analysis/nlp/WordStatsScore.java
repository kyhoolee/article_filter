package id.co.babe.analysis.nlp;

import java.util.Map;
import java.util.Set;

public class WordStatsScore {
	public static double const_k1 = 1.8; // [1.2 , 2.0]
	public static double const_b = 0.75;
	
	public static double average_doclen = 1;
	public static Set<Long> articleIdSet;
	public static Map<String, Double> wordIdf;
	public static Map<String, Double> wordDocFreq;
	public static Map<String, Double> docLen;
	
	
	public static Map<String, Double> wordDocBm25;

}
