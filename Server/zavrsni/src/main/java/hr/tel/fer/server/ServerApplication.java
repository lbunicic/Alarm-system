package hr.tel.fer.server;


import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.digi.xbee.api.XBeeDevice;
import com.digi.xbee.api.exceptions.XBeeException;

@SpringBootApplication
public class ServerApplication {
	
	public static void main(String[] args) {
		
		SpringApplication.run(ServerApplication.class, args);
		
	}
	
	
	@Bean
  	public CommandLineRunner runServer(WindowRepository repository) {
  	       return (args) -> {
  	    	 try {
	  				TimeUnit.SECONDS.sleep(5);
	  			} catch (InterruptedException e) {
	  				e.printStackTrace();
	  			}
	  			
	  			// Instantiate an XBee device object.
	  			XBeeDevice myXBeeDevice = new XBeeDevice(args[0], 115200);
	  			try {
	  				myXBeeDevice.open();
	  		    } catch (XBeeException e) {
	  				e.printStackTrace();
	  				e.getLocalizedMessage();	
	  			}
	  			
	  			// Create the data reception listener.
	  			XBeeListener myDataReceiveListener = new XBeeListener(repository);
	  			
	  			// Subscribe to data reception.
	  			myXBeeDevice.addDataListener(myDataReceiveListener);
	  			
	  		
	  			while(true){
	  				try {
	  					TimeUnit.MINUTES.sleep(5);
	  				} catch (InterruptedException e) {
	  					e.printStackTrace();
	  				}
	  				WeatherCheck.getLatest("http://161.53.19.110:2000", "weatherpluv");
	  				
	  				ArrayList<Window> openedList = (ArrayList<Window>) repository.findOpened();
	  				
	  
	  				if(openedList.size() > 0 && WeatherCheck.isRaining()){
                        StringBuffer response = new StringBuffer();
	  					for(Window window : openedList){
	  						response.append(window.getName()+" ");
	  					}
	  					
	  					 String notificationMessage = response.toString();
	  				        JSONObject notificationBody=new JSONObject();
	  				        try {
	  				            notificationBody.put("to","/topics/hitne_obavjesti");
	  				            notificationBody.put("data", new JSONObject().put("body", notificationMessage));
	  				
	  				        } catch (JSONException e) {
	  				            e.printStackTrace();
	  				        }
	  				        PostToFCM.post(notificationBody);
	  				}
	
	  			}
  	         
  	       };
	}
}
