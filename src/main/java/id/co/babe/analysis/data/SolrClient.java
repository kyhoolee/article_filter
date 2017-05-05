package id.co.babe.analysis.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.flakks.spelling.service.SpellApp;

import id.co.babe.analysis.model.Article;
import id.co.babe.analysis.model.Category;
import id.co.babe.analysis.model.DocWordCat;
import id.co.babe.analysis.model.Entity;
import id.co.babe.analysis.nlp.CneDetector;
import id.co.babe.analysis.nlp.TextParser;
import id.co.babe.analysis.util.HttpUtils;
import id.co.babe.analysis.util.TextfileIO;
import id.co.babe.analysis.util.Utils;

public class SolrClient {
	
	public static final long[] aIds = {
		10646013, 10646022, 10646021, 10646036, 10646039, 10646045, 10646047, 10646056, 10646054, 10646058,
		10646062, 10646069, 10646073, 10646074, 10646081, 10646083, 10646087, 10646095,
		10646094, 10646099, 10646114
		
	};
	
	public static final String url_entity = "http://10.2.15.89:9000/v1/entity/extract/";
	
	public static final String url_article = "http://10.2.15.5:8983/solr/article-repo/select?sort=created_ts_l+desc&wt=json&indent=true&q=type_i%3A0";
	
	public static List<Entity> getArticleEntity(String articleId) {
		String json = HttpUtils.postRequest(url_entity + articleId);
		JSONObject object = HttpUtils.jsonObject(json);
		
		JSONArray matches = object.getJSONArray("matches");
		
		List<Entity> result = new ArrayList<Entity>();
		for(int i = 0 ; i < matches.length() ; i ++) {
			JSONObject o = matches.getJSONObject(i);
			Entity e = new Entity(o.getString("name"), o.getInt("occFreq"), o.getInt("entityType"));
			result.add(e);
		}
		
		return result;
	}
	
	public static JSONArray getJSONArray(List<?> l) {
		JSONArray array = new JSONArray(l);
		return array;
	}
	
	public static List<String> getList(JSONArray array) {
		List<String> result = new ArrayList<String>();
		
		for(int i = 0 ; i < array.length() ; i ++) {
			result.add(array.getString(i));
		}
		
		return result;
	}
	
	public static List<Integer> getIntList(JSONArray array) {
		List<Integer> result = new ArrayList<Integer>();
		
		for(int i = 0 ; i < array.length() ; i ++) {
			result.add(array.getInt(i));
		}
		
		return result;
	}
	
	public static void printArticle(Article a) {
		System.out.println();
		System.out.println(a.articleId);
		System.out.println(a.title);
		System.out.println(a.content);
		System.out.println(a.url);
		System.out.println();
		Utils.printCollection(a.category);
		System.out.println();
		Utils.printCollection(a.catId);
		System.out.println();
	}
	
	public static String queryList(Collection<?> l) {
		String result = "";
		for(Object c : l) {
			result += c.toString() + "+";
		}
		if(result.length() > 0) {
			result = result.substring(0, result.length() - 1);
		}
		
		return result;
	}
	
	public static long countArticle(String word, int catId) {
		
		String json = HttpUtils.getRequest(url_article + "+AND+body_t%3A" + word.replace(" ", "+")  
				+ "+AND+category_is%3A" + catId
				+ "&start=" + 0 + "&rows=" + 1);
		JSONObject object = HttpUtils.jsonObject(json);
		long count = object.getJSONObject("response").getLong("numFound");
		return count;
	}
	
	public static long countArticle(String word, List<Integer> category) {
		
		String json = HttpUtils.getRequest(url_article + "+AND+body_t%3A" + word.replace(" ", "+")  
				+ "+AND+category_is%3A" + queryList(category) 
				+ "&start=" + 0 + "&rows=" + 1);
		JSONObject object = HttpUtils.jsonObject(json);
		long count = object.getJSONObject("response").getLong("numFound");
		return count;
	}
	
	public static long countArticle(String word) {
		String json = HttpUtils.getRequest(url_article + "+AND+body_t%3A" + word.replace(" ", "+") + "&start=" + 0 + "&rows=" + 1);
		JSONObject object = HttpUtils.jsonObject(json);
		long count = object.getJSONObject("response").getLong("numFound");
		return count;
	}
	
	public static long totalArticle() {
		String json = HttpUtils.getRequest(url_article + "&start=" + 0 + "&rows=" + 1);
		JSONObject object = HttpUtils.jsonObject(json);
		long count = object.getJSONObject("response").getLong("numFound");
		return count;
	}
	
	public static Map<String, Long> countArticle(Collection<String> ws) {
		Map<String, Long> result = new HashMap<String, Long>();
		
		for(String w : ws) {
			Long count = countArticle(w);
			result.put(w, count);
		}
		
		
		return result;
	}
	
