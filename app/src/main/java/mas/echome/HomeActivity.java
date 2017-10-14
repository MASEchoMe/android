package mas.echome;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import io.github.yavski.fabspeeddial.FabSpeedDial;
import io.github.yavski.fabspeeddial.SimpleMenuListenerAdapter;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class HomeActivity extends AppCompatActivity {

    private ListView listView;
    ArrayAdapter<String> adapter;

    private ArrayList<Person> household;
    private ArrayList<String> householdNames;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("My House");
        setSupportActionBar(toolbar);

        //LOGIC FOR FLOATING ACTION BUTTON --------------------------
        FabSpeedDial fabSpeedDial = (FabSpeedDial) findViewById(R.id.fab_speed_dial);
        fabSpeedDial.setMenuListener(new SimpleMenuListenerAdapter() {
            @Override
            public boolean onMenuItemSelected(MenuItem menuItem) {

                switch (menuItem.getItemId()) {
                    case R.id.action_help:
                        //Send reminder to Echo;

                        return true;
                    case R.id.add_person:
                        //Add person to household
                        addToHouse(getCurrentFocus());
                        return true;
                    case R.id.add_reminder:
                        //Add reminder for person;
                        addEvent(getCurrentFocus());
                }
                return true;
            }
        });



        listView = (ListView) findViewById(R.id.list);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position,
                                    long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle("Current Tasks");


                ListView modeList = new ListView(view.getContext());
                builder.setView(modeList);
                final Dialog dia = builder.create();
                modeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, final int newpos,
                                            long id) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                        builder.setTitle("Don't Forget!");

                        ListView modeList = new ListView(view.getContext());
                        Task curTask = household.get(position).getTasks().get(newpos);

                        builder.setMessage(curTask.getDescription() + " \n\nLeft by " + curTask.getSenderName() + " on " + curTask.getDate().toString());

                        builder.setView(modeList);

                        builder.setPositiveButton("Mark as Completed",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int whichButton) {
                                        household.get(position).getTasks().remove(newpos);
                                        adapter.notifyDataSetChanged();
                                        dia.dismiss();
                                    }
                                }).setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int whichButton) {
                                        return;
                                    }
                                });

                        final Dialog dialog = builder.create();
                        dialog.show();
                    }
                });

                builder.setView(modeList);
                dia.show();

                ArrayList<String> curTasks = new ArrayList<String>();
                Person p = household.get(position);

                if (p.getNumTasks() == 0) {
                    curTasks.add("No tasks available for " + p.getName() + " right now.");
                }

                for (Task t : p.getTasks()) {
                    curTasks.add(t.getDescription());
                }

                ArrayAdapter<String> modeAdapter = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_list_item_1, android.R.id.text1, curTasks);
                modeList.setAdapter(modeAdapter);
            }
        });


        household = new ArrayList<>();
        //TODO: FIX SPACING BUG THAT REQUIRES ""
        household.add(new Person(""));

        householdNames = new ArrayList<>();
        householdNames.add("");

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, householdNames);
        listView.setAdapter(adapter);
        }

    protected void addEvent(View view) {
        LayoutInflater factory = LayoutInflater.from(this);
        final View textEntryView = factory.inflate(R.layout.add_event, null);
        final EditText input2 = (EditText) textEntryView.findViewById(R.id.Title);
        input2.setText("", TextView.BufferType.EDITABLE);
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);

        final ArrayList<String> householdNames= new ArrayList<>();
        final AutoCompleteTextView name = (AutoCompleteTextView) textEntryView.findViewById(R.id.recipient);
        for (Person p: household) {
            householdNames.add(p.getName());
        }
        final ArrayAdapter<String> nameAdapter = new ArrayAdapter<String>(alert.getContext(), android.R.layout.simple_list_item_1, householdNames);
        name.setAdapter(nameAdapter);

        alert.setTitle("New Event:").setView(textEntryView).setPositiveButton("Save",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int whichButton) {

                        if (!(householdNames.contains(name.getText().toString()))) {
                            Toast.makeText(getApplicationContext(),"Make sure that person is already in the household!",Toast.LENGTH_SHORT).show();
                        } else {
                            int i  = 0;
                            int index = 0;
                            for (Person p: household) {
                                if (p.getName().toLowerCase().equals(name.getText().toString().toLowerCase())) {
                                    index = i;
                                }
                                i++;
                            }
                            household.get(index).giveTask(new Task(new Person("InsertSenderHere"), input2.getText().toString()));
                        }
                        adapter.notifyDataSetChanged();
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

    protected void addToHouse(View view) {
        LayoutInflater factory = LayoutInflater.from(this);

        final View textEntryView = factory.inflate(R.layout.add_person, null);

        final EditText name = (EditText) textEntryView.findViewById(R.id.name);

        name.setText("", TextView.BufferType.EDITABLE);

        final AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Add Person to Household:").setView(textEntryView).setPositiveButton("Add",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int whichButton) {
                        Person p = new Person(name.getText().toString());
                        household.add(p);
                        householdNames.add(name.getText().toString());
                        adapter.notifyDataSetChanged();

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

    protected void gotoMailbox(View view) {
        Intent i = new Intent(this, Mailbox.class);
        startActivity(i);
    }
}

