package mas.echome;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;

import java.util.ArrayList;

/**
 * Created by rodri on 11/13/17.
 */

public class GetUsersTask extends AsyncTask<String, Void, Void> {
    private HomeActivity activity;
    private SharedPreferences sharedPrefs;
    private RequestQueue reqQueue;

    private String baseURL;

    public GetUsersTask(HomeActivity activity) {
        this.activity = activity;
        this.sharedPrefs = activity.getSharedPreferences(activity.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        this.reqQueue = Volley.newRequestQueue(this.activity);
        this.baseURL = this.activity.getString(R.string.requests_url);
    }
    /*
     * Specifies what to actually do (in the background) when the .execute() function is called. The
     * return value is passed back to the original place where .execute() was called.
     */
    @Override
    protected Void doInBackground(String... params) {
        getUsers();
        return null;
    }

    private void getUsers() {
        int reqType = Request.Method.GET;
        String url = baseURL + "/api/getUsers?groupId=" + sharedPrefs.getString("groupId", "1");

        JsonArrayRequest jsonArrReq = new JsonArrayRequest(reqType, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    ArrayList<Person> householdMembers = new ArrayList<>();
                    String name;
                    for (int i = 0; i < response.length(); i++) {
                        name = response.getJSONObject(i).getString("name");
                        householdMembers.add(new Person(name)); // Note: does not retrieve tasks
                    }

                    activity.refreshHouseholdMembers(householdMembers); // Callback to HomeActivity
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        },  new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError e) {
                e.printStackTrace();
            }
        });

        reqQueue.add(jsonArrReq);
    }
}
