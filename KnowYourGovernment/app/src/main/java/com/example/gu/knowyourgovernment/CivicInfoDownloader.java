package com.example.gu.knowyourgovernment;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gu on 2018/3/19.
 */

public class CivicInfoDownloader extends AsyncTask<String, Void, String> {
    private static final String TAG = "CivicInfoDownloader";
    private MainActivity mainActivity;

    String APIkey = "AIzaSyBHX1uya5k_NvSmm53QKvaY5bcK4NeOdnk";
    String URL = "https://www.googleapis.com/civicinfo/v2/representatives?key=" + APIkey + "&address=";

    public CivicInfoDownloader(MainActivity ma) {
        mainActivity = ma;
    }

    @Override
    protected String doInBackground(String... strings) {
        String zip = strings[0];
        URL = URL + zip;
        Log.d(TAG, "doInBackground: " + URL);
        StringBuilder sb = new StringBuilder();
        try {
            java.net.URL url = new URL(URL);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            InputStream is = conn.getInputStream();
            BufferedReader reader = new BufferedReader((new InputStreamReader(is)));

            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
            Log.d(TAG, "doInBackground: " + sb.toString());
            return sb.toString();
        } catch (Exception e) {
            Log.e(TAG, "doInBackground: ", e);
            return null;
        }
    }

    @Override
    protected void onPostExecute(String s) {
        Log.d(TAG, "onPostExecute: JSON String:" + s);
        if (s == null) {
            mainActivity.noInfoAvailable();
            mainActivity.setOfficialList(null);
        } else if (s == "") {
            mainActivity.noDataAvailable();
            mainActivity.setOfficialList(null);
        } else {
            Object[] res = parseJSON(s);
            mainActivity.setOfficialList(res);
        }
        super.onPostExecute(s);
    }

    private Object[] parseJSON(String s) {
        Object[] res = new Object[2];
        ArrayList<Official> list = new ArrayList<Official>();
        String debug = "";
        try {
            JSONObject jObjMain = new JSONObject(s);
            JSONObject normalizedInput = jObjMain.getJSONObject("normalizedInput");
            String city = normalizedInput.getString("city");
            String state = normalizedInput.getString("state");
            String zip = normalizedInput.getString("zip");
            res[0] = city + "," + state + " " + zip;
            JSONArray officesList = jObjMain.getJSONArray("offices");
            JSONArray officialsList = jObjMain.getJSONArray("officials");
            for (int i = 0; i < officesList.length(); i++) {
                JSONObject offices = (JSONObject) officesList.get(i);
                String name = offices.getString("name");
                JSONArray officialIndices = offices.getJSONArray("officialIndices");
                for (int j = 0; j < officialIndices.length(); j++) {
                    Official official = new Official();
                    official.setTitle(name);
                    JSONObject officials = (JSONObject) officialsList.get(Integer.parseInt(officialIndices.get(j).toString()));
                    String personName = officials.getString("name");
                    official.setName(personName);
                    if (officials.has("address")) {
                        JSONArray addressList = officials.getJSONArray("address");
                        JSONObject address = (JSONObject) addressList.get(0);
                        String line = address.getString("line1");
                        if (address.has("line2")) line = line + " " + address.getString("line2");
                        if (address.has("line3")) line = line + " " + address.getString("line3");
                        official.setAddress(line, address.getString("city"), address.getString("state"), address.getString("zip"));
                    }
                    if (officials.has("party")) official.setParty(officials.getString("party"));
                    if (officials.has("phones")) official.setPhones(officials.getJSONArray("phones"));
                    if (officials.has("urls")) official.setUrls(officials.getJSONArray("urls"));
                    if (officials.has("emails")) official.setEmails(officials.getJSONArray("emails"));
                    if (officials.has("photoUrl"))
                        official.setPhotoUrl(officials.getString("photoUrl"));
                    if (officials.has("channels")) {
                        JSONArray channelList = officials.getJSONArray("channels");
                        Channel channel = new Channel();
                        for (int k = 0; k < channelList.length(); k++) {
                            JSONObject channelobj = (JSONObject) channelList.get(k);
                            switch (channelobj.getString("type")) {
                                case "GooglePlus":
                                    channel.setGooglePlusId(channelobj.getString("id"));
                                    break;
                                case "Facebook":
                                    channel.setFacebookId(channelobj.getString("id"));
                                    break;
                                case "Twitter":
                                    channel.setTwitterId(channelobj.getString("id"));
                                    break;
                                case "YouTube":
                                    channel.setYouTubeId(channelobj.getString("id"));
                                    break;
                            }
                        }
                        official.setChannel(channel);
                    }
                    list.add(official);
                }
            }
            res[1] = list;
            return res;

        } catch (Exception e) {
            Log.e(TAG, "parseJSON: "+debug+"!!!!!!!!!!!!!", e);
            String a = e.getMessage();
            return null;
        }
    }
}
