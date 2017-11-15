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

/**
 * Created by rodri on 10/31/17.
 */

public class AuthenticateTask extends AsyncTask<String, Void , Void> {
    private Context context;
    private SharedPreferences sharedPrefs;
    private RequestQueue reqQueue;

    private String baseURL;

    public AuthenticateTask(Context context, SharedPreferences sharedPrefs) {
        this.context = context;
        this.sharedPrefs = sharedPrefs;
        this.reqQueue = Volley.newRequestQueue(this.context);
        this.baseURL = context.getString(R.string.requests_url);
    }

    /*
     * Specifies what to actually do (in the background) when the .execute() function is called. The
     * return value is passed back to the original place where .execute() was called.
     */
    @Override
    protected Void doInBackground(String... params) {
        authenticate(params[0]); // Shouldn't be more than one arg
        return null;
    }

    /**
     * Fetches the user's name and token based on the temporary token.
     *
     * @param  tempToken the temporary token
     */
    private void authenticate(String tempToken) {
        int reqType = Request.Method.GET;
        String url = baseURL + "/api/getUser?tempToken=" + tempToken;

        JsonObjectRequest jsonReq = new JsonObjectRequest(reqType, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String respName = response.getString("name");
                    String respGroupId = response.getString("groupId");
                    SharedPreferences.Editor editor = sharedPrefs.edit();

                    editor.putString("name", respName);
                    editor.putString("groupId", respGroupId);
                    editor.commit();
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
