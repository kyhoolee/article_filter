package id.co.babe.analysis.wiki;

import id.co.babe.analysis.util.TextfileIO;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringEscapeUtils;

public class WikiClient {
	
	public static final String wiki_entity = "<http://dbpedia.org/resource/";
	public static final String wiki_redirect = "<http://id.dbpedia.org/resource/";
	public static final String wiki_end = ">";
	
	public static void main(String[] args) throws UnsupportedEncodingException {
		//sample();
		//readWikiEntity("/home/mainspring/tutorial/resources/data/DbPedia/id/page_ids_id.nt", "nlp_data/wiki_dict/page_wiki_entity.txt", wiki_redirect);
		//parseRootWiki("nlp_data/wiki_dict/page_links_unredirected_id.nt", "nlp_data/wiki_dict/unredirect_entity.txt");
//		mergeEntity("nlp_data/wiki_dict/wiki_tag.txt", 
//				"nlp_data/wiki_dict/wiki_entity.txt",
//				"nlp_data/indo_dict/tag_dict.txt");
		//parseRootWiki("nlp_data/wiki_dict/redirects_id.nt", "nlp_data/wiki_dict/redirect_entity_map.txt");
		filterEntity("nlp_data/wiki_dict/page_wiki_entity.txt", "nlp_data/wiki_dict/filtered_page_wiki_entity.txt");
	}
	
	public static Set<String> filterEntity(String inPath, String outPath) {
		Set<String> r = new HashSet<String>();
		
		List<String> in = TextfileIO.readFile(inPath);
		for(String e : in) {
			if(filterEntity(e)) {
				r.add(e);
			}
		}
		
		TextfileIO.writeFile(outPath, r);
		
		return r;
	}
	
	public static boolean filterEntity(String entity) {
		boolean result = (entity.length() > 1)
				&& (!entity.contains("Kategori:"))
				&& (!entity.contains("Templat:"))
				&& (!entity.contains("Berkas:"));
		
		return result;
	}
	
	
	public static Set<String> mergeEntity(String out, String... paths) {
		Set<String> r = new HashSet<String>();
		
		for(String p : paths) {
			Set<String> rs = readEntity(p);
			r.addAll(rs);
		}
		
		TextfileIO.writeFile(out, r);
		
		return r;
	}
	
	public static Set<String> readEntity(String path) {
		Set<String> r = new HashSet<String>();
		List<String> e = TextfileIO.readFile(path);
		r.addAll(e);
		return r;
	}
	
	
	public static Set<String> readWikiEntity(String path, String outPath) {
		Set<String> result = new HashSet<String>();
		List<String> wikiData = TextfileIO.readFile(path);
		
		for(String line : wikiData) {
			String entity = parseWiki(line);
			if(entity != null)
				result.add(entity);
		}
		
		TextfileIO.writeFile(outPath, result);
		
		return result;
		
	}
	
	public static Set<String> readWikiEntity(String path, String outPath, String sign) {
		Set<String> result = new HashSet<String>();
		List<String> wikiData = TextfileIO.readFile(path);
		
		for(String line : wikiData) {
			String entity = parseWiki(line, sign);
			if(entity != null)
				result.add(entity);
		}
		
		TextfileIO.writeFile(outPath, result);
		
		return result;
		
	}
	
	
	public static String urlDecode(String input) {
		try {
			return java.net.URLDecoder.decode(input, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void sample() {
		String unicode = "\u010Cesk\u00E1 l\u00EDpa";
		String r = StringEscapeUtils.unescapeJava(unicode);
		System.out.println(r);
	}
	
	public static String unicode(String input) {
		String text = StringEscapeUtils.unescapeJava(input);
		return text;
	}
	
	public static String parseWiki(String line) {
		if(line.contains(wiki_entity)) {
			int endIndex = line.indexOf(">");
			String entity = line.substring(wiki_entity.length(), endIndex);
			entity = entity.replace("_", " ");
			entity = urlDecode(entity);
			entity = unicode(entity);
			return entity;
		} else {
			return null;
		}
	}
	
	public static String parseWiki(String line, String sign) {
		if(line.contains(sign)) {
			int endIndex = line.indexOf(">");
			String entity = line.substring(sign.length(), endIndex);
			entity = entity.replace("_", " ");
			entity = urlDecode(entity);
			entity = unicode(entity);
			return entity;
		} else {
			return null;
		}
	}
	
	public static Map<String, String> parseRootWiki(String inPath, String outPath) {
		Map<String, String> result = new HashMap<String, String>();
		
		List<String[]> redirects = new ArrayList<String[]>();
		List<String> wikiData = TextfileIO.readFile(inPath);
		
		for(String line : wikiData) {
			String[] entity = parseRootLine(line);
			if(entity != null && entity.length == 2) {
				if(entity[0] != null && entity[1] != null) {
					result.put(entity[0], entity[1]);
					redirects.add(entity);
				}
			}
		}
		
		//TextfileIO.writeFile(outPath, result.keySet());
		TextfileIO.writeCsv(outPath, redirects);
		
		return result;
	}
	
	public static String[] parseRootLine(String line) {
		if(!line.contains(wiki_redirect))
			return null;
		
		
		String[] res = new String[2];
		
		int start = line.indexOf(wiki_redirect) + wiki_redirect.length();
		int end = line.indexOf(wiki_end, start);
		String entity = line.substring(start, end);
		entity = entity.replace("_", " ");
		entity = urlDecode(entity);
		entity = unicode(entity);
		
		start = line.indexOf(wiki_redirect, end) + wiki_redirect.length();
		end = line.indexOf(wiki_end, start);
		String root = line.substring(start, end);
		root = root.replace("_", " ");
		root = urlDecode(root);
		root = unicode(root);
		
		res[0] = entity;
		res[1] = root;
		
		return res;
	}
	

}
