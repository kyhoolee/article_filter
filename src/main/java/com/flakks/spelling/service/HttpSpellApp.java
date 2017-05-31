package com.flakks.spelling.service;

import id.co.babe.analysis.util.HttpUtils;
import id.co.babe.analysis.util.TextfileIO;

import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

public class HttpSpellApp extends SpellApp {
	public static final int type_normal = 1;
	public static final int type_stop = 2;
	public static final int type_entity = 3;
	
	
	
	public static String remote_url = "http://10.2.15.176:9004";
	public static boolean isLocal = true;
	
	
	
	
	public static void main(String[] args) {
		//initRemoteNormal("nlp_data/indo_dict/id_full.txt");
		//initRemoteStop("nlp_data/indo_dict/stop_word.txt");
		initRemoteEntity("nlp_data/indo_dict/tag_dict.txt");
		//testInsert();
		//updateType();
		//testUpdate();
	}
	
	public static void testInsert() {
		System.out.println(postToDict("adi", type_normal));
		postToDict("ada", type_stop);
	}
	
	public static void testUpdate() {
		String data = "{\"keyword\": \"akhir\",\"classification\": [],\"taggedUrl\": \"\",\"state\": 0,\"entityType\": [1,2]}";
		System.out.println(HttpUtils.postRequest(remote_url + "/entity/v1/update", data));
	}
	
	
	
	public static String postToDict(String word, int type) {
		String jsonData = "{"
				  + "\"keyword\": \""+ word +"\","
				  + "\"classification\": [],"
				  + "\"taggedUrl\": \"\","
				  + "\"state\": 0,"
				  + "\"entityType\": [" + type + "]"
				+ "}";
		String result = HttpUtils.postRequest(remote_url + "/entity/v1/insert", jsonData);
		//System.out.println(result);
		
		return result;
	}
	
	public static String postToDict(String word, String types) {
		String jsonData = "{"
				  + "\"keyword\": \""+ word +"\","
				  + "\"classification\": [],"
				  + "\"taggedUrl\": \"\","
				  + "\"state\": 0,"
				  + "\"entityType\": " + types + ""
				+ "}";
		System.out.println(jsonData);
		String result = HttpUtils.postRequest(remote_url + "/entity/v1/insert", jsonData);
		System.out.println(result);
		//System.out.println(result);
		
		return result;
	}
	
	public static String updateToDict(String word, String types) {
		String jsonData = "{"
				  + "\"keyword\": \""+ word +"\","
				  + "\"classification\": [],"
				  + "\"taggedUrl\": \"\","
				  + "\"state\": 0,"
				  + "\"entityType\": " + types + ""
				+ "}";
		//System.out.println(jsonData);
		String result = HttpUtils.postRequest(remote_url + "/entity/v1/update", jsonData);
		//System.out.println(result);
		//System.out.println();
		
		return result;
	}
	
	public static int postUpdateToDict(String word, int type) {
		int r = 0;
		String jsonData = "{"
				  + "\"keyword\": \""+ word +"\","
				  + "\"classification\": [],"
				  + "\"taggedUrl\": \"\","
				  + "\"state\": 0,"
				  + "\"entityType\": [" + type + "]"
				+ "}";
		//System.out.println(jsonData);
		String result = HttpUtils.postRequest(remote_url + "/entity/v1/insert", jsonData);
		//System.out.println(result);
		JSONObject o = new JSONObject(result);
		int id = -1;
		try{
			id = o.getInt("insertedId");
		} catch (Exception e) {
			id = -1;
		}
		//System.out.println(id);
		// Duplicate --> re-insert
		if(id == -1) {
			try {
				JSONObject entity = o;//.getJSONArray("entities").getJSONObject(0);
				JSONArray entityType = entity.getJSONArray("entityType");
				//System.out.println(type + " -- " + entityType);
				boolean check = true;
				for(int i = 0 ; i < entityType.length() ; i ++) {
					
					//System.out.println("element " + entityType.get(i).toString());
					int t = Integer.parseInt(entityType.get(i).toString());
					if(t == type) {
						check = false;
					}
				}
				//System.out.println(check);
				if(check) {
					entityType = entityType.put(type);
					//System.out.println(entityType);
					updateToDict(word, entityType.toString());
					r = 1;
				}
				//System.out.println(entityType.toString());
				
			} catch (Exception e) {
				
			}
		}
		
		return r;
	}
	