	public static List<Article> getBabeArticleById(int articleId) {
		String json = HttpUtils.getRequest(url_article + "+AND+article_id_l%3A" + articleId);
		return getBabeArticle(json);
	}
	
	public static List<Article> getBabeArticle(int start, int rows) {
		String json = HttpUtils.getRequest(url_article + "&start=" + start + "&rows=" + rows);
		
		return getBabeArticle(json);
	}
	
	public static List<Article> getBabeArticle(String json) {
		JSONObject object = HttpUtils.jsonObject(json);
		
		JSONArray docs = object.getJSONObject("response").getJSONArray("docs");
		
		List<Article> result = new ArrayList<Article>();
		for(int i = 0 ; i < docs.length() ; i ++) {
			JSONObject o = docs.getJSONObject(i);
			Article e = new Article(
					o.getInt("article_id_l"), 
					o.getString("title_t"),
					o.getString("summary_t"),
					o.getString("body_t"), 
					o.getString("url_s"));
			try {
				e.allEntity = getList(o.getJSONArray("allentity_ss"));
				e.entity = getList(o.getJSONArray("entity_ss"));
				e.category = getList(o.getJSONArray("category_name_ss"));
				e.catId = getIntList(o.getJSONArray("category_is"));
				
			} catch (Exception er) {
				
			}
			result.add(e);
			
			printArticle(e);
		}
		
		return result;
	}
	
	public static void parseBody(Article a) {
		String html = a.content;
		Document doc = Jsoup.parse(html);
		String text = doc.text();
		
		String[] sents = TextParser.sentenize(text);
		
		for(int i = 0 ; i < sents.length ; i ++) {
			//System.out.println(sents[i]);
			System.out.println();
			String[] word = TextParser.tokenize(sents[i]);
			for(int j = 0 ; j < word.length ; j ++) {
				System.out.println(word[j]);
			}
			System.out.println();
		}
	}
	
	public static String htmlText(String html) {
		Document doc = Jsoup.parse(html);
		String text = doc.text();
		
		return text;
	}
	
	public static Set<String> parseCNE(Article a) {
		String html = a.content;
		Document doc = Jsoup.parse(html);
		String text = doc.text();
		return CneDetector.processCapitalized(text);
	}
	
	public static void parseBody() {
		String text = "Saint-Étienne is, arguably, the \"most successful\" club in French football history having won ten Ligue 1 titles, six Coupe de France titles, a Coupe de la Ligue title and five Trophée des Champions (the French Super Cup). The club's ten league titles are the most professional league titles won by a French club, while the six cup victories places the club third among most Coupe de France titles. Saint-Étienne has also won the second division championship on three occasions. The club achieved most of its honours in the 1960s and 1970s when the club was led by managers Jean Snella, Albert Batteux, and Robert Herbin. Saint-Étienne's primary rivals are Olympique Lyonnais, who are based in nearby Lyon. The two teams annually contest the Derby Rhône-Alpes. In 2009, the club added a female section to the football club.";
	
		CneDetector.processCapitalized(text);
	}
	
	
	public static void sample() {
		//TextParser.init();
		CneDetector.init();
		List<Article> as = getBabeArticleById(11831647);//10672395); //10672395 //7911252
		String q = queryList(as.get(0).catId);
		System.out.println(q);
		//parseBody();
		Article a = as.get(0);
		Set<String> ws = parseCNE(a);
		
		System.out.println("category: " + a.catId);
		for(String w : ws) {
			long count = countArticle(w, a.catId);
			System.out.printf("'%-10d'", count);
			System.out.print(" :: " + w + "\n");
		}
		
		System.out.println("\n\n");
		
		DocWordCat dWC = new DocWordCat(a.articleId, a.catId.get(0), a.category.get(0)); 
		
		List<Category> cats = SqlClient.getCategory();
		for(String w: ws) {
			System.out.println("\n\n" + w);
			Map<Integer, Double> catCount = new HashMap<Integer, Double>();
			
			for(int k = 0 ; k < cats.size() ; k ++) {
				Category c = cats.get(k);
				long count = countArticle(w, c.catId);
				System.out.printf("'%-10d'", count);
				System.out.print(" :: " + c.catId + "\n");
				
				catCount.put(c.catId, (double)count);
			}
			
			dWC.addWordCat(w, catCount);
		}
		
		System.out.println("\n\n");
		Map<String, Integer> catRank = dWC.wordCatRank();
		for(String key: catRank.keySet()) {
			System.out.println(key + " -- " + catRank.get(key));
		}
	}
	
