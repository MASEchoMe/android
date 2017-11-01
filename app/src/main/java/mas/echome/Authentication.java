package mas.echome;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by rodri on 10/31/17.
 */

public class Authentication {
    private static String requestsURL = "http://localhost:80";

    public static String newGroup() throws Exception {
        String reqType = "POST";
        String apiURL = "/api/newGroup";
        String responseName = "groupId";

        return sendReq(reqType, apiURL, false, null, responseName);
    }

    public static String newUser(String name, String groupId) throws Exception {
        String reqType = "GET";
        String apiURL = "/api/getUserToken";
        String body = "name=" + name + "&groupId=" + groupId;
        String responseName = "tempToken";

        String tempToken = sendReq(reqType, apiURL, true, body, responseName);

        return getUser(tempToken);
    }


    public static String getUser(String tempToken) throws Exception {
        String reqType = "GET";
        String apiURL = "/api/getUserToken";
        String body = "tempToken=" + tempToken;
        String responseName = "token";

        return sendReq(reqType, apiURL, true, body, responseName);
    }

    public static String sendReq(String reqType, String apiURL, boolean hasBody, String body, String responseName) throws Exception {
        URL urlObj = new URL(requestsURL + apiURL);
        HttpURLConnection conn = (HttpURLConnection) urlObj.openConnection();

        conn.setRequestMethod(reqType);

        if (hasBody) {
            conn.addRequestProperty("Content-Length", Integer.toString(body.length()));
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setDoOutput(true);

            DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
            wr.writeBytes(body);
            wr.flush();
            wr.close();
        }

        StringBuilder response = new StringBuilder();
        String line;
        BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        while ((line = rd.readLine()) != null) {
            response.append(line).append("\n");
        }
        rd.close();

        JSONObject json = (JSONObject) new JSONTokener(response.toString()).nextValue();
        return json.getString(responseName);
    }
}
