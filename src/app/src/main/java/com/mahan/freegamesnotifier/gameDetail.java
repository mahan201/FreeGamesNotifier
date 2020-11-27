package com.mahan.freegamesnotifier;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class gameDetail extends AppCompatActivity {

    ImageView imageView;
    TextView titleView,descView;
    Button storeBtn;

    String image,gameName,gameStore,gameDescription;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_detail);

        Intent intent = getIntent();
        image = intent.getStringExtra(MainActivity.EXTRA_IMAGE);
        gameName = intent.getStringExtra(MainActivity.EXTRA_TITLE);
        gameStore = intent.getStringExtra(MainActivity.EXTRA_LINK);
        gameDescription = intent.getStringExtra(MainActivity.EXTRA_DESC);
        


        imageView = findViewById(R.id.imageView);
        titleView = findViewById(R.id.gameTitle);
        storeBtn = findViewById(R.id.storeBtn);
        descView = findViewById(R.id.gameDesc);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            descView.setText(Html.fromHtml(gameDescription, Html.FROM_HTML_MODE_COMPACT));
        } else {
            descView.setText(Html.fromHtml(gameDescription));
        }

        styleBtn();

        Glide.with(this).load(image).into(imageView);
        titleView.setText(gameName);
    }

    private void styleBtn(){

        String store = getDomain(gameStore);
        Drawable img;

        switch (store){
            case "steampowered":
                storeBtn.setText("GET IT ON STEAM");
                storeBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.steam,0,0,0);
                storeBtn.setBackgroundColor(Color.parseColor("#19415A"));
                break;

            case "epicgames":
                storeBtn.setText("GET IT ON EPIC STORE");
                storeBtn.setBackgroundColor(Color.parseColor("#181A1B"));
                storeBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.epic,0,0,0);
                break;

            case "google":
                storeBtn.setText("DOWNLOAD FROM G PLAY");
                storeBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.gplay,0,0,0);
                storeBtn.setBackgroundColor(Color.parseColor("#50BA6A"));
                break;

            case "ubi":
                storeBtn.setText("GET IT ON UPLAY");
                storeBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ubisoft,0,0,0);
                storeBtn.setBackgroundColor(Color.parseColor("#0071FE"));
                break;
        }
    }

    private String getDomain(String url){
        String data[] = url.split("\\.");
        String domainLabel="";
        if(data.length==3 || data.length==4 || data.length==5){
            domainLabel =data[1];
        }
        else if(data.length==2 ){
            domainLabel =data[0];
        }

        return domainLabel;
    }

    public void goToStore(View view){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(gameStore));
        startActivity(intent);
    }


}
