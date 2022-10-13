#define led_input  8
#define led_output 9
#define led        11
/*
  Open & Close

  Turns an LED on for one second, then off for one second, repeatedly.

*/

bool stringComplete = false;
String inputString;

// the setup function runs once when you press reset or power the board
void setup() {
  // initialize digital pin LED_BUILTIN as an output.
  Serial.begin(9600);
  //Serial.println("Start");
  pinMode(led_input, OUTPUT);
  pinMode(led_output, OUTPUT);
  pinMode(led, OUTPUT);
  digitalWrite(led_input, LOW); 
  //digitalWrite(led_output, HIGH);
  //delay(2000);
  digitalWrite(led_output, LOW);
  inputString = "";
  digitalWrite(led, HIGH);
}

// the loop function runs over and over again forever
void loop() {
  Serial.print(".");
  char code = 0;
  int open_time = 0;
  while (Serial.available()) {
    char inChar = (char)Serial.read();
    // if the incoming character is a newline, set a flag so the main loop can
    // do something about it:
    if (inChar == '#') {
        stringComplete = true;
        code = (char)Serial.read();
        inputString += (char)Serial.read();
        inputString += (char)Serial.read();
        inputString += (char)Serial.read();
        inputString += (char)Serial.read();
        open_time = inputString.toInt();
    }
   }
   if(stringComplete){
    if (code == '1'){
      Serial.println("1");
      Serial.print("Open time: ");
      Serial.print(open_time);
      digitalWrite(led_input, HIGH);   // turn the LED on (HIGH is the voltage level)
      delay(open_time);                 // wait for a 2 second
      digitalWrite(led_input, LOW);    // turn the LED off by making the voltage LOW
    }
    else
    {
      Serial.println("0");
      digitalWrite(led_output, HIGH);   // turn the LED on (HIGH is the voltage level)
      delay(2500);                   // wait for a 2 second
      digitalWrite(led_output, LOW);
      }
    stringComplete = false;
    inputString = "";
  }
  delay(1200);                       // wait for a second
}
