package mas.echome;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by rodri on 11/15/17.
 */

public class AddUserTask extends AsyncTask<String, Void, Void> {
    private HomeActivity activity;
    private SharedPreferences sharedPrefs;
    private RequestQueue reqQueue;

    private String baseURL;

    public AddUserTask(HomeActivity activity) {
        this.activity = activity;
        this.sharedPrefs = activity.getSharedPreferences(activity.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        this.reqQueue = Volley.newRequestQueue(this.activity);
        this.baseURL = this.activity.getString(R.string.requests_url);
    }

    /*
     * Specifies what to actually do (in the background) when the .execute() function is called.
     */
    @Override
    protected Void doInBackground(String... params) {
        adduser(params[0]);
        return null;
    }

    private void adduser(final String name) {
        int reqType = Request.Method.POST;
        final String groupId = sharedPrefs.getString("groupId", "1");
        String url = baseURL + "/api/newUserTempToken?name=" + name + "&groupId=" + groupId;

        JsonObjectRequest jsonReq = new JsonObjectRequest(reqType, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    // If this doesn't error it means that we successfully created a new user, but
                    // at this moment we don't use the tempToken for anything.
                    // TODO: clean this up
                    String tempToken = response.getString("tempToken");
                    activity.addHouseholdMember(name);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError e) {
                e.printStackTrace();
            }
        }) {
            @Override
            public byte[] getBody() {
                byte[] body = new byte[0];
                HashMap<String, String> map = new HashMap<>();
                JSONObject jsonBody;

                try {
                    map.put("name", name);
                    map.put("groupId", groupId);
                    jsonBody = new JSONObject(map);
                    body = jsonBody.toString().getBytes();
                } catch (UnsupportedOperationException uee) {
                    uee.printStackTrace();
                }

                return body;
            }
        };


        reqQueue.add(jsonReq);
    }
}
