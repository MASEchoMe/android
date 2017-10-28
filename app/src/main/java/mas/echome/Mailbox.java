package mas.echome;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Mailbox extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mailbox);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("My Mailbox");
        setSupportActionBar(toolbar);

        RelativeLayout cur = (RelativeLayout) findViewById(R.id.content_mailbox);
        TextView txtview = (TextView) findViewById(R.id.setupFor);
        txtview.setText("Mailbox for : INSERT USERNAME HERE");
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
