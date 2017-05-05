package id.co.babe.notification;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.notnoop.apns.APNS;
import com.notnoop.apns.ApnsService;

public class IosPush {

	
	public static void main(String[] args) {
		samplePush();
	}
	
	public static void samplePush() {
		List<String> tempRegis = new ArrayList<String>();
		tempRegis.add("1");
		tempRegis.add("7b0ab1564af0e9baa36572f44c48c7c8b5992c4de1cceefd9206a1f77be9a508");
		sendPushList(initService(), tempRegis, "title", "message");
	}
	
	
	public static ApnsService initService() {
		ApnsService service = APNS
				.newService()
				.withCert("doc/Certificates_Shop_dev.p12","123456789")
				//.withProductionDestination()
				.withSandboxDestination().build();
		
		return service;
	}

	
	public static List<String> sendPushList(ApnsService apnsService, List<String> tempRegis,String title, String message){
		List<String> returnVl = new ArrayList<String>();
		apnsService.push(tempRegis, 
				"{\"aps\":{\"alert\":\""+title+"\","
				+"\"message\":\""+message+"\","
				+ "\"badge\":0,\"sound\":\"default\"}}");
		Map<String, Date> listInActive = apnsService.getInactiveDevices();
		for(String str : listInActive.keySet()){
			returnVl.add(str.toLowerCase());	
			System.out.println(str);
		}
		
		apnsService.stop();
		return returnVl;
		
	}
	
	
}
