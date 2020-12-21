package com.mahan.freegamesnotifier;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.RequiresApi;

public class NotificationHelper extends ContextWrapper {

    private static final String CHANNEL_ID = "freeGamesChannel";
    private static final String CHANNEL_NAME = "Free Games Channel";
    private NotificationManager manager;


    @RequiresApi(api = Build.VERSION_CODES.O)
    public NotificationHelper(Context base) {
        super(base);
        createChannels();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createChannels(){

        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        AudioAttributes attr = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                .build();

        NotificationChannel myChannel = new NotificationChannel(CHANNEL_ID,CHANNEL_NAME,NotificationManager.IMPORTANCE_HIGH);
        myChannel.enableLights(true);
        myChannel.enableVibration(true);
        myChannel.setLightColor(Color.CYAN);
        myChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        myChannel.setSound(soundUri,attr);

        getManager().createNotificationChannel(myChannel);
    }

    public NotificationManager getManager() {

        if(manager == null){
            manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }

        return manager;

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Notification.Builder getNotification(String title, String body){

        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);

        return new Notification.Builder(getApplicationContext(),CHANNEL_ID)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),
                        R.drawable.logo))
                .setContentText(body)
                .setContentTitle(title)
                .setSmallIcon(R.drawable.logo)
                .setContentIntent(pIntent)
                .setAutoCancel(true);

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Notification.Builder getNotification(String title, String body, Bitmap img){

        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);

        return new Notification.Builder(getApplicationContext(),CHANNEL_ID)
                .setContentText(body)
                .setContentTitle(title)
                .setLargeIcon(img)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentIntent(pIntent)
                .setAutoCancel(true);

    }

}
