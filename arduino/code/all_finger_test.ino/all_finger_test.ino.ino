/* Sweep
 by BARRAGAN <http://barraganstudio.com>
 This example code is in the public domain.

 modified 8 Nov 2013
 by Scott Fitzgerald
 http://www.arduino.cc/en/Tutorial/Sweep
*/

#include <Servo.h>

Servo servoFingerOne;  // create servo object to control a servo
Servo servoFingerTwo;  // create servo object to control a servo
Servo servoFingerThree;  // create servo object to control a servo
Servo servoFingerFour;  // create servo object to control a servo
Servo servoFingerFive;  // create servo object to control a servo
// twelve servo objects can be created on most boards

int pos = 0;    // variable to store the servo position

void setup() {
  servoFingerOne.attach(8);  // attaches the servo on pin 9 to the servo object
  servoFingerTwo.attach(9);  // attaches the servo on pin 9 to the servo object
  servoFingerThree.attach(10);  // attaches the servo on pin 9 to the servo object
  servoFingerFour.attach(11);  // attaches the servo on pin 9 to the servo object
  servoFingerFive.attach(12);  // attaches the servo on pin 9 to the servo object
}

void loop() {
  /*
  for (pos = 0; pos <= 180; pos += 10) { // goes from 0 degrees to 180 degrees
    // in steps of 1 degree
    servoFingerOne.write(pos);              // tell servo to go to position in variable 'pos'
    servoFingerTwo.write(pos);              // tell servo to go to position in variable 'pos'
    servoFingerThree.write(pos);              // tell servo to go to position in variable 'pos'
    servoFingerFour.write(pos);              // tell servo to go to position in variable 'pos'
    servoFingerFive.write(pos);              // tell servo to go to position in variable 'pos'
    delay(15);                       // waits 15ms for the servo to reach the position
  }
  for (pos = 180; pos >= 0; pos -= 10) { // goes from 180 degrees to 0 degrees
    servoFingerOne.write(pos);              // tell servo to go to position in variable 'pos'
    delay(15);                       // waits 15ms for the servo to reach the position
  }
  */
}
