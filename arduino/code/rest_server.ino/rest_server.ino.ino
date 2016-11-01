#include <Adafruit_CC3000.h>
#include <SPI.h>
#include "utility/debug.h"
#include "utility/socket.h"
#include <Servo.h>

// These are the interrupt and control pins
#define ADAFRUIT_CC3000_IRQ   3  // MUST be an interrupt pin!
// These can be any two pins
#define ADAFRUIT_CC3000_VBAT  5
#define ADAFRUIT_CC3000_CS    10
// Use hardware SPI for the remaining pins
// On an UNO, SCK = 13, MISO = 12, and MOSI = 11
Adafruit_CC3000 cc3000 = Adafruit_CC3000(ADAFRUIT_CC3000_CS, ADAFRUIT_CC3000_IRQ, ADAFRUIT_CC3000_VBAT,
                                         SPI_CLOCK_DIVIDER); // you can change this clock speed

Servo servoFingerOne;  // create servo object to control a servo
Servo servoFingerTwo;  // create servo object to control a servo
Servo servoFingerThree;  // create servo object to control a servo
Servo servoFingerFour;  // create servo object to control a servo
Servo servoFingerFive;  // create servo object to control a servo
// twelve servo objects can be created on most boards

int pos = 0;    // variable to store the servo position
int min_angle=30;
int max_angle=110;
int step_angle=10;
int delay_ms=180;

#define WLAN_SSID       "GOMES HOUSE"           // cannot be longer than 32 characters!
#define WLAN_PASS       "5a471dad35"
// Security can be WLAN_SEC_UNSEC, WLAN_SEC_WEP, WLAN_SEC_WPA or WLAN_SEC_WPA2
#define WLAN_SECURITY   WLAN_SEC_WPA2

#define LISTEN_PORT           80    // What TCP port to listen on for connections.  The echo protocol uses port 80.

Adafruit_CC3000_Server httpServer(LISTEN_PORT);

#define USE_DHCP
#define MAX_ACTION            10      // Maximum length of the HTTP action that can be parsed.
#define MAX_PATH              64      // Maximum length of the HTTP request path that can be parsed.
#define BUFFER_SIZE           MAX_ACTION + MAX_PATH + 20  // Size of buffer for incoming request data.
                                                          // Since only the first line is parsed this
                                                          // needs to be as large as the maximum action
                                                          // and path plus a little for whitespace and
                                                          // HTTP version.
#define TIMEOUT_MS            500    // Amount of time in milliseconds to wait for
                                     // an incoming request to finish.  Don't set this
                                     // too high or your server could be slow to respond.
char path[MAX_PATH+1];
uint8_t buffer[BUFFER_SIZE+1];
int bufindex = 0;
char action[MAX_ACTION+1];


