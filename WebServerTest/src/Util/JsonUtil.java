package Util;

import org.bson.Document;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class JsonUtil {
	
	private static JSONParser parser = new JSONParser();
	public static JSONObject stringToJson(String json) {
		
		JSONObject result = null;
		try {
			result = (JSONObject) parser.parse(json);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
//	public static Document jsonToDocument(JSONObject jo){
//		
//		Document document = null;
//		
//		try {
//			
//			
//		}
//		catch(Exception e) {
//			
//			
//		}
//	} 
	
}
