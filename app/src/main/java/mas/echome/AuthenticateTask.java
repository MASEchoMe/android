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

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

/**
 * Created by rodri on 10/31/17.
 */

public class AuthenticateTask extends AsyncTask<String, Void , Void> {
    private String baseURL = "https://55caf19a-7559-4be5-bdbf-edf4bd64043c.mock.pstmn.io";
    //private String baseURL = "https://reddit.com";

    private SharedPreferences sharedPrefs;
    private RequestQueue reqQueue;

    public AuthenticateTask(Context context, SharedPreferences sharedPrefs) {
        this.sharedPrefs = sharedPrefs;
        this.reqQueue = Volley.newRequestQueue(context);
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
    public void authenticate(final String tempToken) {
        int reqType = Request.Method.GET;
        String url = baseURL + "/api/getUserToken";

        JsonObjectRequest jsonReq = new JsonObjectRequest(reqType, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String respName = response.getString("name");
                    String respAuthToken = response.getString("token");
                    SharedPreferences.Editor editor = sharedPrefs.edit();

                    editor.putString("name", respName);
                    editor.putString("token", respAuthToken);
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
        }) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() {
                byte[] body = new byte[0];
                HashMap<String, String> jsonMap = new HashMap<>();
                JSONObject jsonBody;

                jsonMap.put("tempToken", tempToken);
                jsonBody = new JSONObject(jsonMap);

                try {
                    body = jsonBody.toString().getBytes("UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                return body;
            }
        };

        reqQueue.add(jsonReq);
    }
}