void setup(void)
{
  Serial.begin(115200);
  Serial.println(F("Project-Bianca Initializing...\n"));

  Serial.print("Free RAM: "); Serial.println(getFreeRam(), DEC);

  /* Initialise the module */
  if (!cc3000.begin())
  {
    Serial.println(F("Couldn't begin()! Check your wiring?"));
    while(1);
  }
  
  Serial.println(F("Arduino UNO, CC3000 Shield!\n")); 
  Serial.print(F("\nAttempting to connect to ")); Serial.println(WLAN_SSID);
  if (!cc3000.connectToAP(WLAN_SSID, WLAN_PASS, WLAN_SECURITY)) {
    Serial.println(F("Failed!"));
    while(1);
  }
   
  Serial.println(F("Connected!"));

#ifdef USE_DHCP
  Serial.println(F("Request DHCP"));
  while (!cc3000.checkDHCP())
  {
    delay(100); // ToDo: Insert a DHCP timeout!
  }
  /* Display the IP address DNS, Gateway, etc. */  
  while (! displayConnectionDetails()) {
    delay(1000);
  }

#else
/* Optional: Set a static IP address instead of using DHCP.
     Note that the setStaticIPAddress function will save its state
     in the CC3000's internal non-volatile memory and the details
     will be used the next time the CC3000 connects to a network.
     This means you only need to call the function once and the
     CC3000 will remember the connection details.  To switch back
     to using DHCP, call the setDHCP() function (again only needs
     to be called once).
  */
  uint32_t ipAddress = cc3000.IP2U32(192, 168, 1, 15);
  uint32_t netMask = cc3000.IP2U32(255, 255, 255, 0);
  uint32_t defaultGateway = cc3000.IP2U32(192, 168, 1, 1);
  uint32_t dns = cc3000.IP2U32(8, 8, 4, 4);
  if (!cc3000.setStaticIPAddress(ipAddress, netMask, defaultGateway, dns)) {
    Serial.println(F("Failed to set static IP!"));
    while(1);
  }
#endif

  /*********************************************************/
  /* You can safely remove this to save some flash memory! */
  /*********************************************************/
  Serial.println(F("\r\nNOTE: This sketch may cause problems with other sketches"));
  Serial.println(F("since the .disconnect() function is never called, so the"));
  Serial.println(F("AP may refuse connection requests from the CC3000 until a"));
  Serial.println(F("timeout period passes.  This is normal behaviour since"));
  Serial.println(F("there isn't an obvious moment to disconnect with a server.\r\n"));
  
  // Start listening for connections
  httpServer.begin();
  
  Serial.println(F("Listening for connections..."));
  
  Serial.println(F("\nInitializing Servos..."));
  servoFingerOne.attach(7);  // attaches the servo on pin 9 to the servo object
  servoFingerTwo.attach(8);  // attaches the servo on pin 9 to the servo object
  servoFingerThree.attach(11);  // attaches the servo on pin 9 to the servo object
  servoFingerFour.attach(10);  // attaches the servo on pin 9 to the servo object
  servoFingerFive.attach(9);  // attaches the servo on pin 9 to the servo object
  Serial.println(F("OK!\n"));
}

void hand_close()
{
  for (pos = min_angle; pos <= max_angle; pos += step_angle)   // goes from 0 degrees to 180 degrees
  {
    // in steps of 1 degree
    servoFingerOne.write(pos);              // tell servo to go to position in variable 'pos'
    delay(delay_ms);                       // waits 250ms for the servo to reach the position
    servoFingerTwo.write(pos);              // tell servo to go to position in variable 'pos'
    delay(delay_ms);                       // waits 250ms for the servo to reach the position
    servoFingerThree.write(pos);              // tell servo to go to position in variable 'pos'
    delay(delay_ms);                       // waits 250ms for the servo to reach the position
    servoFingerFour.write(pos);              // tell servo to go to position in variable 'pos'
    delay(delay_ms);                       // waits 250ms for the servo to reach the position
    servoFingerFive.write(pos);              // tell servo to go to position in variable 'pos'
    delay(delay_ms);                       // waits 250ms for the servo to reach the position
  }
}
void hand_open()
{
  for (pos = max_angle; pos >= min_angle; pos -= step_angle)   // goes from 180 degrees to 0 degrees
  {
    // in steps of 1 degree
    servoFingerOne.write(pos);              // tell servo to go to position in variable 'pos'
    delay(delay_ms);                       // waits 250ms for the servo to reach the position
    servoFingerTwo.write(pos);              // tell servo to go to position in variable 'pos'
    delay(delay_ms);                       // waits 250ms for the servo to reach the position
    servoFingerThree.write(pos);              // tell servo to go to position in variable 'pos'
    delay(delay_ms);                       // waits 250ms for the servo to reach the position
    servoFingerFour.write(pos);              // tell servo to go to position in variable 'pos'
    delay(delay_ms);                       // waits 250ms for the servo to reach the position
    servoFingerFive.write(pos);              // tell servo to go to position in variable 'pos'
    delay(delay_ms);                       // waits 250ms for the servo to reach the position
  }
}

