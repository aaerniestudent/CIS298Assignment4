package edu.kvcc.cis298.cis298assignment4;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Anthony on 12/15/2015.
 */
//loads beverage data from Web
public class BeverageFetcher {

    private static final String TAG = "BEVERAGEFETCHER";

    private byte[] getUrlBytes(String urlSpec) throws IOException {
        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException(connection.getResponseMessage() + ": with " + urlSpec);
            }

            int bytesRead = 0;
            byte[] buffer = new byte[1024];

            while ((bytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
            }
            out.close();
            return out.toByteArray();
        } finally {
            connection.disconnect();
        }
    }

            
    private String getUrlString(String urlSpec) throws IOException {
        return new String(getUrlBytes(urlSpec));
    }

    public List<Beverage> fetchBeverages() {

        List<Beverage> beverages = new ArrayList<>();
        try {
            String url = Uri.parse("http://barnesbrothers.homeserver.com/beverageapi").buildUpon().build().toString();
            String jsonString = getUrlString(url);
            JSONArray jsonArray = new JSONArray(jsonString);
            parseBeverages(beverages, jsonArray);
            Log.i(TAG, "Received JSON: " + jsonString);
        } catch (JSONException je) {
            Log.i(TAG, "Failed to Parse", je);
        } catch (IOException ioe) {
            Log.e(TAG, "Failed to fetch items; " + ioe);
        }

        return beverages;
    }

    private void parseBeverages(List<Beverage> beverages, JSONArray jsonArray)
         throws IOException, JSONException {

        for (int i=0; i < jsonArray.length(); i++) {
            JSONObject beverageJsonObject = jsonArray.getJSONObject(i);
            String uuidString = beverageJsonObject.getString("uuid");
            UUID uuidBeverage = UUID.fromString(uuidString);
            String nameBeverage = beverageJsonObject.getString("name");
            String packBeverage = beverageJsonObject.getString("pack");
            double priceBeverage = beverageJsonObject.getDouble("price");
            boolean activeBeverage = beverageJsonObject.getString("is_solved").equals("1");
            Beverage beverage = new Beverage(uuidBeverage.toString(),nameBeverage,packBeverage,priceBeverage,activeBeverage);
            beverages.add(beverage);
        }
    }
    
}
