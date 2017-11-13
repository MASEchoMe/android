package mas.echome;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Mailbox extends AppCompatActivity {
    SharedPreferences sharedPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(android.R.style.Theme_Holo_NoActionBar);
        setContentView(R.layout.activity_mailbox);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("My Mailbox");
        setSupportActionBar(toolbar);
        sharedPrefs = getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE);

        RelativeLayout cur = (RelativeLayout) findViewById(R.id.content_mailbox);
        TextView txtview = (TextView) findViewById(R.id.setupFor);
        txtview.setText("Mailbox for : " + sharedPrefs.getString("name", "You"));
    }

    protected void setup(View view) {
        Intent i = new Intent(this, SetupActivity.class);
        startActivity(i);
    }

    protected void goBack(View view) {
        Intent i = new Intent(this, HomeActivity.class);
        startActivity(i);
    }

}