	public static void updateType() {
		String jsonData = "{"
				  + "\"keyword\": \""+ "hihi" +"\","
				  + "\"classification\": [],"
				  + "\"taggedUrl\": \"\","
				  + "\"state\": 0,"
				  + "\"entityType\": [" + 1 + "]"
				+ "}";
		
		try {
			JSONObject entity = new JSONObject(jsonData);
			JSONArray entityType = entity.getJSONArray("entityType");
			JSONArray newType = entityType.put(2);
			entity.put("entityType", newType);
			
			System.out.println(entity);
		} catch (Exception e) {
			
		}
	}
	
	
	public static void initRemoteNormal(String path) {
		
		List<String> lines = TextfileIO.readFile(path);
		int count = 0;
		long start = System.currentTimeMillis();
		for(String line : lines) {
			try {
				count ++;
				String word = line.split(" ")[0];
				//indoDict.put(tokens[0], Integer.parseInt(tokens[1]));
				postToDict(word, type_normal);
				if(count % 100 == 0) {
					long time = System.currentTimeMillis() - start;
					System.out.println(time + " -- " + count);
					start = System.currentTimeMillis();
				}
				
			} catch (Exception e) {
				System.out.println(" --- " + line);
				e.printStackTrace();
			}
		}
		
	}
	
	
	
	public static void initRemoteStop(String path) {
		
		
		List<String> lines = TextfileIO.readFile(path);
		int dups = 0;
		int count = 0;
		long start = System.currentTimeMillis();
		for(String line : lines) {
			try {
				count ++;
				String word = line.toLowerCase();
				int r = postUpdateToDict(word, type_stop);
				dups += r;
				if(count % 100 == 0) {
					long time = System.currentTimeMillis() - start;
					System.out.println(time + " -- " + count + " -- " + dups);
					start = System.currentTimeMillis();
					//break;
				}
				
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	public static void initRemoteEntity(String path) {
		
		List<String> dictionary = TextfileIO.readFile(path);
		int dups = 0;
		int count = 0;
		long start = System.currentTimeMillis();
		for (String word : dictionary) {
			Set<String> varied = variedWord(word.toLowerCase());
			
			for(String w: varied) {
				count ++;
				int r = postUpdateToDict(w, type_entity);
				dups += r;
				if(count % 100 == 0) {
					long time = System.currentTimeMillis() - start;
					System.out.println(time + " -- " + count + " -- " + dups);
					start = System.currentTimeMillis();
					//break;
				}
			}

		}
	}
	public static void initRemoteEntity(String... path) {
		if(isLocal) {
			SpellApp.initEntity(path);
		}
	}
	public static void initRemoteRedirect(String path) {
		if(isLocal) {
			SpellApp.initRedirect(path);
		}
	}
	
	
	public static void initNormal(String path) {
		if(isLocal) {
			SpellApp.initNormal(path);
		}
		
	}
	public static void initStop(String path) {
		if(isLocal) {
			SpellApp.initStop(path);
		}	
	}
	public static void initEntity(String path) {
		if(isLocal) {
			SpellApp.initEntity(path);
		}
	}
	public static void initEntity(String... path) {
		if(isLocal) {
			SpellApp.initEntity(path);
		}
	}
	public static void initRedirect(String path) {
		if(isLocal) {
			SpellApp.initRedirect(path);
		}
	}
	
	/**
	 * Check word is Stop
	 * @param word
	 * @return
	 */
	public static boolean checkStop(String word) {
		if(isLocal) {
			return SpellApp.checkStop(word);
		}
		
		return false;
	}
	
	/**
	 * Check word is Entity 
	 * @param word
	 * @return
	 */
	public static int checkEntity(String word) {
		if(isLocal) {
			return SpellApp.checkEntity(word);
		}
		return 1;
	}
	
	/**
	 * Get root of Word
	 * @param word
	 * @return
	 */
	public static String checkRedirect(String word) {
		if(isLocal) {
			return SpellApp.checkRedirect(word);
		}
		return word;
	}
	
	/**
	 * Check word is Normal
	 * @param word
	 * @return
	 */
	public static boolean checkNormal(String word) {
		if(isLocal) {
			return SpellApp.checkNormal(word);
		}
		return true;
	}

}
