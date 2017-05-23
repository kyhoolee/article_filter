package id.co.babe.analysis.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

public class Article {
	public static final String BAD = "0";
	public static final String NORMAL = "1";
	public static final String PROBABLE = "2";
	
	public static final int f_title_len = 0;
	public static final int f_title_word = 1;
	
	public static final int f_sub_len = 2;
	public static final int f_sub_word = 3;
	
	public static final int f_doc_len = 4;
	public static final int f_doc_word = 5;
	
	public static final int f_sent_len = 6;
	public static final int f_sent_word = 7;
	public static final int f_sent_num = 8;
	
	public static final int f_parag_len = 9;
	public static final int f_parag_word = 10;
	public static final int f_parag_num = 11;
	
	public static final int f_image_num = 12;
	
	public static final int num_feature = 13;
	
	public static final String sep_title = "\r\n\r\n\r\n";
	public static final String sep_parag = "\r\n\r\n";
	public static final String sep_sent = ". ";
	
	public static final String babe_asset = "https://assets.babe.news/";
	
	public long articleId; 
	public String title;
	public List<String> allEntity;
	public List<String> entity;
	
	public List<String> category;
	public List<Integer> catId;
	
	public String summary;
	public String url;
	
	public String content;
	public String label;
	
	
	public Map<Integer, Double> features;
	
	
	public Article() {
		this.allEntity = new ArrayList<String>();
		this.entity = new ArrayList<String>();
		this.category = new ArrayList<String>();
		this.catId = new ArrayList<Integer>();
	}
	
	public Article(String content, String label) {
		this.content = content;
		this.label = label;
	}
	
	public Article(long articleId, String title, String content, String url) {
		this.articleId = articleId;
		this.title = title;
		this.content = content;
		this.url = url;
		this.allEntity = new ArrayList<String>();
		this.entity = new ArrayList<String>();
		this.category = new ArrayList<String>();
		this.catId = new ArrayList<Integer>();
	}
	
	public void updateByJson(String json) {
		JSONObject o = new JSONObject(json);
	}
}
