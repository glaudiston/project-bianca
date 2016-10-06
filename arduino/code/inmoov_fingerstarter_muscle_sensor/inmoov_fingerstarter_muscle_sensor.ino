int muscle;

//servo library
#include <Servo.h> 
 
Servo myservo;  // create servo object to control a servo 
 
int val;    // variable to read the value from the analog pin 
 
void setup() 
{ 
  myservo.attach(2);  // attaches the servo on pin 9 to the servo object 
  Serial.begin(57600);
  pinMode(A0,INPUT);  
    myservo.write(0);
} 
 
void loop() 
{ 

  
  //muscle
muscle=analogRead(A0);
Serial.println(muscle);
  
  //action of the finger
  if(muscle>79)
  {
  kikoo();
  }
  else{
  myservo.write(50);
  delay(100);
    }
  
} 
//action of the finger : the last point is without tension (90 degrees).
void kikoo()
{

  myservo.write(100);
  delay(100);    
}
  