void loop(void)
{
    Serial.print(F("."));
  // Try to get a client which is connected.
  Adafruit_CC3000_ClientRef client = httpServer.available();
  if (client) {
    Serial.println(F("Client connected."));
    // Process this request until it completes or times out.
    // Note that this is explicitly limited to handling one request at a time!

    // Clear the incoming data buffer and point to the beginning of it.
    bufindex = 0;
    memset(&buffer, 0, sizeof(buffer));
    
    // Clear action and path strings.
    memset(&action, 0, sizeof(action));
    memset(&path,   0, sizeof(path));

    // Set a timeout for reading all the incoming data.
    unsigned long endtime = millis() + TIMEOUT_MS;
    
    // Read all the incoming data until it can be parsed or the timeout expires.
    bool parsed = false;
    while (!parsed && (millis() < endtime) && (bufindex < BUFFER_SIZE)) {
      if (client.available()) {
        buffer[bufindex++] = client.read();
      }
      parsed = parseRequest(buffer, bufindex, action, path);
    }

    // Handle the request if it was parsed.
    if (parsed) {
      Serial.println(F("Processing request"));
      Serial.print(F("Action: ")); Serial.println(action);
      Serial.print(F("Path: ")); Serial.println(path);
      // Check the action to see if it was a GET request.
      if (strcmp(action, "GET") == 0) {
  hand_close();
  hand_open();
        // Respond with the path that was accessed.
        // First send the success response code.
        client.fastrprintln(F("HTTP/1.1 200 OK"));
        // Then send a few headers to identify the type of data returned and that
        // the connection will not be held open.
        client.fastrprintln(F("Content-Type: text/html"));
        client.fastrprintln(F("Connection: close"));
        client.fastrprintln(F("Server: Arduino Uno CC3000"));
        // Send an empty line to signal start of body.
        client.fastrprintln(F(""));
        // Now send the response data.
        client.fastrprintln(F("<html><head><title>Bianca-Project</title></head>"));
        client.fastrprint(F("Voce acessou o path: ")); client.fastrprintln(path);
        client.fastrprintln(F("</html>"));

      }
      else {
        // Unsupported action, respond with an HTTP 405 method not allowed error.
        client.fastrprintln(F("HTTP/1.1 405 Method Not Allowed"));
        client.fastrprintln(F(""));
      }
    }

    // Wait a short period to make sure the response had time to send before
    // the connection is closed (the CC3000 sends data asyncronously).
    delay(100);

    // Close the connection when done.
    Serial.println(F("Client disconnected"));
    client.close();
  }
}

// Return true if the buffer contains an HTTP request.  Also returns the request
// path and action strings if the request was parsed.  This does not attempt to
// parse any HTTP headers because there really isn't enough memory to process
// them all.
// HTTP request looks like:
//  [method] [path] [version] \r\n
//  Header_key_1: Header_value_1 \r\n
//  ...
//  Header_key_n: Header_value_n \r\n
//  \r\n
bool parseRequest(uint8_t* buf, int bufSize, char* action, char* path) {
  // Check if the request ends with \r\n to signal end of first line.
  if (bufSize < 2)
    return false;
  if (buf[bufSize-2] == '\r' && buf[bufSize-1] == '\n') {
    parseFirstLine((char*)buf, action, path);
    return true;
  }
  return false;
}

// Parse the action and path from the first line of an HTTP request.
void parseFirstLine(char* line, char* action, char* path) {
  // Parse first word up to whitespace as action.
  char* lineaction = strtok(line, " ");
  if (lineaction != NULL)
    strncpy(action, lineaction, MAX_ACTION);
  // Parse second word up to whitespace as path.
  char* linepath = strtok(NULL, " ");
  if (linepath != NULL)
    strncpy(path, linepath, MAX_PATH);
}

/**************************************************************************/
/*!
    @brief  Tries to read the IP address and other connection details
*/
/**************************************************************************/
bool displayConnectionDetails(void)
{
  uint32_t ipAddress, netmask, gateway, dhcpserv, dnsserv;
  
  if(!cc3000.getIPAddress(&ipAddress, &netmask, &gateway, &dhcpserv, &dnsserv))
  {
    Serial.println(F("Unable to retrieve the IP Address!\r\n"));
    return false;
  }
  else
  {
    Serial.print(F("\nIP Addr: ")); cc3000.printIPdotsRev(ipAddress);
    Serial.print(F("\nNetmask: ")); cc3000.printIPdotsRev(netmask);
    Serial.print(F("\nGateway: ")); cc3000.printIPdotsRev(gateway);
    Serial.print(F("\nDHCPsrv: ")); cc3000.printIPdotsRev(dhcpserv);
    Serial.print(F("\nDNSserv: ")); cc3000.printIPdotsRev(dnsserv);
    Serial.println();
    return true;
  }
}



