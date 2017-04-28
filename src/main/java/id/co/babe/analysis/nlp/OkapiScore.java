package id.co.babe.analysis.nlp;

import java.util.HashMap;
import java.util.Map;

public class OkapiScore {
	public static final double const_k = 1.8;
	public static final double const_b = 0.75;
	
	
	
	
	public String docId;
	public long docLen;
	public long totalDoc;
	public double averageDocLen = 132.343;
	public Map<String, Long> wordArticle;
	public Map<String, Double> wordFreq;
	
	public Map<String, Double> wordOkapi;
	
	public OkapiScore(String docId, long docLen, Map<String, Long> wordArticle, Map<String, Double> wordFreq) {
		this.docId = docId;
		this.docLen = docLen;
		this.wordArticle = wordArticle;
		this.wordFreq = wordFreq;
		
		this.wordOkapi = new HashMap<String, Double>();
	}
	
	
	public Map<String, Double> idf() {
		Map<String, Double> res = new HashMap<String, Double>();
		
		for(String w : wordArticle.keySet()) {
			double idf = Math.log(
					(docLen - wordArticle.get(w) + 0.5)
					/ (double)(wordArticle.get(w) + 0.5) 
					);
			res.put(w, idf);
		}
		
		return res;
	}
	
	public void calculateOkapi() {
		Map<String, Double> idf = idf();
		for(String w : idf.keySet()) {
			double f = wordFreq.get(w);
			double factor = f * (const_k + 1) 
					/ (f + const_k * (1 - const_b + const_b * (totalDoc / averageDocLen)));  
			double score = idf.get(w) * factor;
			
			wordOkapi.put(w, score);
		}
	}
	
 
}
