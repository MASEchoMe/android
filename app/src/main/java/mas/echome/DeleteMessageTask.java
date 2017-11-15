package mas.echome;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.ArrayAdapter;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by rodri on 11/15/17.
 */

public class DeleteMessageTask extends AsyncTask<String, Void, Void> {
    private HomeActivity activity;
    private SharedPreferences sharedPrefs;
    private RequestQueue reqQueue;

    private String baseURL;

    private Person p;
    private ArrayAdapter<String> adapter;
    private int position;

    public DeleteMessageTask(HomeActivity activity, Person p, int position) {
        this.activity = activity;
        this.sharedPrefs = activity.getSharedPreferences(activity.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        this.reqQueue = Volley.newRequestQueue(this.activity);
        this.baseURL = this.activity.getString(R.string.requests_url);
        this.p = p;
        this.position = position;
    }

    /*
     * Specifies what to actually do (in the background) when the .execute() function is called.
     */
    @Override
    protected Void doInBackground(String... params) {
        System.out.println("Running doInBg");
        deleteMessage();
        return null;
    }

    private void deleteMessage() {
        int reqType = Request.Method.DELETE;
        String url = baseURL + "/api/messages";

        String currId = "1";
        ArrayList<Task> tasks = p.getTasks();
        int i = 0;
        for (Task t : tasks) {
            if (i == position) {
                currId = t.getId();
            }
            i++;
        }
        final String idToDelete = currId;
        StringRequest jsonReq = new StringRequest(reqType, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    // If this doesn't error it means that we successfully posted a message, but
                    // at this moment we don't use the response for anything.
                    // TODO: clean this up
                    if (response.equals("Successfully deleted message " + idToDelete)) {
                        p.getTasks().remove(position);
                    } else {
                        // TODO: more garbage I know, I swear I'll fix this later
                        throw new Exception("Unable to delete message at this time.");
                    }
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
                System.out.println("getBody has been called");
                byte[] body = new byte[0];
                JSONObject jsonBody = new JSONObject();

                try {
                    jsonBody.put("messageId", idToDelete);
                    body = jsonBody.toString().getBytes();
                    System.out.println(jsonBody.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return body;
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };

        reqQueue.add(jsonReq);
    }
}
