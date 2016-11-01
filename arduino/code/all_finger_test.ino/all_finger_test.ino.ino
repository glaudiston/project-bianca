#include <Servo.h>

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

void setup()
{
  servoFingerOne.attach(7);  // attaches the servo on pin 9 to the servo object
  servoFingerTwo.attach(8);  // attaches the servo on pin 9 to the servo object
  servoFingerThree.attach(11);  // attaches the servo on pin 9 to the servo object
  servoFingerFour.attach(10);  // attaches the servo on pin 9 to the servo object
  servoFingerFive.attach(9);  // attaches the servo on pin 9 to the servo object
}

void loop()
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

