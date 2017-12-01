package zavrsni.tel.fer.windowalarm;

import android.app.Dialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class InfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        TextView link = (TextView) findViewById(R.id.linkTxt);
        link.setClickable(true);
        link.setMovementMethod(LinkMovementMethod.getInstance());

        final Button urlBtn  = (Button) findViewById(R.id.url_btn);
        final MyPreference myPreference = MyPreference.getInstance(getApplicationContext());
        urlBtn.setText(myPreference.getData("URL"));

        urlBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(InfoActivity.this);
                dialog.setContentView(R.layout.alert_dialog_url);
                dialog.setTitle("Edit URL");
                final EditText URL = (EditText) dialog.findViewById(R.id.edit_text_url);
                URL.setText(myPreference.getData("URL"));
                Button btnSave = (Button) dialog.findViewById(R.id.btn_save_url);
                btnSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MyPreference myPreference = MyPreference.getInstance(getApplicationContext());
                        myPreference.saveData("URL", URL.getText().toString());
                        urlBtn.setText(URL.getText().toString());
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });
    }
}
