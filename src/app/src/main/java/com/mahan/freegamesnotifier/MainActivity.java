package com.mahan.freegamesnotifier;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    LinearLayout scrollView;
    TextView placeHolder;
    SwipeRefreshLayout refreshLayout;

    public static final String EXTRA_IMAGE1 = "com.mahan.freegamesnotifier.gameImage1";
    public static final String EXTRA_IMAGE2 = "com.mahan.freegamesnotifier.gameImage2";
    public static final String EXTRA_TITLE = "com.mahan.freegamesnotifier.postName";
    public static final String EXTRA_DESC = "com.mahan.freegamesnotifier.gameDesc";
    public static final String EXTRA_LINK = "com.mahan.freegamesnotifier.storeLink";

    boolean placeHolderShowing;
    RequestQueue rQueue;
    int postCount,viewCount;

    ArrayList<String[]> gamesList;
    SharedPreferences sharedPreferences;



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

        postCount = 0;
        gamesList = new ArrayList<>();
        sharedPreferences = getSharedPreferences("com.mahan.freegamesnotifier",MODE_PRIVATE);

        loadGames();
    }

    public void loadGames(){
        placeHolder.setText("Fetching Games.");
        placeHolderShowing = true;

        try {
            String serialized = sharedPreferences.getString("oldPosts",ObjectSerializer.serialize(new ArrayList<String[]>()));
            final ArrayList<String[]> oldPosts = (ArrayList<String[]>) ObjectSerializer.deserialize(serialized);
            String url = "https://www.reddit.com/r/freegames/top/.json?t=week";

            StringRequest request = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                setJson(oldPosts,new JSONObject(response));
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setJson(final ArrayList<String[]> savedPosts, final JSONObject json) {
        /*
        Handles the json file returned by Volley request.
        In this case it goes through post inside "children" array and turns it into a game.
         */


        try {
            final JSONArray posts = json.getJSONObject("data").getJSONArray("children");
            List<Object> list = separatePosts(savedPosts,posts);

            ArrayList<String[]> oldPosts = (ArrayList<String[]>) list.get(0);
            JSONArray newPosts = (JSONArray) list.get(1);

            postCount = oldPosts.size() + newPosts.length();

            //FOR OLD POSTS
            for (int i = 0; i < oldPosts.size(); i++) {
                String[] post = oldPosts.get(i);
                System.out.println("OLD POST: " + post[0]);
                setImage(post[0],post[1],post[2],post[3],post[4],post[5]);
            }

            //FOR NEW POSTS
            for(int i = 0; i < newPosts.length(); i++){
                JSONObject post = newPosts.getJSONObject(i).getJSONObject("data");
                final String title = post.getString("title");
                final String link = post.getString("url");

                System.out.println("NEW POST: " + title);

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

                                                        final String imgURL2;
                                                        if (jsonObject2.has("background_image_additional")) {

                                                            imgURL2 = jsonObject2.getString("background_image_additional");
                                                        }
                                                        else{imgURL2 = "NULL";}

                                                        setImage(title,name,imgURL,imgURL2,gameDescription,link);
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

    public void setImage(final String redditName, final String name, final String image, final String image2, final String gameDescription, final String storeLink){

        gamesList.add(new String[]{redditName,name,image,image2,gameDescription,storeLink});
        viewCount += 1;

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
                intent.putExtra(EXTRA_IMAGE1, image);
                intent.putExtra(EXTRA_IMAGE2,image2);
                intent.putExtra(EXTRA_TITLE,name);
                intent.putExtra(EXTRA_LINK,storeLink);
                intent.putExtra(EXTRA_DESC,gameDescription);
                startActivity(intent);

            }
        });

        //Add the game to the view.
        scrollView.addView(postLayout);

        if(viewCount==postCount){storeGames();}

    }

    private void storeGames() {
        try {
            String serialized = ObjectSerializer.serialize(gamesList);
            sharedPreferences.edit().putString("oldPosts",serialized).apply();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private List<Object> separatePosts(ArrayList<String[]> oldPosts,JSONArray newPosts){
        ArrayList<String[]> oldPostsInNew = new ArrayList<>();

        try {

            for(int i=0;i<newPosts.length();i++){
                JSONObject post =  newPosts.getJSONObject(i);
                String postTitle = post.getJSONObject("data").getString("title");
                for(int j=0; j<oldPosts.size();j++){
                    String title = oldPosts.get(j)[0];
                    if(title.equals(postTitle)){
                        oldPostsInNew.add(oldPosts.get(j));
                        newPosts.remove(i);
                        i--;
                        break;
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return Arrays.asList(oldPostsInNew,newPosts);
    }
}
