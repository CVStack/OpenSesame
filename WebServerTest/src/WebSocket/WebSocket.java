package WebSocket;

import java.util.HashMap;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.bson.Document;
import org.json.simple.JSONObject;

import Util.JsonUtil;
import Util.MongoDBConnection;

// path : ip:8080/WebServerTest/WebSocket/WebSocket
@ServerEndpoint("/WebSocket/WebSocket")
public class WebSocket {
	
	private static HashMap<String,Session> Mac_SessionMapping =
			new HashMap<String, Session>();
	//Mac주소와 Session 맵핑
	
	private static HashMap<String,Integer> Mac_requestMapping = 
			new HashMap<String,Integer>();
	//Mac주소와 request 유무를 맵핑
	
	private static MongoDBConnection conn = MongoDBConnection.getInstance(); 
	@OnOpen
	public void handleOpen(Session session){
		// 클라이언트가 접속시 요청되는 function
		System.out.println("Client is connected");
	}
	
	@OnMessage
	public void handleMessage(String message,Session session) {
		//메세지 오면 처리하는 함수
		//json 형태로 송수신 		
		
		JSONObject jo = JsonUtil.stringToJson(message);
		//message를 json으로 파싱
		
		if(!(Mac_SessionMapping.containsKey((String)jo.get("MAC")))) {
			//맵핑 리스트에 해당 맥주소가 없다면 넣어줌
			Mac_SessionMapping.put((String)jo.get("MAC"), session);
		}
		
		String mac = "temp"; //상대방의 Mac 주소를 얻음. --> database에서 가져올 예정.
		Session s; //상대방 세션
		
		if((jo.get("type")).equals("아두이노")) {
			//아두이노에서 데이터를 보냈을때
			if((s = Mac_SessionMapping.get(mac)) != null) {
				//해당 세션이 맵핑이 되어있으면 상대방 세션 얻음				
				try {
					if(Mac_requestMapping.get(mac) == 1) {
						s.getBasicRemote().sendObject(jo.get("sensors"));
						Mac_requestMapping.replace(mac,0);
					}
					// 안드로이드에서 요청을 했을때만 보내야됨. 
					else {
						//데이터 베이스로 전송
					}		
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		else {
			//안드로이드에서 데이터를 보냈을때
			if((s = Mac_SessionMapping.get(mac)) != null) {
				//해당 세션이 맵핑이 되어있으면
				String request_type = (String)jo.get("request_type");
				String my_MAC = (String)jo.get("MAC");
				
				try {
					if(request_type.equals("sensors"))
							Mac_requestMapping.replace(my_MAC,1);
					s.getBasicRemote().sendText((String)jo.get("request_type"));
					// 안드로이드에서 아두이노에게 sensor값 요청 or 창문 제어 요청
					//해당 세션으로 데이터를 보냄
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	@OnClose
	public void handleClose() {
		//접속 끊길때 
		if(conn.closeConnection())
			System.out.println("MongoDB Connection closed");
			
		System.out.println("Client disconnected..");
	}
	
	@OnError
	public void handleError(Throwable t) {
		//Error 처리
		t.printStackTrace();
	}
	
	
}
