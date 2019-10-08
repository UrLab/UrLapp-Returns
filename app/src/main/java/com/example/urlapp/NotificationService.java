package com.example.urlapp;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;


class NotificationService extends Service {
    private String CHANNEL_ID = "URLAP_CHANNEL";
    private static long CHECK_INTERVAL_MS = 1000;

    public static void register(Context context) {
        Intent service_intent = new Intent(context, NotificationService.class);
        PendingIntent pend_intent = PendingIntent.getService(context, 1,
                service_intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager am = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
        am.setInexactRepeating(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime(), CHECK_INTERVAL_MS, pend_intent);
    }

    protected void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public int onStartCommand (Intent intent, int flags, int startId) {
        createNotificationChannel();
        boolean open;
        try {
            open = isOpen();
        }
        catch (Exception e) {
            open = false;
            Log.e("Bardaf!", e.toString());
        }
        if (open) {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.drawable.urlab_logo)
                    .setContentTitle("News from UrLaB")
                    .setContentText("UrLaB is now open!")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);
            NotificationManagerCompat nmc = NotificationManagerCompat.from(this);
            nmc.notify(4269, builder.build());
        }

        // Start the service again if it is killed
        return START_STICKY;
    }

    @Override
    public void onCreate () {
        createNotificationChannel();
        boolean open = true;
        /*
        try {
            open = isOpen();
        }
        catch (Exception e) {
            open = false;
            Log.e("Bardaf!", e.toString());
        }
        */
        if (open) {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.drawable.urlab_logo)
                    .setContentTitle("News from UrLaB")
                    .setContentText("UrLaB is now open!")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);
            NotificationManagerCompat nmc = NotificationManagerCompat.from(this);
            nmc.notify(4269, builder.build());
        }
    }

    @Override
    public void onDestroy () {
        // TODO: disable alarm (?)
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public JSONObject getAPI() throws Exception {
        URL urlab = new URL("https://urlab.be/spaceapi.json");
        URLConnection ustream = urlab.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(ustream.getInputStream()));

        String inputLine;
        String response = "";

        while ((inputLine = in.readLine()) != null)
            response += inputLine;
        in.close();
        return new JSONObject(response);
    }

    public boolean isOpen() throws Exception {
        JSONObject json = getAPI();
        return json.getJSONObject("state").getBoolean("open");
    }

}

