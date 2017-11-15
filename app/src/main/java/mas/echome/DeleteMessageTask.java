package mas.echome;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.ArrayAdapter;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
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

        HttpURLConnection urlConnection;
        try {
            urlConnection = (HttpURLConnection) (new URL(url)).openConnection();
            urlConnection.setRequestMethod("DELETE");
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("Content-Type", "application/json");
            JSONObject jsonBody = new JSONObject();

            OutputStreamWriter osw = new OutputStreamWriter(urlConnection.getOutputStream());
            jsonBody.put("messageId", idToDelete);
            osw.write(jsonBody.toString());
            osw.flush();

            int HttpResult = urlConnection.getResponseCode();
            if (HttpResult == HttpURLConnection.HTTP_OK) {
                InputStream is = new BufferedInputStream(urlConnection.getInputStream());
                System.out.println(is.read());
            } else {
                System.out.println(HttpResult);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
