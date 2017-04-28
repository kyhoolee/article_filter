package id.co.babe.analysis.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

public class HttpUtils {
	public static String getRequest(String url) {
		String result = "";
		try {
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpGet getRequest = new HttpGet(url);
			//getRequest.addHeader("accept", "application/json");

			HttpResponse response = httpClient.execute(getRequest);

			BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));
			String output;
			while ((output = br.readLine()) != null) {
				result += output;
			}
			httpClient.getConnectionManager().shutdown();
			return result;
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	

	public static String postRequest(String url) {
		String result = "";
		try {
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpPost postRequest = new HttpPost(url);
			postRequest.addHeader("Accept", "application/json");
			postRequest.addHeader("Content-Type", "application/json");
			// StringEntity input = new StringEntity("{\"qty\":100,\"name\":\"iPad 4\"}");
			// input.setContentType("application/json");
			// postRequest.setEntity(input);
			HttpResponse response = httpClient.execute(postRequest);
			BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));
			String output;
			while ((output = br.readLine()) != null) {
				result += output;
			}
			httpClient.getConnectionManager().shutdown();
			return result;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return result;
	}
	
	public static JSONObject jsonObject(String json) {
		JSONObject result = new JSONObject(json);
		return result;
	}
	
	public static void main(String[] args) {
		postRequest("http://10.2.15.89:9000/v1/entity/extract/10669669");
	}
}
