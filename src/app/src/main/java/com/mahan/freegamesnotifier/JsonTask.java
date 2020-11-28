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
/*
The JsonTask is an AsyncTask Class that is able to return the JSON response
of a HTML API call.
To use this the class needs to implement the JsonTask.JsonTaskInterface
This makes the user create a setJson Method that is called with the data.
 */
public class JsonTask extends AsyncTask<String,Void,JSONObject> {

    private MainActivity parent;

    public JsonTask(MainActivity activity){
        /*
        Constructor. By default takes in the parent activity that called it
        so that it can run setJson on completion.
         */
        this.parent = activity;
    }

    @Override
    protected void onPreExecute() {
        /*
        Run before executing the background task.
         */
        super.onPreExecute();
    }

    @Override
    protected JSONObject doInBackground(String... strings) {
        /*
        doInBackground that runs the code on a background thread.
        This is where the HTTP call is made and result is turned into a JSON object.
        The URL of the api is given to the execute() method when this task is called. It is strings[0]
         */
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        String data = "";
        JSONObject jsonObject;
        StringBuilder builder = new StringBuilder();
        //Whole operation is engulfed in a try statement.
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
        /*
        runs after executing the background task. Method is called using the return
        of doInBackground. By default passes the json object to the caller's setJson
         */
        super.onPostExecute(object);
        parent.setJson(object);

    }

    public interface JsonTaskInterface{
        void setJson(JSONObject obj);
    }
}
