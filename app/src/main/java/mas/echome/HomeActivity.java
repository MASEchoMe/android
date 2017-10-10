package mas.echome;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

    }

    protected void addEvent(View view) {
        LayoutInflater factory = LayoutInflater.from(this);

        final View textEntryView = factory.inflate(R.layout.add_event, null);

        final EditText input1 = (EditText) textEntryView.findViewById(R.id.recipient);
        final EditText input2 = (EditText) textEntryView.findViewById(R.id.Title);

        input1.setText("", TextView.BufferType.EDITABLE);
        input2.setText("", TextView.BufferType.EDITABLE);

        final AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("New Event:").setView(textEntryView).setPositiveButton("Save",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int whichButton) {

                    }
                }).setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int whichButton) {
                        return;
                    }
                });
        alert.show();
    }



}
