package com.example.summar_v3;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class Web_url extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web_url);

        Bundle bundle = getIntent().getExtras();
        String url = bundle.getString("url" );

        WebView webview = (WebView) findViewById(R.id.webview);
        WebSettings webSettings = webview.getSettings();
        webSettings.setJavaScriptEnabled(true);
        //啟用網頁縮放功能
        webSettings.setSupportZoom( true);
        webSettings.setBuiltInZoomControls( true);
        setContentView(webview);
        webview.setWebViewClient(new WebViewClient());
        webview.loadUrl(url);
    }
}
