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

/**
 * Created by rodri on 11/27/17.
 */

public class GetRecommendationsTask extends AsyncTask<String, Void, Void> {
    private HomeActivity activity;
    private SharedPreferences sharedPrefs;
    private RequestQueue reqQueue;
    private Task task;
    private String product;

    private String baseURL;

    public GetRecommendationsTask(HomeActivity activity, Task task, String product) {
        this.activity = activity;
        this.sharedPrefs = activity.getSharedPreferences(activity.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        this.reqQueue = Volley.newRequestQueue(this.activity);
        this.baseURL = this.activity.getString(R.string.requests_url);
        this.task = task;
        this.product = product;
    }

    /*
     * Specifies what to actually do (in the background) when the .execute() function is called.
     */
    @Override
    protected Void doInBackground(String... params) {
        getRecommendations();
        return null;
    }

    private void getRecommendations() {
        int reqType = Request.Method.GET;
        String url = baseURL + "/api/products?product=" + product;

        JsonArrayRequest jsonArrReq = new JsonArrayRequest(reqType, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    String productLink = response.getString(0);
                    task.setLink(productLink);
                    task.setLinkDescription("Recommendation for:<br/><a href=\"" + productLink + "\">" + product.toLowerCase() + "</a>");
                    activity.refreshRecommendation();
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
