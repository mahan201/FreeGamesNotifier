package com.mahan.freegamesnotifier;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class JsonTaskRawG extends AsyncTask<String,Void,Void> {

    private MainActivity parent;
    private String name,imgURL,gameDescription,storeLink;

    public JsonTaskRawG(MainActivity activity){
        parent = activity;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(String... strings) {
        storeLink = strings[1];

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

            //Up to here is the same as JsonTask but now we extract image URL, name and description.

            imgURL = jsonObject.getJSONArray("results").getJSONObject(0).getString("background_image");
            name = jsonObject.getJSONArray("results").getJSONObject(0).getString("name");

            url = new URL("https://api.rawg.io/api/games/" + jsonObject.getJSONArray("results").getJSONObject(0).getString("slug"));
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            stream = connection.getInputStream();

            reader = new BufferedReader(new InputStreamReader(stream));

            buffer = new StringBuffer();

            while ((line = reader.readLine()) != null) {
                buffer.append(line+"\n");
            }
            data  = buffer.toString();
            JSONObject jsonObject2 = new JSONObject(data);

            gameDescription = jsonObject2.getString("description_raw");

            return null;


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
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);


    }
}
