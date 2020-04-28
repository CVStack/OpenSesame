package Util;

import org.bson.Document;
import org.json.simple.JSONObject;

import com.mongodb.DBObject;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class MongoDBConnection {

	private static MongoDBConnection conn = new MongoDBConnection();
	private MongoClient mc;
    private MongoDatabase db;
    private MongoCollection<Document> collection;
    
	private MongoDBConnection() {
		
	}
	
	public static MongoDBConnection getInstance() {
		
		return conn;
	}
	
	private void connection() {
		
		mc = MongoClients.create(); // connect to mongodb instance
    	db = mc.getDatabase("sensorDB"); // access database
    	collection = db.getCollection("sensorData");
	}
	
	public void insertDocument(String json) {
		
		try {
			connection();
			Document doc = Document.parse(json);
			collection.insertOne(doc);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		finally {
			mc.close();
		}
		
	}
}
