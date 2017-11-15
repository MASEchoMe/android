package mas.echome;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import io.github.yavski.fabspeeddial.FabSpeedDial;
import io.github.yavski.fabspeeddial.SimpleMenuListenerAdapter;

public class HomeActivity extends AppCompatActivity {
    private HomeActivity selfReference; // TODO: I know this shit is garbage I'll fix it later

    private ListView listView;
    ArrayAdapter<String> adapter;

    private Household household = new Household();
    private ArrayList<String> householdNames;
    private SharedPreferences sharedPrefs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        selfReference = this;
        setTheme(android.R.style.Theme_Holo_NoActionBar);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("My House");
        setSupportActionBar(toolbar);
        sharedPrefs = getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE);

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


        //HANDLES INITIAL CLICKING ON NAME FROM HOME SCREEN
        listView = (ListView) findViewById(R.id.list);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position,
                                    long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext(), android.R.style.Theme_Holo_Dialog);
                builder.setTitle("Current Tasks");

                ListView modeList = new ListView(view.getContext());
                builder.setView(modeList);
                final Dialog dia = builder.create();

                //HANDLES CLICKING ON INDIVIDUAL TASKS FROM INSIDE LIST
                modeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, final int newpos,
                                            long id) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext(), android.R.style.Theme_Holo_Dialog);


                        try {
                            Task curTask = household.getPerson(position-1).getTasks().get(newpos);
                            builder.setTitle(curTask.getDescription());

                            final TextView message = new TextView(view.getContext());
                            final SpannableString s =
                                    new SpannableString("Left by: "
                                            + curTask.getSenderName() + " at " + curTask.getDate().toString());
                            Linkify.addLinks(s, Linkify.WEB_URLS);
                            message.setText(s);
                            message.setPadding(30, 30, 30, 30);
                            message.setMovementMethod(LinkMovementMethod.getInstance());
                            builder.setView(message);
                            builder.setPositiveButton("Mark as Completed",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,
                                                            int whichButton) {
                                            new DeleteMessageTask(selfReference, household.getPerson(position-1), newpos).execute();
                                            dia.dismiss();
                                        }
                                    }).setNegativeButton("Cancel",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,
                                                            int whichButton) {
                                            dia.dismiss();
                                            return;

                                        }
                                    });

                            final Dialog dialog = builder.create();
                            dialog.show();
                            TextView msgTxt = (TextView) message;
                            msgTxt.setMovementMethod(LinkMovementMethod.getInstance());

                        } catch (Exception e) {
                            Toast.makeText(view.getContext(), "There's no tasks for this person!", Toast.LENGTH_SHORT).show();
                            dia.dismiss();
                        }
                    }
                });

                builder.setView(modeList);
                dia.show();

                ArrayList<String> curTasks = new ArrayList<String>();
                Person p = household.getPerson(position-1);

                if (p.getNumTasks() == 0) {
                    curTasks.add("No tasks available for " + p.getName() + " right now.");
                }

                for (Task t : p.getTasks()) {
                    curTasks.add(t.getDescription());
                }

                ArrayAdapter<String> modeAdapter = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_list_item_1, android.R.id.text1, curTasks);
                modeList.setAdapter(modeAdapter);

                new GetMessagesTask(selfReference, p, modeAdapter).execute();
            }
        });

        //TODO: FIX SPACING BUG THAT REQUIRES ""
        household.addToHousehold(new Person(""));

        householdNames = new ArrayList<>();
        householdNames.add("");

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, householdNames);
        listView.setAdapter(adapter);

        new GetUsersTask(this).execute();
        }

    protected void addEvent(View view) {
        LayoutInflater factory = LayoutInflater.from(this);
        final View textEntryView = factory.inflate(R.layout.add_event, null);
        final EditText input2 = (EditText) textEntryView.findViewById(R.id.Title);
        input2.setText("", TextView.BufferType.EDITABLE);
        final AlertDialog.Builder alert = new AlertDialog.Builder(this, android.R.style.Theme_Holo_Dialog);

        final ArrayList<String> householdNames= new ArrayList<>();
        final AutoCompleteTextView name = (AutoCompleteTextView) textEntryView.findViewById(R.id.recipient);
        for (Person p: household.getMembers()) {
            householdNames.add(p.getName());
        }
        final ArrayAdapter<String> nameAdapter = new ArrayAdapter<String>(alert.getContext(), android.R.layout.simple_list_item_1, householdNames);
        name.setAdapter(nameAdapter);

        alert.setTitle("New Event:").setView(textEntryView).setPositiveButton("Save",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int whichButton) {

                        ArrayList<String> householdCopy = new ArrayList<String>();

                        for (String s : householdNames) {
                            householdCopy.add(s.toLowerCase());
                        }

                        if (!(householdCopy.contains(name.getText().toString().toLowerCase()))) {
                            Toast.makeText(getApplicationContext(),"Make sure that person is already in the household!",Toast.LENGTH_SHORT).show();
                        } else {
                            int i  = 0;
                            int index = 0;
                            for (Person p: household.getMembers()) {
                                if (p.getName().toLowerCase().equalsIgnoreCase(name.getText().toString())) {
                                    index = i;
                                }

                                i++;
                            }
                            new SendMessageTask(selfReference).execute(household.getPerson(index).toString(), input2.getText().toString());
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

        final AlertDialog.Builder alert = new AlertDialog.Builder(this, android.R.style.Theme_Holo_Dialog);

        final ArrayList<String> householdCopy = new ArrayList<String>();

        for (String s : householdNames) {
            householdCopy.add(s.toLowerCase());
        }

        alert.setTitle("Add Person to Household:").setView(textEntryView).setPositiveButton("Add",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int whichButton) {
                        if (householdCopy.contains(name.getText().toString().toLowerCase())) {
                            Toast.makeText(getApplicationContext(),"That person's already been added!",Toast.LENGTH_SHORT).show();
                        } else {
                            new AddUserTask(selfReference).execute(name.getText().toString());
                        }

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

    public void refreshHouseholdMembers(ArrayList<Person> householdMembers) {
        household.setHousehold(householdMembers);
        householdNames = new ArrayList<>();
        adapter.clear();

        householdNames.add("");
        adapter.add("");
        adapter.notifyDataSetChanged();

        for (Person p : household.getMembers()) { // TODO: there is probably a better way of doing this
            householdNames.add(p.toString());
            adapter.add(p.toString());
            adapter.notifyDataSetChanged();
        }
    }

    public void addHouseholdMember(String name) {
        Person p = new Person(name);
        household.addToHousehold(p);
        householdNames.add(name);
        adapter.add(name);
        adapter.notifyDataSetChanged();
    }

    public void sendMessage(String recipient, String sender, String msg) {
        int i  = 0;
        int index = 0;
        int u = 0;
        for (Person p: household.getMembers()) {
            if (p.getName().toLowerCase().equalsIgnoreCase(recipient)) {
                index = i;
            }

            if (p.getName().toLowerCase().equalsIgnoreCase(sender)) {
                u = i;
            }

            i++;
        }
        household.getPerson(index).giveTask(new Task(household.getPerson(u), msg));
        adapter.notifyDataSetChanged();
    }

    public void refreshMessages(Person p, ArrayAdapter<String> adapter) {
        adapter.clear();

        for (Task t: p.getTasks()) {
            adapter.add(t.getDescription());
            adapter.notifyDataSetChanged();
        }
    }
}

