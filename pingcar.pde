//char* strin="Bienvenido\n";
int a=0;
u8 dato=0;
void stop();
void setup(){
 pinMode(0,OUTPUT);
	pinMode(1,OUTPUT);
	pinMode(2,OUTPUT);
	pinMode(3,OUTPUT);
	pinMode(4,OUTPUT);
	pinMode(5,OUTPUT);
	Serial.begin(9600);
	//Serial.printf(strin);
	stop();
	pinMode(7,INPUT);
}
void dere(){
//stop();
	digitalWrite(0,HIGH);
	digitalWrite(3,HIGH);

	delay(10);
	

}
void atras(){
//stop();

	digitalWrite(1,HIGH);
	digitalWrite(3,HIGH);
	delay(10);

}

void stop(){
	digitalWrite(0,LOW);
	digitalWrite(1,LOW);
	digitalWrite(2,LOW);
	digitalWrite(3,LOW);

	delay(10);
	
}
void izq(){
//stop();

	digitalWrite(1,HIGH);
	digitalWrite(2,HIGH);// LED INDICADOR

	delay(10);
	}
void adelante(){
//stop();

	digitalWrite(0,HIGH);//*******************
	
	digitalWrite(2,HIGH);
	delay(10);
}


void loop(){
	
while(Serial.available()){
 dato=Serial.read();

	
		 switch(dato){
		 case 97: //a
				stop();
				adelante();

				break;
			case 98: //b
				stop();
				atras();
				break;
			
			case 99: //c
				stop();
				
      dere();
				break;
			case 100: //d
				stop();
      izq();
				break;
		
			case 101: //e
				stop();
				break;
					 
		 }
	
 }

}