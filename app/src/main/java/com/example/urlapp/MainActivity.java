package com.example.urlapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.os.StrictMode;
import android.util.Log;

import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;


public class MainActivity extends AppCompatActivity {
    private WebView webview;

    private class MyWebViewClient extends WebViewClient {

        private AppCompatActivity context;

        public MyWebViewClient(AppCompatActivity sup_context) {
            this.context = sup_context;
        }


        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if ("urlab.be".equals(Uri.parse(url).getHost())) {
                // This is my website, so do not override; let my WebView load the page
                return false;
            }
            // Otherwise, the link is not for a page on my site, so launch another Activity that handles URLs
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            context.startActivity(intent);
            return true;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

            StrictMode.setThreadPolicy(policy);

            NotificationService.register(this);

            super.onCreate(savedInstanceState);
            String url = "https://urlab.be";

            setContentView(R.layout.activity_main);

            webview = findViewById(R.id.myWebView);
            webview.setWebViewClient(new MyWebViewClient(this));
            webview.getSettings().setJavaScriptEnabled(true);
            webview.loadUrl(url);
        }
        catch (Exception e) {
            Log.e("Bardouf", e.toString());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
