package id.co.babe.notification;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import com.fasterxml.jackson.databind.ObjectMapper;

public class AndroidPush {

	public static final String API_KEY = "AIzaSyDsO0YYsDIPhDAFrQL0UWNrzzIBjSSPwkI";
	
	
	

	public static class Content {
		public List<String> registration_ids;
		public Map<String, String> data;

		public void addRegId(String regId) {
			if (registration_ids == null)
				registration_ids = new ArrayList<String>();
			registration_ids.add(regId);
		}

		public void createData(String title, String message) {
			if (data == null)
				data = new HashMap<String, String>();

			data.put("title", title);
			data.put("message", title + "/" + message);
		}

	}
	
	
	public static void main(String[] args) {
		List<String> ds = new ArrayList<String>();
		ds.add("eLWxmA0faE0:APA91bGKim2pR6l8jimQfN5xilL0W6T1cyHMhYLfpBxut7s8B88Acm_JlVcvUuTggPUDkzX6MZgddeDxTaM1F2ejlYKf0Iwftiy_dAaAONfgacziKrVEnOY0jiZo1TokEpHzG0d5LCBp");
		
		try {
			sendPushToDevices(ds);
		} catch (Exception e) {
			System.out.println(e);
			e.printStackTrace();
		}
	}
	

	public static List<String> sendPushToDevices(List<String> listDeviceIds) throws Exception {
		List<String> returnVl = new ArrayList<>();
		
		
		Content content = new Content();
		for (String str : listDeviceIds) {
			content.addRegId(str);
		}
		
		
		content.createData("Title", "Notification Message");
		
		
		URL url = new URL("https://android.googleapis.com/gcm/send");
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type", "application/json");
		conn.setRequestProperty("Authorization", "key=" + API_KEY);
		conn.setDoOutput(true);
		ObjectMapper mapper = new ObjectMapper();
		DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
		mapper.writeValue(wr, content);
		wr.flush();
		wr.close();
		int responseCode = conn.getResponseCode();
		System.out.println(responseCode);

		if (responseCode == 200) {
			BufferedReader br = new BufferedReader(new InputStreamReader(
					conn.getInputStream()));
			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = br.readLine()) != null) {
				sb.append(line + "\n");
			}
			br.close();

			JSONObject jsonObject = new JSONObject(sb.toString());
			JSONArray jsonArray = jsonObject.getJSONArray("results");
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject tmp = jsonArray.getJSONObject(i);
				System.out.println(tmp);
				if (tmp.has("error"))
					returnVl.add(listDeviceIds.get(i));
			}
			// System.out.println(sb.toString());
		}
		// logger.info("_____________________________________________________");
		for(String item : returnVl){
			System.out.println(item);
		}
		return returnVl;
	}

}