	public static void catSample() {
		List<Article> as = getBabeArticle(0, 1);
		Article a = as.get(0);
		System.out.println(getJSONArray(a.catId));
		System.out.println(getJSONArray(a.category));
	}
	
	public static void parseSample() {
		List<Article> as = getBabeArticle(0, 1);
		Article a = as.get(0);
		
		CneDetector.init();
		String content = htmlText(a.content);
		List<List<String>> res = CneDetector.parse(content);
		
		System.out.println();
		for(List<String> s : res) {
			System.out.println();
			for(String i : s) {
				System.out.println(i);
			}
			
		}
	}
	
	public static void freqSample() {
		List<Article> as = getBabeArticle(0, 1);
		Article a = as.get(0);
		
		CneDetector.init();
		String content = htmlText(a.content);
		Map<String, Double> res = CneDetector.processFreq(content);
		
		System.out.println();
		for(String key : res.keySet()) {
			System.out.println(key + " -- " + res.get(key));
		}
	}
	
	public static void averageDocLen() {
		List<Article> as = getBabeArticle(0, 1000);
		
		CneDetector.init();
		double tL = 0;
		double c = as.size();
		for(Article a : as) {
			String content = htmlText(a.content);
			long l = CneDetector.docLen(content);
			System.out.println(a.articleId + " -- " + l);
			tL += l;
		}
		tL /= c;
		System.out.println("Average: ");
		System.out.println(tL);
	}
	
	public static void allCandidate() {
		List<String> result = new ArrayList<String>();
		
		List<Article> as = 
				//getBabeArticleById(
						//11588577);
						//11880499);
				//11588577);
				//11880499);
				//11880971);
				//11877525);
				//11873189);
				//11879701);
				//11831647); 
				//11720946);
				//11854246);
				//11795823);
				getBabeArticle(0, 200); 
				
		
		CneDetector.init();
		for(Article a : as) {
			String content = htmlText(a.content);
			
			result.add(a.content);
			result.add("\n");
			result.add(a.articleId + "");
			result.add(a.url);
			
			long start = System.currentTimeMillis();
			List<String> candidate = CneDetector.genCanScore(content);
			long value = System.currentTimeMillis() - start;
			//CneDetector.genCandidate(content);
			System.out.println("id: " + a.articleId + " -- time: " + (value * 0.001));
			result.addAll(candidate);
			result.add(" -- time: " + (value * 0.001) + "\n\n--------------------\n\n");
			
			start = System.currentTimeMillis();
			candidate = CneDetector.genUnmatchCan(content);
			value = System.currentTimeMillis() - start;
			//CneDetector.genCandidate(content);
			System.out.println("Time: " + (value * 0.001));
			result.addAll(candidate);
			result.add(" -- time: " + (value * 0.001) + "\n\n--------------------\n\n");
		}
		
		TextfileIO.writeFile("sample_result/root_score_can.update.4.5.txt", result);
		
	}
	
	
	public static void countSample() {
		String[] w = {
				"Perhelatan Miss Universe",
				"Perhelatan Miss",
				"Miss Universe",
//				"Kezia Warouw",
//				"Filipina",
//				"Courtesy",
//				"Pia Alonzo Wurtzbach",
//				"Pia Alonzo",
//				"Alonzo Wurtzbach",
//				"Ivan Gunawan",
//				"Januari",
//				"Puteri Indonesia",
//				"Ia",
//				"Instagram",
//				"Lara Dutta",
//				"Manado",
//				"Indonesia"	
		};
		
		List<Category> cats = SqlClient.getCategory();
		for(int i = 0 ; i < w.length ; i ++) {
			System.out.println("\n\n" + w[i]);
			for(int k = 0 ; k < cats.size() ; k ++) {
				Category c = cats.get(k);
				long count = countArticle(w[i], c.catId);
				System.out.printf("'%-10d'", count);
				System.out.print(" :: " + c.catId + "\n");
			}
		}
	}
	
	public static void test() {
		String c = "Januari 2017";
		CneDetector.init();
		boolean r = CneDetector.candidateFilter(c);
		System.out.println(r);
	}
	
	public static void test1() {
		String c = "Lubang jalan di ruas Kudus-Pati, Desa Gondoharum, Kecamatan Jekulo. (suaramerdeka.com/Anton W. Hartono)";
		String r = CneDetector.preProcess(c);
		System.out.println(r);
	}
	
	public static void testRedirect() {
		CneDetector.init();
		//SpellApp.printRedirect();
		System.out.println(SpellApp.getRedirect("ahok"));
		
	}
	
	public static void main(String[] args) {
		//testRedirect();
		//test1();
		//test();
		allCandidate();
		//averageDocLen();
		//freqSample();
		//parseSample();
		//sample();
		//countSample();
		
	}
	
	

}
