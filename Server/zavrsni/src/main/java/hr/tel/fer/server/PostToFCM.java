package hr.tel.fer.server;

/**
 * Created by lbunicic on 23/03/2017.
 */
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import org.json.JSONObject;


public class PostToFCM {

    private final static String apiKey = "AAAAIU5YSDM:APA91bHHmqprqTHMQgTXQap28JZAJWO7yJHN8crHsHZrvCvwQb7SO127GlNBR-Dy1NI5ue-ZRTZ_bDTnyWVUvEwygHhPwK3rVgub2PDjBmVKpt0hzhhBehGjWEfPaxiDZJfsFlDN9_43";

    public static void post(JSONObject json){

        try{

            //URL
            URL url = new URL("https://fcm.googleapis.com/fcm/send");

            //Open connection
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            //POST
            conn.setRequestMethod("POST");

            //headers, authorization - our FCM server key
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "key=" + apiKey);

            conn.setDoOutput(true);

            //Add JSON data into POST request body

            //Get connection output stream
            DataOutputStream wr = new DataOutputStream(conn.getOutputStream());

            // 5.3 Copy Content "JSON" into
            //mapper.writeValue(wr, content);

            //Proba
            System.out.println(json.toString());

            wr.writeBytes(json.toString());
            // 5.4 Send the request
            wr.flush();

            // 5.5 close
            wr.close();

            // 6. Get the response
            int responseCode = conn.getResponseCode();
            System.out.println("\nSending 'POST' request to URL : " + url);
            System.out.println("Response Code : " + responseCode);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // 7. Print result
            System.out.println(response.toString());

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}