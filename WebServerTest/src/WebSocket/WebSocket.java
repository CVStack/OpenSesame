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

import MongoDAO.MongoDBConnection;
import Util.JsonUtil;

// path : ip:8080/WebServerTest/WebSocket/WebSocket
@ServerEndpoint("/WebSocket/WebSocket")
public class WebSocket {
	
	private static HashMap<String,Session> Mac_SessionMapping =
			new HashMap<String, Session>();
	//Mac주소와 Session 맵핑
	
	private static HashMap<String,Integer> Mac_requestMapping = 
			new HashMap<String,Integer>();
	//Mac주소와 request 유무를 맵핑 --> esp32에서 받아오는 데이터를 안드로이드로 넘길지 말지 결정할때 사용
	
	private static MongoDBConnection conn = MongoDBConnection.getInstance();
	
	@OnOpen
	public void handleOpen(Session session){
		// 클라이언트가 접속시 요청되는 function
		System.out.println("Client is connected");
		
		System.out.println("current session list");

	}
	
	@OnMessage
	public void handleMessage(String message,Session session) {
		//메세지 오면 처리하는 함수
		//json 형태로 송수신 		
		printCurrentSessionList();
		System.out.println();
		
		System.out.println(message);
		JSONObject jo = JsonUtil.stringToJson(message);
		
		//message를 json으로 파싱		
		if(!(Mac_SessionMapping.containsKey((String)jo.get("MAC")))) {
			//맵핑 리스트에 해당 맥주소가 없다면 넣어줌
			Mac_SessionMapping.put((String)jo.get("MAC"), session);
			return; //처음엔 session 등록 후 종료
		}
		
		String mac = return_OpponentMac((String)jo.get("MAC")); //상대방의 Mac 주소를 얻음. --> database에서 가져올 예정.
		System.out.println("opponentMac : " + mac);
		
		Session s; //상대방 세션
		
		if((jo.get("type")).equals("Arduino")) {
			//아두이노에서 데이터를 보냈을때
			if((s = Mac_SessionMapping.get(mac)) != null) {
				//해당 세션이 맵핑이 되어있으면 상대방 세션 얻음				
				try {
					if(Mac_requestMapping.get(mac) == 1) {
						s.getBasicRemote().sendText((jo.get("sensors")).toString());
						Mac_requestMapping.replace(mac,0);
						System.out.println("Arduino send sensors data to user");
					}
					// 안드로이드에서 요청을 했을때만 보내야됨. 
					
					conn.insertDocument("SensorCollection", message); //이상함
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
				
				System.out.println(request_type + " " + my_MAC);
				
				try {
					if(request_type.equals("sensors"))
							Mac_requestMapping.put(my_MAC,1);
					s.getBasicRemote().sendText((String)jo.get("request_type"));
					System.out.println("user request sensor values");
					// 안드로이드에서 아두이노에게 sensor값 요청 or 창문 제어 요청
					//해당 세션으로 데이터를 보냄
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	@OnClose
	public void handleClose(Session s) {
		//접속 끊길때 
		Mac_SessionMapping.forEach((key,value) ->
			
		{
			if(s.getId().equals(value.getId())) { //연결 끊길때 해당 세션 맵핑에서 지움
				Mac_SessionMapping.remove(key);
			}
		}
		);	
		System.out.println("Client disconnected..");
		
		System.out.println("current Session List");
		printCurrentSessionList();
	}
	
	@OnError
	public void handleError(Throwable t) {
		//Error 처리
		t.printStackTrace();
	}
	
	private String return_OpponentMac(String mac) {
		
		String result = null;
		Document doc = conn.getDocument_MAC_pair(mac); 
		
		if(((String)doc.get("MAC_ad")).equals(mac))
			result = (String)doc.get("MAC_ar");
		else
			result = (String)doc.get("MAC_ad");
		
		return result;
	}
	
	private void printCurrentSessionList() {
		
		Mac_SessionMapping.forEach((key, value) -> 
		{System.out.println("MAC : " + key + " Session_id : " + value.getId());});
	}
	
}
