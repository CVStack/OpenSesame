#include <WebSocketClient.h>
#include <WiFi.h>
#include <ArduinoJson.h>

const char* ssid     = "deleste";
const char* password = "20160103";
//char path[] = "/WebServerTest2/WebSocket/WebSocket";
//char host[] = "34.228.216.228";

char path[] = "/WebServerTest/WebSocket/WebSocket";
char host[] = "192.168.0.60";
  
WebSocketClient webSocketClient;

// Use WiFiClient class to create TCP connections
WiFiClient client;
void WifiScan() {
  int n = WiFi.scanNetworks();
  Serial.println("WiFi Scan : " + n);

  if(n > 0) {
      for(int x = 0 ; x < n ; x++) {

          Serial.print(x + 1);
          Serial.print(" : ");
          Serial.print(WiFi.SSID(x));
          Serial.print(" , ");
          Serial.println(WiFi.RSSI(x));
          delay(100);
      }
  }
  Serial.println("");
  delay(5000);
}

void setup() {
  Serial.begin(115200);
  delay(10);

  // We start by connecting to a WiFi network
  
  WifiScan();
  Serial.println();
  Serial.println();
  Serial.print("Connecting to ");
  Serial.println(ssid);
  
  WiFi.begin(ssid, password);
  
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }

  Serial.println("");
  Serial.println("WiFi connected");  
  Serial.println("IP address: ");
  Serial.println(WiFi.localIP());

  delay(5000);
  

  // Connect to the websocket server
  if (client.connect(host, 8080)) {
    Serial.println("Connected");
  } else {
    Serial.println("Connection failed.");
    while(1) {
      // Hang on failure
    }
  }

  // Handshake with the server Server와 연결 시작. 
  webSocketClient.path = path;
  webSocketClient.host = host;
  
  if (webSocketClient.handshake(client)) {
    Serial.println("Handshake successful");
  } else {
    Serial.println("Handshake failed.");
    while(1) {
      // Hang on failure
    }  
  }

}

double sensor1 = -1;
double sensor2 = -1;
double sensor3 = -1;

void loop() {
  String data; //buffer
  //json format으로 send / recv
  StaticJsonBuffer<200> jsonBuffer;
  JsonObject& root = jsonBuffer.createObject();
  
  if (client.connected()) {
    
    webSocketClient.getData(data); //recv data
    
    if (data.length() > 0) {
      Serial.print("Received data: ");
      Serial.println(data);
    }

    //motor control request or send data request시 처리

    data = "";
    char c;
    
    //일정 주기로 데이터 보내기

     sensor1 += 1.2;
     sensor2 += 1.3;
     sensor3 += 1.4;
     root["sensor1"] = sensor1;
     root["sensor2"] = sensor2;
     root["sensor3"] = sensor3;   
     root.printTo(data);
     Serial.println(data);  
     webSocketClient.sendData(data);
     
     delay(5000);
    
  } else {
    Serial.println("Client disconnected.");
    while (1) {
      // Hang on disconnect.
    }
  }
  
  // wait to fully let the client disconnect
  delay(3000);
  
}
