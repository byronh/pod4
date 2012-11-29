package com.example.Groupup;

import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;

@SuppressLint("SetJavaScriptEnabled")
public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        WebView engine = (WebView) findViewById(R.id.web_engine);  
        WebViewClient webViewClient = new WebViewClient() {
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed(); // Ignore SSL certificate errors
            }
        };

        engine.setWebViewClient(webViewClient);          
        engine .getSettings().setJavaScriptEnabled(true);
        engine .getSettings().setDomStorageEnabled(true);
        engine.loadUrl("https://68.169.60.224:8181/GroupUp_Prototype/");  
//    	String url = "http://www.google.com";
//    	Intent i = new Intent(Intent.ACTION_VIEW);
//    	i.setData(Uri.parse(url));
//    	startActivity(i);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    

    
}
