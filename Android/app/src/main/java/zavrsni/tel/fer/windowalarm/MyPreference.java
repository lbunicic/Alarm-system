package zavrsni.tel.fer.windowalarm;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by lbunicic on 01/06/2017.
 */

public class MyPreference {
    private static MyPreference yourPreference;
    private SharedPreferences sharedPreferences;
    private static final String defaultURL = "http://192.168.5.13:8084";

    public static MyPreference getInstance(Context context) {
        if (yourPreference == null) {
            yourPreference = new MyPreference(context);
        }
        return yourPreference;
    }

    private MyPreference(Context context) {
        sharedPreferences = context.getSharedPreferences("Urls",Context.MODE_PRIVATE);
    }

    public void saveData(String key,String value) {
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        prefsEditor.putString(key, value);
        prefsEditor.commit();
    }

    public String getData(String key) {
        if (sharedPreferences!= null) {
            return sharedPreferences.getString(key, defaultURL );
        }
        return defaultURL ;
    }
}
