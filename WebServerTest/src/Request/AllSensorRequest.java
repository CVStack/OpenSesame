package Request;

import java.io.IOException;
import java.util.Arrays;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.bson.Document;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
 
/**
 * Servlet implementation class AllSensorRequest
 */
@WebServlet("/SensorValueRequest/AllSensorRequest")
public class AllSensorRequest extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private MongoClient mc;
    private MongoDatabase db;
    private MongoCollection<Document> collection;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AllSensorRequest() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    public void init() throws ServletException { //초기화 Servlet Container가 생성될 때 처음 한번 실행 됨
    
    	mc = MongoClients.create(); // connect to mongodb instance
    	db = mc.getDatabase("sensorDB"); // access database
    	collection = db.getCollection("sensorData");
    }
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		System.out.println("post come");
		String sensor1 = request.getParameter("sensor1");
		String sensor2 = request.getParameter("sensor2");
		String sensor3 = request.getParameter("sensor3");
		
		Document doc = new Document("ip", request.getHeader("X-FORWARDED-FOR"))
						.append("sensors", Arrays.asList(sensor1,sensor2,sensor3));
		
		collection.insertOne(doc);				
	}

}
