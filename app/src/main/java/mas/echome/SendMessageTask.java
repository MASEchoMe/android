package mas.echome;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

/**
 * Created by rodri on 11/15/17.
 */

public class SendMessageTask extends AsyncTask<String, Void, Void> {
    private HomeActivity activity;
    private SharedPreferences sharedPrefs;
    private RequestQueue reqQueue;

    private String baseURL;

    public SendMessageTask(HomeActivity activity) {
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
        sendMessage(params[0], params[1]);
        return null;
    }

    private void sendMessage(final String recipient, final String msg) {
        int reqType = Request.Method.POST;
        final String groupId = sharedPrefs.getString("groupId", "1");
        final String sender = sharedPrefs.getString("name", "You");
        String url = baseURL + "/api/messages";

        StringRequest jsonReq = new StringRequest(reqType, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    // If this doesn't error it means that we successfully posted a message, but
                    // at this moment we don't use the response for anything.
                    // TODO: clean this up
                    if (response.equals("Successfully added " + sender + "'s message to " + recipient)) {
                        activity.sendMessage(recipient, sender, msg);
                    } else {
                        // TODO: more garbage I know, I swear I'll fix this later
                        throw new Exception("Unable to send message at this time.");
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
                byte[] body = new byte[0];
                JSONObject jsonBody = new JSONObject();

                try {
                    jsonBody.put("recipient", recipient);
                    jsonBody.put("groupId", groupId);
                    jsonBody.put("sender", sender);
                    jsonBody.put("message", msg);
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
