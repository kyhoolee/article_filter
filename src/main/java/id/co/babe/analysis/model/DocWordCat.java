package id.co.babe.analysis.model;

import id.co.babe.analysis.util.Utils;

import java.util.HashMap;
import java.util.Map;

public class DocWordCat {
	public long docId;
	public int catId;
	public String catName;
	
	public Map<String, Map<Integer, Double>> wordCat;
	
	
	
	public DocWordCat(long docId, int catId, String catName) {
		this.docId = docId;
		this.catId = catId;
		this.catName = catName;
		wordCat = new HashMap<String, Map<Integer, Double>>();
	}
	
	public void addWordCat(String word, Map<Integer, Double> catCount) {
		wordCat.put(word, catCount);
	}
	
	public int sortWordCat(Map<Integer, Double> wC, int catId) {
		Map<Integer, Double> testMap = Utils.MapUtil.sortByValue( wC );
		int count = 0;
		int result = 0;
        for(Map.Entry<Integer, Double> entry : testMap.entrySet()) {
        	count ++;
        	System.out.println(catId + " -- " + entry.getKey() + " -- " + entry.getValue());
        	if(entry.getKey().intValue() == catId) {
        		result = count;
        		break;
        	}
        }
        
        return result;
	}
	
	public Map<String, Integer> wordCatRank() {
		Map<String, Integer> catRank = new HashMap<String, Integer>();
		
		for(String word : wordCat.keySet()) {
			Map<Integer, Double> wC = wordCat.get(word);
			
			System.out.println("\n\n" + word);
			int rank = sortWordCat(wC, catId);
			catRank.put(word, rank);
		}
		
		
		return catRank;
	}
	
	
	public static void main(String[] args) {
		DocWordCat dwc = new DocWordCat(1, 1, "");
		
	}

}
