package hr.tel.fer.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class WeatherCheck {
	
	private static boolean raining;
	
	public static boolean isRaining(){
		return WeatherCheck.raining;
	}
	



	public static void getLatest(String urlString, String code) {
        try {
              URL url = new URL (urlString + "/get?what=" + code + "&hours=" + "latest-1");
              HttpURLConnection conn;
             
              conn = (HttpURLConnection) url.openConnection();
              conn.setRequestMethod("GET");
              int responseCode = conn.getResponseCode();
              System.out.println("Sending GET request to " + url);
              System.out.println("response code: " + responseCode);
             
              BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
              String inputLine;
              StringBuffer response = new StringBuffer();
             
              while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
              }
              in.close();
             
              //print result
              System.out.println("Weather: "+response.toString());
             
             if(response.toString().equals("0")){
            	 raining = false;
             } else {
            	 raining = true;
             }
             
        } catch (MalformedURLException e) {
              // TODO Auto-generated catch block
              e.printStackTrace();
        } catch (IOException e) {
              // TODO Auto-generated catch bloc
              e.printStackTrace();
        }
	 }

}
