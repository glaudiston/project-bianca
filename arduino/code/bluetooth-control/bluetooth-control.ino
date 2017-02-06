/**
 * This code aims to control a complete inmoov arm/hand using just 
 * a Arduino Uno (ATMEGA328P-PU) and a shift register 74HC595N
 * controlling 7 servos TowerPro (MG995)
 * via bluetoot using a HC-05
 */
#include <Servo.h>
#include <SoftwareSerial.h>
SoftwareSerial bluetooth(6,5); // RX, TX

Servo servoFingerOne;  // create servo object to control a servo
Servo servoFingerTwo;  // create servo object to control a servo
Servo servoFingerThree;  // create servo object to control a servo
Servo servoFingerFour;  // create servo object to control a servo
Servo servoFingerFive;  // create servo object to control a servo
Servo servoTwrist;  // create servo object to control a servo
Servo servoElbow;  // create servo object to control a servo
// twelve servo objects can be created on most boards
  
int pos = 0;    // variable to store the servo position
int START_SERVO_DELAY=500;
void setup() {
//  TIMSK0 = 0;
  Serial.begin(57600);
  Serial.println("Project-Bianca initializing...");
  Serial.print("Starting BlueTooth HC-05.  ..");
  bluetooth.begin(9600);
  Serial.println("Done.");
  Serial.print("Starting Servo Finger One...");
  servoFingerOne.attach(9);  // attaches the servo on pin 9 to the servo object
  Serial.println("Done.");
  delay(START_SERVO_DELAY);
  Serial.print("Starting Servo Finger Two...");
  servoFingerTwo.attach(8);  // attaches the servo on pin 12 to the servo object
  Serial.println("Done.");
  delay(START_SERVO_DELAY);
  Serial.print("Starting Servo Finger Three...");
  servoFingerThree.attach(10);  // attaches the servo on pin 10 to the servo object
  Serial.println("Done.");
  delay(START_SERVO_DELAY);
  Serial.print("Starting Servo Finger Four...");
  servoFingerFour.attach(11);  // attaches the servo on pin 11 to the servo object
  Serial.println("Done.");
  delay(START_SERVO_DELAY);
  Serial.print("Starting Servo Finger Five...");
  servoFingerFive.attach(12);  // attaches the servo on pin 12 to the servo object
  Serial.println("Done.");
//  Serial.print("Starting Servo Twrist...");
//  servoTwrist.attach(13);  // attaches the servo on pin 13 to the servo object
//  Serial.println("Done.");
  Serial.println("Project-Bianca is ready to work.");
}

void loop() {
    while (bluetooth.available()) {
      Serial.print("BlueTooth input detected... ");
      char btVal;
      while( ( btVal = bluetooth.read() ) == -1 );
      char bf[4];
      sprintf(bf, "%i\0", btVal);
      Serial.print(bf);
      Serial.print(", to ");
      char btVal2;
      while( ( btVal2 = bluetooth.read() ) == -1 );
      sprintf(bf, "%i\0", btVal2);
      Serial.print(bf);
      Serial.print("\n");
      // TODO implement a array of servos to a better code
      if ( btVal == 1 ) {
        servoFingerOne.write(btVal2);
      }
      if ( btVal == 2 ) {
        servoFingerTwo.write(btVal2);
      }
      if ( btVal == 3 ) {
        servoFingerThree.write(btVal2);
      }
      if ( btVal == 4 ) {
        servoFingerFour.write(btVal2);
      }
      if ( btVal == 5 ) {
        servoFingerFive.write(btVal2);
      }
      
  }
  if (Serial.available()) {
    bluetooth.write(Serial.read());
  }
}

