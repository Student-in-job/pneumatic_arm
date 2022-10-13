#define input   8
#define output  9
#define led     12
int analogPin = A3;

float kPa;
int val = 0;
int range = 100;
float maxErr = 0.35;
float minPSI = 1.3;
String inputString;
bool change = false;

void setup() {
  Serial.begin(9600);
  pinMode(input, OUTPUT);
  pinMode(output, OUTPUT);
  pinMode(led, OUTPUT);
  inputString = "";
  digitalWrite(led, HIGH);
  digitalWrite(input, LOW);
  digitalWrite(output, LOW);
  kPa = 2.5;
  change = false;
}

void loop() {
  val = analogRead(analogPin);  // read the input pin
  while (Serial.available()) {
    char inChar = (char)Serial.read();
    // if the incoming character is not "#", set a flag so the main loop can
    if (inChar == '#') {
      inputString += (char)Serial.read();
      inputString += (char)Serial.read();
      inputString += (char)Serial.read();
      inputString += (char)Serial.read();
      int value = inputString.toInt();
      kPa = float(value) / 100.0;
      Serial.println(kPa);
      inputString = "";
      change = true;
    }
  }
  float pressure = getPressure(val);
  if(change)
  {
    if(kPa > pressure)
    {
      //digitalWrite(input, HIGH);
      while(true)
      {
        delay(100);
        val = analogRead(analogPin);
        pressure = getPressure(val);
        if(abs(kPa - pressure) < maxErr)
          break;
        if (kPa > pressure)
          up();
        else
          down();
        //Serial.println(pressure);
      }
      //digitalWrite(input, LOW);
      change = false;
    }
    else
    {
      digitalWrite(output, HIGH);
      delay(3000);
      digitalWrite(output, LOW);
      kPa = 0.1;
      change = false;
    }
  }
  else
  {
    //Serial.print(kPa);
    //Serial.print("\t");
    //Serial.print(pressure);
    //Serial.println();
  }
  delay(200);
}

void some()
{
  digitalWrite(input, HIGH);
  delay(20);
  digitalWrite(input, LOW);
  delay(50);
}

void up()
{
  digitalWrite(input, HIGH);
  delay(10);
  digitalWrite(input, LOW);
  delay(50);
}

void down()
{
  digitalWrite(output, HIGH);
  delay(7);
  digitalWrite(output, LOW);
  delay(50);
}

float getPressure(int value){
  float voltage = value * 5.0 / 1024;
  float pres = (voltage - 0.49) * 7 * range / 4 / 5.8;
  return pres;
}
