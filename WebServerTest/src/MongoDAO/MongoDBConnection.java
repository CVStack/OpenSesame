package MongoDAO;

import org.bson.Document;
import org.json.simple.JSONObject;

import com.mongodb.DBObject;
import com.mongodb.client.FindIterable;
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
    
	private MongoDBConnection() {
		mc = MongoClients.create(); // connect to mongodb instance
		db = mc.getDatabase("openSeasame"); //connect to database
	}
	
	public static MongoDBConnection getInstance() {
		return conn;
	}
	
	public void insertDocument(String collection_Name, String json) {
			
		try {
			collection = db.getCollection(collection_Name);
			Document doc = Document.parse(json);
			collection.insertOne(doc);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public Document getDocument_MAC_pair(String mac) {
		//해당 mac의 pair를 가져옴
		Document result = null;
		try {
			collection = db.getCollection("MAC_MappingCollection");
			result = collection.find(Filters.or(Filters.eq("MAC_ad",mac),Filters.eq("MAC_ar",mac))).first();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	public FindIterable<Document> getDocument_sensors(String mac) {
		//해당 사용자의 센서값들을 리턴
		FindIterable<Document> result = null;
		try {
			collection = db.getCollection("SensorCollection");
			result = collection.find(Filters.eq("MAC")).projection(Projections.fields(Projections.exclude("_id", "MAC_ad")));
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
