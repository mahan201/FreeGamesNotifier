package com.mahan.freegamesnotifier;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.widget.ImageView;
import android.widget.ProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class JsonTask extends AsyncTask<String,Void,JSONObject> {

    private MainActivity parent;
    ProgressDialog pd;

    public JsonTask(MainActivity activity){
        parent = activity;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected JSONObject doInBackground(String... strings) {
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        String data = "";
        JSONObject jsonObject;
        StringBuilder builder = new StringBuilder();
        try{
            URL url = new URL(strings[0]);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            InputStream stream = connection.getInputStream();

            reader = new BufferedReader(new InputStreamReader(stream));

            StringBuffer buffer = new StringBuffer();
            String line;

            while ((line = reader.readLine()) != null) {
                buffer.append(line+"\n");
            }
            data  = buffer.toString();
            jsonObject = new JSONObject(data);

            return jsonObject;


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(JSONObject object) {
        super.onPostExecute(object);


        parent.setJson(object);

    }

    public interface JsonTaskInterface{
        void setJson(JSONObject obj);
    }
}
