package mas.echome;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

/**
 * Created by rodri on 10/31/17.
 */

public class AuthenticateTask extends AsyncTask<String, Void , String> {
    private String requestsURL = "ec2-54-157-43-79.compute-1.amazonaws.com:3000";

    private Context context;
    private SharedPreferences sharedPrefs;
    private SharedPreferences.Editor sharedPrefsEditor;
    private RequestQueue reqQueue;

    @Override
    protected String doInBackground(String... params) {
        authenticate();
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        System.out.println("Done"); // TODO: what to do here ?
    }

    public AuthenticateTask(Context context, SharedPreferences sharedPrefs) {
        super();
        this.sharedPrefs = sharedPrefs;
        this.sharedPrefsEditor = sharedPrefs.edit();
        this.context = context;
        this.reqQueue = Volley.newRequestQueue(this.context);
        this.sharedPrefsEditor.putString("userName", "currentUser"); // TODO: get name somehow
    }

    /*
     * Checks for the existence of a groupID and a user authentication token and fetches whichever
     * are needed.
     */
    public void authenticate() {
        if (!sharedPrefs.contains("groupId")) {
            try {
                newGroup();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (!sharedPrefs.contains("authToken")) {
            try {
                String groupId = sharedPrefs.getString("groupId", "defaultGroupId");
                newUser("currentUser", groupId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void newGroup() throws Exception {
        int reqType = Request.Method.POST;
        String url = requestsURL + "/api/newGroup";

        JsonObjectRequest jsonReq = new JsonObjectRequest
                (reqType, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String groupIdResponse = response.getString("groupId");
                            sharedPrefsEditor.putString("groupId", groupIdResponse);
                            sharedPrefsEditor.commit();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });
        reqQueue.add(jsonReq);
    }

    private void newUser(String name, String groupId) throws Exception {
        int reqType = Request.Method.POST;
        String url = requestsURL + "/api/getUserToken";

        StringRequest stringRequest = new StringRequest(reqType, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response).getJSONObject("form");
                    String tempAuthToken = jsonResponse.getString("tempToken");
                    getUser(tempAuthToken);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                byte[] body = new byte[0];
                JSONObject jsonContent = new JSONObject();
                try {
                    jsonContent.put("name", sharedPrefs.getString("userName", "currentUser"));
                    jsonContent.put("groupId", sharedPrefs.getString("groupId", "defaultGroupId"));
                    body = jsonContent.toString().getBytes("UTF-8");
                } catch (Exception error) {
                    error.printStackTrace();
                }
                return body;
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                String responseString = "";
                if (response != null) {
                    responseString = String.valueOf(response.statusCode);
                    // can get more details such as response.headers
                }
                return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
            }
        };
        reqQueue.add(stringRequest);
    }


    private void getUser(final String tempToken) throws Exception {
        int reqType = Request.Method.GET;
        String url = requestsURL + "/api/getUserToken";

        StringRequest stringRequest = new StringRequest(reqType, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response).getJSONObject("form");
                    String authToken = jsonResponse.getString("token");
                    sharedPrefsEditor.putString("token", authToken);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                byte[] body = new byte[0];
                JSONObject jsonContent = new JSONObject();
                try {
                    jsonContent.put("tempAuthToken", tempToken);
                    body = jsonContent.toString().getBytes("UTF-8");
                } catch (Exception error) {
                    error.printStackTrace();
                }
                return body;
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                String responseString = "";
                if (response != null) {
                    responseString = String.valueOf(response.statusCode);
                    // can get more details such as response.headers
                }
                return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
            }
        };
        reqQueue.add(stringRequest);
    }
}
