package Util;

import javax.print.Doc;

import org.bson.Document;
import org.json.simple.JSONObject;

import com.mongodb.DBObject;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
public class MongoDBConnection {

	private static MongoDBConnection conn = new MongoDBConnection();
	private MongoClient mc;
    private MongoDatabase db;
    private MongoCollection<Document> collection;
    private boolean connected; //단 한번만 connect하고 계속 사용
    
	private MongoDBConnection() {
		connected = false;
	}
	
	public static MongoDBConnection getInstance() {
		
		return conn;
	}
	
	private void connection() {
		
		mc = MongoClients.create(); // connect to mongodb instance
    	db = mc.getDatabase("sensorDB"); // access database
    	connected = true;
	}
	
	public void insertDocument(String json) {
		
		try {
			if(!connected)
				connection();
			collection = db.getCollection("sensorData");
			Document doc = Document.parse(json);
			collection.insertOne(doc);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public Document findDocument_request(String MacAddress_esp32) {
		
		Document result = null;
		try {
			if(!connected)
				connection();
			collection = db.getCollection("request");
			collection.find(Filters.eq("MacAddress_ESP32", MacAddress_esp32))
					.projection(Projections.elemMatch("request")).first();
			
			//Mac address가 같은 document의 request만 return
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	public boolean closeConnection() {
		
		boolean result = false;
		try {
			mc.close();
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
}
