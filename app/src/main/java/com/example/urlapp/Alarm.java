package com.example.urlapp;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class Alarm extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        boolean open = isOpen();
        String channel_id = context.getString(R.string.channel_id);
        if (open) {
            Log.d("BITEBITE", "BITEBITEBITEBITE");
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channel_id)
                    .setSmallIcon(R.drawable.urlab_logo)
                    .setContentTitle("News from UrLab")
                    .setWhen(System.currentTimeMillis())
                    .setContentText("UrLab is now open!")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true);
            NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            nm.notify(123456, builder.build());
        }
        else {
            Log.d("BITEBITE", "Mais pas pareil");
        }
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

    public boolean isOpen() {
        try {
            JSONObject json = getAPI();
            return json.getJSONObject("state").getBoolean("open");
        }
        catch (Exception e) {
            return false;
        }
    }
}