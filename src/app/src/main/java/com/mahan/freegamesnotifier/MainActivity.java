package com.mahan.freegamesnotifier;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.joooonho.SelectableRoundedImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



public class MainActivity extends AppCompatActivity implements JsonTask.JsonTaskInterface {
    LinearLayout scrollView;

    public static final String EXTRA_IMAGE = "com.mahan.freegamesnotifier.gameImage";
    public static final String EXTRA_TITLE = "com.mahan.freegamesnotifier.postName";
    public static final String EXTRA_DESC = "com.mahan.freegamesnotifier.gameDesc";
    public static final String EXTRA_LINK = "com.mahan.freegamesnotifier.storeLink";

    boolean placeHolderShowing;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        scrollView = findViewById(R.id.imgScroll);
        placeHolderShowing = true;

        new JsonTask(this).execute("https://www.reddit.com/r/freegames/top/.json?t=week");

    }



    public void setImage(final String image, final String name, final String storeLink, final String gameDescription){

        if(placeHolderShowing){
            scrollView.removeAllViews();
            placeHolderShowing = false;
        }

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ConstraintLayout postLayout =  (ConstraintLayout) inflater.inflate(R.layout.post_layout,null);

        SelectableRoundedImageView imgView = postLayout.findViewById(R.id.imageView);
        final TextView textView = postLayout.findViewById(R.id.postName);


        Glide.with(this).load(image).into(imgView);
        textView.setText(name);

        final MainActivity context = this;

        postLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,gameDetail.class);
                intent.putExtra(EXTRA_IMAGE, image);
                intent.putExtra(EXTRA_TITLE,name);
                intent.putExtra(EXTRA_LINK,storeLink);
                intent.putExtra(EXTRA_DESC,gameDescription);
                startActivity(intent);

            }
        });

        scrollView.addView(postLayout);
    }

    public void setJson(JSONObject json) {

        try {
            JSONArray posts = json.getJSONObject("data").getJSONArray("children");

            for(int i = 0; i < posts.length(); i++){
                JSONObject post = posts.getJSONObject(i).getJSONObject("data");
                String title = post.getString("title");
                String link = post.getString("url");

                new JsonTaskRawG(this).execute("https://api.rawg.io/api/games?search=" + title.replace(" ","+"),link);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}
