package http_database;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.util.HashMap;

import javax.net.ssl.HttpsURLConnection;

import Utils.Constants;
import Utils.Keys;
import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.json.JSONObject;

public class PushServer {


    public String send(String key, int id) {
        int responseCode = -1;
        String responseBody = null;
        try {
            System.out.println("Sending FCM request");

            byte[] postData;

            // checking for tokenID and topic
            if (id == Constants.SEND_TOPIC) {
                postData = getPostDataTopic(key);
            } else {
                postData = getPostData(key);
            }

            URL url = new URL(Keys.END_URL);
            HttpsURLConnection httpURLConnection = (HttpsURLConnection) url.openConnection();

            //set timeputs to 10 seconds
            httpURLConnection.setConnectTimeout(10000);
            httpURLConnection.setReadTimeout(10000);

            httpURLConnection.setDoOutput(true);
            httpURLConnection.setUseCaches(false);
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("Content-Type", "application/json");
            httpURLConnection.setRequestProperty("Content-Length", Integer.toString(postData.length));
            httpURLConnection.setRequestProperty("Authorization", "key=" + Keys.AUTH_KEY);


            OutputStream out = httpURLConnection.getOutputStream();
            out.write(postData);
            out.close();
            responseCode = httpURLConnection.getResponseCode();
            //success
            if (responseCode == HttpStatus.SC_OK) {
                responseBody = convertStreamToString(httpURLConnection.getInputStream());
                System.out.println("FCM message sent : " + responseBody);
            }
            //failure
            else {
                responseBody = convertStreamToString(httpURLConnection.getErrorStream());
                System.out.println("Sending FCM request failed for regId: " + key + " response: " + responseBody);
            }
        } catch (IOException ioe) {
            System.out.println("IO Exception in sending FCM request. regId: " + key);
            ioe.printStackTrace();
        } catch (Exception e) {
            System.out.println("Unknown exception in sending FCM request. regId: " + key);
            e.printStackTrace();
        }
        if (responseBody == null) {
            return "Failed";
        } else {
            return responseBody;
        }
    }

    private byte[] getPostData(String registrationId) throws JSONException {
        HashMap<String, String> dataMap = new HashMap<>();
        JSONObject payloadObject = new JSONObject();

        dataMap.put("title", "Push Notification title");
        dataMap.put("message", "This is a notification message");
        dataMap.put("type","singleuser");

        JSONObject data = new JSONObject(dataMap);
        payloadObject.put("data", data);
        payloadObject.put("to", registrationId);

        return payloadObject.toString().getBytes();
    }

    private static byte[] getPostDataTopic(String topic) throws JSONException {
        HashMap<String, String> dataMap = new HashMap<>();
        JSONObject payloadObject = new JSONObject();

        dataMap.put("title", "Push http_database.Notification title");
        dataMap.put("message", "This is a notification message");
        dataMap.put("type","topic");
        dataMap.put("topic",topic);

        JSONObject data = new JSONObject(dataMap);
        ;
        payloadObject.put("data", data);
        payloadObject.put("to", "/topics/" + topic);

        return payloadObject.toString().getBytes();
    }

    public static String convertStreamToString(InputStream inStream) throws Exception {
        InputStreamReader inputStream = new InputStreamReader(inStream);
        BufferedReader bReader = new BufferedReader(inputStream);

        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = bReader.readLine()) != null) {
            sb.append(line);
        }

        return sb.toString();
    }

}