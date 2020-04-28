package WebSocket;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import Util.MongoDBConnection;

// path : ip:8080/WebServerTest/WebSocket/WebSocket
@ServerEndpoint("/WebSocket/WebSocket")
public class WebSocket {
	
	private static MongoDBConnection conn = MongoDBConnection.getInstance();
	
	@OnOpen
	public void handleOpen(Session session){
		// 클라이언트가 접속시 요청되는 함수
		System.out.println("Client is connected");
	}
	
	@OnMessage
	public String handleMessage(String message) {
		
		//메세지 오면 처리하는 함수
		//json 형태로 송수신 
//		System.out.println("from client : " + message);
		if(message != null)
			conn.insertDocument(message);
		String reply = "Thank you";
		return reply;
	}
	
	@OnClose
	public void handleClose() {
		//접속 끊길때 
		System.out.println("Client disconnected..");
	}
	
	@OnError
	public void handleError(Throwable t) {
		//Error 처리
		t.printStackTrace();
	}
}
