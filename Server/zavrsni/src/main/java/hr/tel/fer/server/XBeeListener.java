package hr.tel.fer.server;

import java.util.ArrayList;


import org.json.JSONException;
import org.json.JSONObject;


import com.digi.xbee.api.listeners.IDataReceiveListener;
import com.digi.xbee.api.models.XBeeMessage;

 

public class XBeeListener implements IDataReceiveListener {
	
	WindowRepository repo;
	

	public XBeeListener(WindowRepository repo) {
		super();
		this.repo = repo;
	}


	public void dataReceived(XBeeMessage xbeeMessage) {
		

		//************************1. PRIMITAK PORUKE XBEE************************
		String macAddress = xbeeMessage.getDevice().get64BitAddress().toString(); 
		String dataString = xbeeMessage.getDataString();
		System.out.println("Received data from " + macAddress +
		": " + dataString);	
		
       boolean isOpened;
		if(dataString.contains("opened")){
			isOpened = true;
		} else {
			isOpened = false;
		}
		
		//************************2. PROVIJERI POSTOJI LI MAC U BAZI -> DODAVANJE U BAZU*************************
     
        if(repo.countById(macAddress)!=0){
           System.out.println("Pošiljatelj "+macAddress+ " poznat");
        } else{
            System.out.println("Pošiljatelj "+macAddress+" nepoznat, dodajem u bazu");
            repo.save(new Window(macAddress));
        }
       
		//************************3. AŽURIRAJ STANJE PROZORA i INFO o vremenu***********************
		
        
      
        
       //System.out.println(window.toString());
        Window window = repo.findById(macAddress).get(0);
        window.setState(isOpened);
        repo.save(window);
        
		//************************4. AKO PADA KIŠA && OTVOREN PROZOR -> FCM Alarm***********************
		
		ArrayList<Window> openedList = (ArrayList<Window>) repo.findOpened();
		if(openedList.size() > 0 && WeatherCheck.isRaining()){
          
			StringBuffer response = new StringBuffer();
			for(Window window2 : openedList){
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
	
}
	
	
	 
