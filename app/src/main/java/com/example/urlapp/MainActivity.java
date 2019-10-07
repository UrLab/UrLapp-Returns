package com.example.urlapp;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import java.net.URLConnection;
import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.json.JSONObject;

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
