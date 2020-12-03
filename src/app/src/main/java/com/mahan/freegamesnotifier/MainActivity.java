package com.mahan.freegamesnotifier;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.joooonho.SelectableRoundedImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



public class MainActivity extends AppCompatActivity implements JsonTask.JsonTaskInterface {
    LinearLayout scrollView;
    TextView placeHolder;
    SwipeRefreshLayout refreshLayout;

    public static final String EXTRA_IMAGE = "com.mahan.freegamesnotifier.gameImage";
    public static final String EXTRA_TITLE = "com.mahan.freegamesnotifier.postName";
    public static final String EXTRA_DESC = "com.mahan.freegamesnotifier.gameDesc";
    public static final String EXTRA_LINK = "com.mahan.freegamesnotifier.storeLink";

    boolean placeHolderShowing;
    RequestQueue rQueue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        added some comments and ting
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        scrollView = findViewById(R.id.imgScroll);
        placeHolder = findViewById(R.id.placeHolder);
        rQueue = Volley.newRequestQueue(this);
        refreshLayout = findViewById(R.id.swipeRefresh);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadGames();
                refreshLayout.setRefreshing(false);
            }
        });
        loadGames();

    }

    public void loadGames(){
        placeHolderShowing = true;

        String url = "https://www.reddit.com/r/freegames/top/.json?t=week";

        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            setJson(new JSONObject(response));
                        } catch (JSONException e) {
                            placeHolder.setText("Failed to get games.");
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                placeHolder.setText("Failed to get games.");
            }
        });

        rQueue.add(request);
    }



    public void setImage(final String image, final String name, final String storeLink, final String gameDescription){
        /*
        This method is called by JsonTaskRawG which provides the information for a given game.
         */

        //Removes the placeholder
        if(placeHolderShowing){
            scrollView.removeAllViews();
            placeHolderShowing = false;
        }

        //Inflates a pre-made postLayout and sets the values for image view and text view.
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ConstraintLayout postLayout =  (ConstraintLayout) inflater.inflate(R.layout.post_layout,null);

        SelectableRoundedImageView imgView = postLayout.findViewById(R.id.imageView);
        final TextView textView = postLayout.findViewById(R.id.postName);

        //Using the Glide library, load the image. Set game name.
        Glide.with(this).load(image).into(imgView);
        textView.setText(name);

        final MainActivity context = this;

        //Start a new intent when the game layout is clicked
        postLayout.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                NotificationHelper n = new NotificationHelper(context);
                n.getNotification("MyTitle","MyString");
                Intent intent = new Intent(context,gameDetail.class);
                intent.putExtra(EXTRA_IMAGE, image);
                intent.putExtra(EXTRA_TITLE,name);
                intent.putExtra(EXTRA_LINK,storeLink);
                intent.putExtra(EXTRA_DESC,gameDescription);
                startActivity(intent);

            }
        });

        //Add the game to the view.
        scrollView.addView(postLayout);
    }

    public void setJson(JSONObject json) {
        /*
        Method implemented for the JsonTask Class to use.
        Handles the json file returned by JsonTask.
        In this case it goes through post inside "children" array and turns it into a game.
         */

        try {
            JSONArray posts = json.getJSONObject("data").getJSONArray("children");

            for(int i = 0; i < posts.length(); i++){
                JSONObject post = posts.getJSONObject(i).getJSONObject("data");
                String title = post.getString("title");
                final String link = post.getString("url");

                String url = "https://api.rawg.io/api/games?search="+ title.replace(" ","+");

                StringRequest request = new StringRequest(Request.Method.GET, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                                try {
                                    JSONObject jsonObject = new JSONObject(response);

                                    final String imgURL = jsonObject.getJSONArray("results")
                                            .getJSONObject(0)
                                            .getString("background_image");

                                    final String name = jsonObject.getJSONArray("results")
                                            .getJSONObject(0)
                                            .getString("name");
                                    String url2 = "https://api.rawg.io/api/games/" +
                                            jsonObject.getJSONArray("results")
                                                    .getJSONObject(0).getString("slug");

                                    StringRequest request2 = new StringRequest(Request.Method.GET,
                                            url2,
                                            new Response.Listener<String>() {
                                                @Override
                                                public void onResponse(String response) {
                                                    JSONObject jsonObject2 = null;
                                                    try {
                                                        jsonObject2 = new JSONObject(response);
                                                        String gameDescription = jsonObject2.getString("description_raw");
                                                        setImage(imgURL,name,link,gameDescription);
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }

                                                }
                                            }, new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {

                                        }
                                    });

                                    rQueue.add(request2);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }


                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

                rQueue.add(request);

            }

        } catch (JSONException e) {
            placeHolder.setText("Failed to get games.");
        }

    }

}
