/* Sweep
 by BARRAGAN <http://barraganstudio.com>
 This example code is in the public domain.

 modified 8 Nov 2013
 by Scott Fitzgerald
 http://www.arduino.cc/en/Tutorial/Sweep
*/

#include <Servo.h>
#include <SoftwareSerial.h>
SoftwareSerial bluetooth(6,5); // RX, TX


Servo servoFingerOne;  // create servo object to control a servo
Servo servoFingerTwo;  // create servo object to control a servo
Servo servoFingerThree;  // create servo object to control a servo
Servo servoFingerFour;  // create servo object to control a servo
Servo servoFingerFive;  // create servo object to control a servo
// twelve servo objects can be created on most boards

int pos = 0;    // variable to store the servo position
int START_SERVO_DELAY=500;
void setup() {
  Serial.begin(57600);
  Serial.println("Project-Bianca initializing...");
  Serial.print("Starting BlueTooth HC-05...");
  bluetooth.begin(9600);
  Serial.println("Done.");
  Serial.print("Starting Servo Finger One...");
  servoFingerOne.attach(9);  // attaches the servo on pin 9 to the servo object
  Serial.println("Done.");
  delay(START_SERVO_DELAY);
  Serial.print("Starting Servo Finger Two...");
  servoFingerTwo.attach(13);  // attaches the servo on pin 9 to the servo object
  Serial.println("Done.");
  delay(START_SERVO_DELAY);
  Serial.print("Starting Servo Finger Three...");
  servoFingerThree.attach(10);  // attaches the servo on pin 9 to the servo object
  Serial.println("Done.");
  delay(START_SERVO_DELAY);
  Serial.print("Starting Servo Finger Four...");
  servoFingerFour.attach(11);  // attaches the servo on pin 9 to the servo object
  Serial.println("Done.");
  delay(START_SERVO_DELAY);
  Serial.print("Starting Servo Finger Five...");
  servoFingerFive.attach(12);  // attaches the servo on pin 9 to the servo object
  Serial.println("Done.");
  Serial.println("Project-Bianca is ready to work.");
}

void loop() {
    while (bluetooth.available()) {
      Serial.print("BlueTooth input detected... ");
      char btVal = bluetooth.read();
      Serial.print(btVal);
      Serial.print("\n");
      servoFingerOne.write(btVal);
  }
  if (Serial.available()) {
    bluetooth.write(Serial.read());
  }
//  for (pos = 0; pos <= 180; pos += 10) { // goes from 0 degrees to 180 degrees
//    // in steps of 1 degree
//    servoFingerOne.write(pos);              // tell servo to go to position in variable 'pos'
//    servoFingerTwo.write(pos);              // tell servo to go to position in variable 'pos'
//    servoFingerThree.write(pos);              // tell servo to go to position in variable 'pos'
//    servoFingerFour.write(pos);              // tell servo to go to position in variable 'pos'
//    servoFingerFive.write(pos);              // tell servo to go to position in variable 'pos'
//    delay(15);                       // waits 15ms for the servo to reach the position
//  }
//  for (pos = 180; pos >= 0; pos -= 10) { // goes from 180 degrees to 0 degrees
//    servoFingerOne.write(pos);              // tell servo to go to position in variable 'pos'
//    delay(15);                       // waits 15ms for the servo to reach the position
//  }
}
