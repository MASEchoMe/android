package mas.echome;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.ArrayAdapter;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;

import java.util.ArrayList;

/**
 * Created by rodri on 11/15/17.
 */

public class GetMessagesTask extends AsyncTask<String, Void, Void> {
    private HomeActivity activity;
    private SharedPreferences sharedPrefs;
    private RequestQueue reqQueue;

    private String baseURL;

    private Person p;
    private ArrayAdapter<String> adapter;

    public GetMessagesTask(HomeActivity activity, Person p, ArrayAdapter<String> adapter) {
        this.activity = activity;
        this.sharedPrefs = activity.getSharedPreferences(activity.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        this.reqQueue = Volley.newRequestQueue(this.activity);
        this.baseURL = this.activity.getString(R.string.requests_url);
        this.p = p;
        this.adapter = adapter;
    }

    /*
     * Specifies what to actually do (in the background) when the .execute() function is called.
     */
    @Override
    protected Void doInBackground(String... params) {
        getMessages();
        return null;
    }

    private void getMessages() {
        int reqType = Request.Method.GET;
        final String groupId = sharedPrefs.getString("groupId", "1");
        String url = baseURL + "/api/messages?name=" + p.getName() + "&groupId=" + groupId;

        JsonArrayRequest jsonReq = new JsonArrayRequest(reqType, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    ArrayList<Person> householdMembers = new ArrayList<>();
                    String sender_name;
                    String msg;
                    String dateStr;
                    String id;
                    p.setTask(new ArrayList<Task>());
                    for (int i = 0; i < response.length(); i++) {
                        sender_name = response.getJSONObject(i).getString("sender_name");
                        msg = response.getJSONObject(i).getString("message");
                        dateStr = response.getJSONObject(i).getString("create_date");
                        id = response.getJSONObject(i).getString("message_id");
                        Task newTask = new Task(new Person(sender_name), msg, dateStr, id);
                        if (msg.toLowerCase().contains("buy")) {
                            String product = msg.replaceAll("(?i)buy", "");
                            new GetRecommendationsTask(activity, newTask, product).execute();
                        }
                        p.giveTask(newTask);
                    }

                    activity.refreshMessages(p, adapter);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError e) {
                e.printStackTrace();
            }
        });

        reqQueue.add(jsonReq);
    }
}
