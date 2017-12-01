#include <WaspXBee802.h>
#include <WaspFrame.h>
#include <WaspSensorEvent_v20.h>


// PAN (Personal Area Network) Identifier
uint8_t  panID[2] = {0x33,0x32}; 

// Define Freq Channel to be set: 
// Center Frequency = 2.405 + (CH - 11d) * 5 MHz
//   Range: 0x0B - 0x1A (XBee)
//   Range: 0x0C - 0x17 (XBee-PRO)
uint8_t  channel = 0xC;
// Define the Encryption mode: 1 (enabled) or 0 (disabled)
uint8_t encryptionMode = 0;

// Define the AES 16-byte Encryption Key
char  encryptionKey[] = "WaspmoteLinkKey!"; 

// Destination MAC address
//////////////////////////////////////////
char RX_ADDRESS[] = "0013A200406937A2";
//////////////////////////////////////////

// Define the Waspmote ID
char WASPMOTE_ID[] = "NODE_1";

uint8_t error;

int len = 5;

float value;




void setup()
{
  // open USB port
  USB.ON();

  USB.println(F("-------------------------------"));
  USB.println(F("Configure XBee 802.15.4"));
  USB.println(F("-------------------------------"));

  frame.setID( WASPMOTE_ID );

  // init XBee 
  xbee802.ON();


  /////////////////////////////////////
  // 1. set channel 
  /////////////////////////////////////
  xbee802.setChannel( channel );
  xbee802.setNodeIdentifier("NODE1");
  
  // check at commmand execution flag
  if( xbee802.error_AT == 0 ) 
  {
    USB.print(F("1. Channel set OK to: 0x"));
    USB.printHex( xbee802.channel );
    USB.println();
  }
  else 
  {
    USB.println(F("1. Error calling 'setChannel()'"));
  }


  /////////////////////////////////////
  // 2. set PANID
  /////////////////////////////////////
  xbee802.setPAN( panID );

  // check the AT commmand execution flag
  if( xbee802.error_AT == 0 ) 
  {
    USB.print(F("2. PAN ID set OK to: 0x"));
    USB.printHex( xbee802.PAN_ID[0] ); 
    USB.printHex( xbee802.PAN_ID[1] ); 
    USB.println();
  }
  else 
  {
    USB.println(F("2. Error calling 'setPAN()'"));  
  }

  /////////////////////////////////////
  // 3. set encryption mode (1:enable; 0:disable)
  /////////////////////////////////////
  xbee802.setEncryptionMode( encryptionMode );

  // check the AT commmand execution flag
  if( xbee802.error_AT == 0 ) 
  {
    USB.print(F("3. AES encryption configured (1:enabled; 0:disabled):"));
    USB.println( xbee802.encryptMode, DEC );
  }
  else 
  {
    USB.println(F("3. Error calling 'setEncryptionMode()'"));
  }

  /////////////////////////////////////
  // 4. set encryption key
  /////////////////////////////////////
  //xbee802.setLinkKey( encryptionKey );

  // check the AT commmand execution flag
  if( xbee802.error_AT == 0 ) 
  {
    USB.println(F("4. AES encryption key set OK"));
  }
  else 
  {
    USB.println(F("4. Error calling 'setLinkKey()'")); 
  }

  /////////////////////////////////////
  // 5. write values to XBee module memory
  /////////////////////////////////////
  xbee802.writeValues();

  // check the AT commmand execution flag
  if( xbee802.error_AT == 0 ) 
  {
    USB.println(F("5. Changes stored OK"));
  }
  else 
  {
    USB.println(F("5. Error calling 'writeValues()'"));   
  }

  USB.println(F("-------------------------------")); 
  
   // Turn on the sensor board
  SensorEventv20.ON();

  // Turn on the RTC
  RTC.ON();

  // Enable interruptions from the board
  SensorEventv20.attachInt();

}



void loop()
{

  /////////////////////////////////////
  // 1. get channel 
  /////////////////////////////////////
  xbee802.getChannel();
  USB.print(F("channel: "));
  USB.printHex(xbee802.channel);
  USB.println();

  /////////////////////////////////////
  // 2. get PANID
  /////////////////////////////////////
  xbee802.getPAN();
  USB.print(F("panid: "));
  USB.printHex(xbee802.PAN_ID[0]); 
  USB.printHex(xbee802.PAN_ID[1]); 
  USB.println(); 

  /////////////////////////////////////
  // 3. get encryption mode (1:enable; 0:disable)
  /////////////////////////////////////
  xbee802.getEncryptionMode();
  USB.print(F("encryption mode: "));
  USB.printHex(xbee802.encryptMode);
  USB.println(); 

  USB.println(F("-------------------------------")); 


   ///////////////////////////////////////////
  // 1. Create ASCII frame
  ///////////////////////////////////////////  

  // create new frame
  frame.createFrame(ASCII);  
  
  
  value = SensorEventv20.readValue(SENS_SOCKET2);
  // Print the info
  USB.print(F("Sensor output: "));    
  USB.print(value);
  USB.println(F(" Volts"));

  // add frame fields
  
  if(value>3){
      frame.addSensor(SENSOR_STR, "closed");  
  }else {
    frame.addSensor(SENSOR_STR, "opened");  
  }
  ///////////////////////////////////////////
  // 2. Send packet
  ///////////////////////////////////////////  

  // send XBee packet
  error = xbee802.send( RX_ADDRESS, frame.buffer, frame.length );   
  
  // check TX flag
  if( error == 0 )
  {
    USB.println(F("send ok"));
    
    // blink green LED
    Utils.blinkGreenLED();
    
  }
  else 
  {
    USB.println(F("send error"));

    // blink red LED
    Utils.blinkRedLED();
  }
    
      ///////////////////////////////////////
  // 2. Go to deep sleep mode  
  ///////////////////////////////////////
  xbee802.OFF();
  USB.println(F("enter deep sleep"));
  PWR.deepSleep("00:00:05:00",RTC_OFFSET,RTC_ALM1_MODE1,SOCKET0_OFF);
  USB.ON();
  USB.println(F("wake up\n"));
  xbee802.ON();
    ///////////////////////////////////////
  // 3. Check Interruption Flags
  ///////////////////////////////////////
  
  // 3.1. Check interruption from Sensor Board
  if(intFlag & SENS_INT)
  {
    interrupt_function();
  }

  // 3.2. Check interruption from RTC alarm
  if( intFlag & RTC_INT )
  {   
    USB.println(F("-----------------------------"));
    USB.println(F("RTC INT captured"));
    USB.println(F("-----------------------------"));
  
    // clear flag
    intFlag &= ~(RTC_INT);
  }
    
    
  

  delay(1000);
}

void interrupt_function()
{  
  // Disable interruptions from the board
  SensorEventv20.detachInt();

  // Load the interruption flag
  SensorEventv20.loadInt();  

  // In case the interruption came from socket 2
  if( SensorEventv20.intFlag & SENS_SOCKET2)
  {
    USB.println(F("-----------------------------"));
    USB.println(F("Interruption from socket 2"));
    USB.println(F("-----------------------------"));
  }

  // Printing and enabling interruptions
  USB.println(F("Windows state changed!\n"));

  // Clean the interruption flag
  intFlag &= ~(SENS_INT);

  // Enable interruptions from the board
  SensorEventv20.attachInt();

}

