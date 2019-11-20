package com.example.diy_simulator;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.net.http.SslError;
import android.util.Log;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.net.URISyntaxException;

public class TossWebViewClient extends WebViewClient {

    private Activity activity;

    public TossWebViewClient(Activity activity) {
        this.activity = activity;
    }
    @Override
    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
        handler.proceed(); // SSL 에러가 발생해도 계속 진행!
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        Log.d("ㅇㅇ",url);
/*
        if (!url.startsWith("http://") && !url.startsWith("https://") && !url.startsWith("javascript:")) {
            Intent intent = null;

            try {
                intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME); //IntentURI처리
                Uri uri = Uri.parse(intent.getDataString());

                activity.startActivity(new Intent(Intent.ACTION_VIEW, uri));
                return true;
            } catch (URISyntaxException ex) {
                return false;
            } catch (ActivityNotFoundException e) {
                if ( intent == null )	return false;

                String packageName = intent.getPackage(); //packageName should be com.kakao.talk
                if (packageName != null) {
                    activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName)));
                    return true;
                }

                return false;
            }
        }
*/
        return false;
    }

}
