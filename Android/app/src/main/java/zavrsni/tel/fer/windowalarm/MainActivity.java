package zavrsni.tel.fer.windowalarm;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;

import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.Proxy;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import static android.R.attr.delay;

public class MainActivity extends AppCompatActivity {


    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<Windows>> listDataChild;
    FloatingActionButton fab ;
    FloatingActionButton fab1;
    FloatingActionButton fab2;
    Boolean isFABOpen = false;
    String URL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MyPreference myPreference = MyPreference.getInstance(getApplicationContext());
        URL = myPreference.getData("URL");

        //FIREBASE TOPIC SUBSCRIPTION
        FirebaseMessaging.getInstance().subscribeToTopic("hitne_obavijesti");

        ImageButton infoBtn = (ImageButton) findViewById(R.id.btn_info);
        infoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, InfoActivity.class);
                startActivity(intent);
            }
        });


        final SwipeRefreshLayout swipeView = (SwipeRefreshLayout) findViewById(R.id.swipe);

        swipeView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeView.setRefreshing(true);
                Log.d("Swipe", "Refreshing data");
                ( new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeView.setRefreshing(false);
                        getData();
                    }
                }, 3000);
            }
        });


        //EXPANDABLE LIST VIEW
        expListView = (ExpandableListView) findViewById(R.id.room_list);

        //populating ExpandableListView
        try {
            getData();
        } catch (IndexOutOfBoundsException e) {

        } catch (NullPointerException e) {

        }

        expListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick( AdapterView<?> parent, View view, int position, long id) {

                long packedPosition = expListView.getExpandableListPosition(position);

                int itemType = ExpandableListView.getPackedPositionType(packedPosition);
                int groupPosition = ExpandableListView.getPackedPositionGroup(packedPosition);
                int childPosition = ExpandableListView.getPackedPositionChild(packedPosition);


                if(itemType==ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
                    final Windows window = listAdapter.getChild(groupPosition, childPosition);

                    final Dialog dialog = new Dialog(MainActivity.this);
                    dialog.setContentView(R.layout.alert_dialog_edit);
                    dialog.setTitle("Edit window");

                    final EditText name = (EditText) dialog.findViewById(R.id.edit_name);
                    final EditText room = (EditText) dialog.findViewById(R.id.edit_room);

                    name.setText(window.getName());
                    room.setText(window.getRoomName());

                    Button btnSave = (Button) dialog.findViewById(R.id.btn_save_edit);
                    // if button is clicked, close the custom dialog
                    btnSave.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            putWindow(window, name.getText().toString(), room.getText().toString());
                            dialog.dismiss();
                        }
                    });

                    Button btnDelete = (Button) dialog.findViewById(R.id.btn_delete);

                    btnDelete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            deleteWindow(window.getHref());
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                } else if (itemType==ExpandableListView.PACKED_POSITION_TYPE_GROUP){
                    //final Dialog dialog = new Dialog(MainActivity.this);
                    //TODO: Delete all windows in that room || change name of that room?
                }

                return false;
            }
        });


        fab = (FloatingActionButton) findViewById(R.id.fab);

        final Animation animation = AnimationUtils.loadAnimation(this, R.anim.rotation_animation);
        final Animation animationBack = AnimationUtils.loadAnimation(this, R.anim.rotation_animation_back);
        animation.setFillAfter(true);
        animationBack.setFillAfter(true);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fab.startAnimation(animation);
                final Dialog dialog = new Dialog(MainActivity.this);
                dialog.setContentView(R.layout.alert_dialog_new);
                dialog.setTitle("New window");

                final EditText name = (EditText) dialog.findViewById(R.id.edit_text_new_name);
                final EditText room = (EditText) dialog.findViewById(R.id.edit_text_new_room);
                final Spinner spinner = (Spinner) dialog.findViewById(R.id.spinner);

                getMacAddresses(spinner);

                Button btnSave = (Button) dialog.findViewById(R.id.btn_save);
                // if button is clicked, close the custom dialog
                btnSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(spinner.getSelectedItem().equals("Nema novih uređaja")){
                            dialog.dismiss();
                            int duration = Toast.LENGTH_SHORT;
                            Toast toast = Toast.makeText(getBaseContext(), "Nema novih uređaja u mreži", duration);
                            toast.show();
                        } else {
                            postWindow(name.getText().toString(), room.getText().toString(), false, spinner.getSelectedItem().toString());
                        }
                        fab.startAnimation(animationBack);
                        dialog.dismiss();
                    }
                });
                dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        fab.startAnimation(animationBack);
                    }
                });
                dialog.show();
            }

        });

    }


    private void getData() {

        new AsyncTask<String,String,List<Windows>>() {

            @Override
            protected List<Windows> doInBackground(String... urls) {

                ArrayList<Windows> list = new ArrayList<Windows>();
/*
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(urls[0])
                        .build();
/
                Response response =null;
                try {
                    response = client.newCall(request).execute();
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    JSONObject jsonObjectParent = jsonObject.getJSONObject("_embedded");
                    JSONArray jsonArrayChild = jsonObjectParent.getJSONArray("window");


                    for (int i = 0; i < jsonArrayChild.length(); i++){
                        JSONObject windowJSON = jsonArrayChild.getJSONObject(i);
                        JSONObject links = windowJSON.getJSONObject("_links");
                        JSONObject link = links.getJSONObject("self");
                        Windows windows = new Windows(link.getString("href"),windowJSON.getString("name"),windowJSON.getString("roomName"),windowJSON.getBoolean("state"));
                        list.add(windows);
                    }

                } catch (MalformedURLException e){
                    Log.d("ASYNC","MalformedURL");
                    e.printStackTrace();
                } catch (IOException e){
                    Log.d("ASYNC","Invalid argument");
                    e.printStackTrace();
                } catch (JSONException e){
                    Log.d("ASYNC","JSON error");
                    e.printStackTrace();
                }

                return list;*/
                try {
                    URL url = new URL(urls[0]);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
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

                    JSONObject jsonObject = new JSONObject(response.toString());
                    JSONObject jsonObjectParent = jsonObject.getJSONObject("_embedded");
                    JSONArray jsonArrayChild = jsonObjectParent.getJSONArray("window");


                    for (int i = 0; i < jsonArrayChild.length(); i++) {
                        JSONObject windowJSON = jsonArrayChild.getJSONObject(i);
                        JSONObject links = windowJSON.getJSONObject("_links");
                        JSONObject link = links.getJSONObject("self");
                        Windows windows = new Windows(link.getString("href"), windowJSON.getString("name"), windowJSON.getString("roomName"), windowJSON.getBoolean("state"));
                        list.add(windows);
                    }
                    in.close();
                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return list;

            }

            @Override
            protected void onPostExecute(List<Windows> windowses) {
                super.onPostExecute(windowses);


                listDataHeader = new ArrayList<String>();
                listDataChild = new HashMap<String, List<Windows>>();

                HashMap<String,List<Windows>> mapaUlaza = new HashMap<String, List<Windows>>();

                for (Windows window: windowses) {
                    if(!listDataHeader.contains(window.getRoomName())){
                        listDataHeader.add(window.getRoomName());
                        mapaUlaza.put(window.getRoomName(),new ArrayList<Windows>());
                    }
                    mapaUlaza.get(window.getRoomName()).add(window);
                }

                for (String headerName:mapaUlaza.keySet()) {
                    listDataChild.put(headerName,mapaUlaza.get(headerName));
                }
                Log.d("ASYNC",listDataChild.toString());
                listAdapter = new ExpandableListAdapter(getApplicationContext(), listDataHeader, listDataChild);
                expListView.setAdapter(listAdapter);
            }
        }.execute(URL+"/window/search/findNotNull");

        updateWeather();
    }

    public void postWindow(final String name, final String roomName, final boolean state, final String spinnerItem){

        new AsyncTask<String,String,String>() {

            @Override
            protected String doInBackground(String... urls) {
                OkHttpClient client = new OkHttpClient();
                String bodyString = "{ \"name\" : \""+name+"\",  \"roomName\" : \""+roomName+"\", \"state\" : \""+state+"\" }";
                RequestBody body = RequestBody.create(MediaType.parse("application/json"),bodyString);

                Request request = new Request.Builder()
                        .url(urls[0]+spinnerItem)
                        .patch(body)
                        .addHeader("content-type", "application/json")
                        .build();

                try {
                    Response response = client.newCall(request).execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return null;
            }
        }.execute(URL+"/window/");

        //Osvježimo listu prozora pozivajući http get
        getData();
    }


    public void deleteWindow(String href){
        final String link = href;
        new AsyncTask<String,String,String>(){

            @Override
            protected String doInBackground(String... strings) {
                OkHttpClient client = new OkHttpClient();

                Request request = new Request.Builder()
                        .url(link)
                        .delete()
                        .addHeader("content-type", "application/json")
                        .build();

                try {
                    Response response = client.newCall(request).execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return null;
            }
        }.execute(href);
        getData();
    }

    public void putWindow(Windows windows, final String newName, final String newRoomName){
        final Windows window = windows;
        new AsyncTask<String,String,String>() {

            @Override
            protected String doInBackground(String... urls) {
                OkHttpClient client = new OkHttpClient();
                String bodyString = "{  \"name\" : \""+newName+"\",  \"roomName\" : \""+newRoomName+"\"}";
                RequestBody body = RequestBody.create(MediaType.parse("application/json"),bodyString);

                Request request = new Request.Builder()
                        .url(window.getHref())
                        .patch(body)
                        .build();

                try {
                    Response response = client.newCall(request).execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return null;
            }
        }.execute(window.getHref());

        getData();
    }



    public void getMacAddresses(final Spinner spinner){
            new AsyncTask<String,String,List<String>>() {

                @Override
                protected List<String> doInBackground(String... urls) {

                    ArrayList<String> list = new ArrayList<>();


                    try{
                        OkHttpClient client = new OkHttpClient();
                        Request request = new Request.Builder()
                                .url(urls[0])
                                .build();

                        Response response =null;

                        try {
                            response = client.newCall(request).execute();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        if (response!=null) {
                            JSONObject jsonObject = new JSONObject(response.body().string());
                            JSONObject jsonObjectParent = jsonObject.getJSONObject("_embedded");
                            JSONArray jsonArrayChild = jsonObjectParent.getJSONArray("window");


                            for (int i = 0; i < jsonArrayChild.length(); i++) {
                                JSONObject windowJSON = jsonArrayChild.getJSONObject(i);
                                JSONObject links = windowJSON.getJSONObject("_links");
                                JSONObject link = links.getJSONObject("self");
                                String macAddress = windowJSON.getString("id");
                                list.add(macAddress);
                            }
                        }

                    } catch (MalformedURLException e){
                        Log.d("ASYNC","MalformedURL");
                        e.printStackTrace();
                    } catch (IOException e){
                        Log.d("ASYNC","Invalid argument");
                        e.printStackTrace();
                    } catch (JSONException e){
                        Log.d("ASYNC","JSON error");
                        e.printStackTrace();
                    }
                    return list;


                }

                @Override
                protected void onPostExecute(List<String> list) {
                    super.onPostExecute(list);
                    if(list.isEmpty()){
                        list.add("Nema novih uređaja");
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getBaseContext(), R.layout.custom_spinner_item, list);
                    spinner.setAdapter(adapter);

                }
            }.execute(URL+"/window/search/findMac");

        }


    public void updateWeather(){
        Log.d("WEATHER","UpdateWeather");

        new AsyncTask<String, String, Integer>(){
            @Override
            protected Integer doInBackground(String... urls) {
                Integer rainState = 0;
                Log.d("WEATHER","doIn");

                try {
                    /*OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url(urls[0])
                            .build();

                    Response response =null;
                    try {
                        response = client.newCall(request).execute();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    //print result
                    rainState=Integer.parseInt(response.body().string());
*/
                        Log.d("WEATHER","doIn");
                        URL url = new URL ("http://161.53.19.110:2000" + "/get?what=" + "weatherpluv" + "&hours=" + "latest-1");
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
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
                        System.out.println("Weather: "+response.toString());
                        rainState=Integer.parseInt(response.toString());
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    //e.printStackTrace();
                }

                return rainState;
            }


            @Override
            protected void onPostExecute(Integer state){
                ImageView image = (ImageView) findViewById(R.id.imageView2);
                Log.d("WEATHER","BOK");
                if(state.equals(0)) {
                    Log.d("WEATHER","0");
                    image.setImageResource(R.drawable.summer);
                } else {
                    Log.d("WEATHER","1");
                    image.setImageResource(R.drawable.umbrela3);
                }
            }
        }.execute("http://161.53.19.110:2000" + "/get?what=" + "weatherpluv" + "&hours=" + "latest-1");
    }
}
