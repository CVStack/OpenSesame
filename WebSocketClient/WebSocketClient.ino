#include <WebSocketClient.h>
#include <WiFi.h>
#include <ArduinoJson.h>

const char* ssid     = "";
const char* password = "";

char path[] = "/WebServerTest/WebSocket/WebSocket";
char host[] = "";
  
WebSocketClient webSocketClient;
StaticJsonBuffer<200> jsonBuffer;

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
    String data;
    jsonBuffer.clear();
    JsonObject& root = jsonBuffer.createObject();

    root["type"] = "Arduino";
    root["MAC"] = "C8:2B:96:8E:EE:2C"; //WiFi.macAddress();
    
    root.printTo(data);
    webSocketClient.sendData(data); // 처음 연결할때 자신의 MAC주소 보냄
    
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
  
  if (client.connected()) {
    
    webSocketClient.getData(data); //recv data
    int wait_count = 0;
    while(true) {
      
      if (data.length() > 0) { //데이터를 수신했을 떄
        Serial.print("Received data: ");
        Serial.println(data);
        break;
      }

      else if(wait_count >= 30){ //30초가 지났을때
        break;  
      }
      
      else {
        delay(1000); // 1초대기
        wait_count += 1;  
      }  
    }

    data = "";
    char c;
    jsonBuffer.clear();
    JsonObject& root = jsonBuffer.createObject();
    
    //일정 주기로 데이터 보냄
    
     sensor1 += 1.2;
     sensor2 += 1.3;
     sensor3 += 1.4;
     root["type"] = "Arduino";
     root["MAC"] = "C8:2B:96:8E:EE:2C"; //WiFi.macAddress();
     JsonObject& sensors = jsonBuffer.createObject();
     sensors["sensor1"] = sensor1;
     sensors["sensor2"] = sensor2;
     sensors["sensor3"] = sensor3;
     root["sensors"] = sensors;
        
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
  
}
