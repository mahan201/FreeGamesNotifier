package com.mahan.freegamesnotifier;

import android.app.Notification;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

public class GameNotiJob extends JobService {
    private boolean isCancelled;

    @Override
    public boolean onStartJob(JobParameters params) {
        doBackgroundTask(params);
        return true;
    }

    private void doBackgroundTask(final JobParameters params){
        if(isCancelled){return;}
        final RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = "https://www.reddit.com/r/freegames/top/.json?t=week";

        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject resp = new JSONObject(response);
                            String title = resp.getJSONObject("data").getJSONArray("children").getJSONObject(0).getJSONObject("data").getString("title");
                            String url = "https://api.rawg.io/api/games?search="+ title.replace(" ","+");
                            StringRequest request1 = new StringRequest(Request.Method.GET,
                                    url,
                                    new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            try {
                                                JSONObject jsonObject = new JSONObject(response);
                                                final String name = jsonObject.getJSONArray("results")
                                                        .getJSONObject(0)
                                                        .getString("name");
                                                compareTopPost(params,name);
                                            } catch (JSONException e) {
                                                jobFinished(params,true);
                                            }
                                        }
                                    },
                                    new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            jobFinished(params, true);
                                        }
                                    });

                            requestQueue.add(request1);
                        } catch (JSONException e) {
                            jobFinished(params,true);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                jobFinished(params, true);
            }
        });



        requestQueue.add(request);
    }

    private void compareTopPost(JobParameters params ,String title){
        if(isCancelled){return;}
        SharedPreferences sharedPreferences = getSharedPreferences("com.mahan.freegamesnotifier",MODE_PRIVATE);
        String prev = sharedPreferences.getString("LastTopPost","");

        if(!title.equals(prev)){
            NotificationHelper helper = new NotificationHelper(this);
            Notification.Builder builder = helper.getNotification(title,"FREE NOW!");
            helper.getManager().notify(new Random().nextInt(),builder.build());
        }
        sharedPreferences.edit().putString("LastTopPost",title).apply();

        jobFinished(params,false);

    }

    @Override
    public boolean onStopJob(JobParameters params) {
        isCancelled = true;
        return true;
    }
}
