package id.co.babe.analysis.data;

import id.co.babe.analysis.model.Article;

import java.util.List;

import org.json.JSONObject;

public class FileClient {

	public static List<Article> readBadArticle(String bad_file) {
		return null;
	}

	public static List<Article> readGoodArticle(String good_file) {
		return null;
	}
	
	
	public static Article jsonToArticle(String json) {
		Article a = new Article();
		
		return a;
	}
	
	public static String articleToJson(Article a) {
		JSONObject o = new JSONObject();
		
		return o.toString();
	}
	
	
	public static List<Article> readJsonArticle(String json_file) {
		
		return null;
	}
	
	public static void writeJsonArticle(String json_file, List<Article> articles) {
		
	}
	

}
