package com.example.diy_simulator;


import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

import javax.net.ssl.HttpsURLConnection;
public class MainActivity extends AppCompatActivity {

    URL url = null;
    URLConnection connection = null;
    StringBuilder responseBody = new StringBuilder();
    private  WebView webView;

    private  String url2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        (new HttpPingAsyncTask()).execute("https://pay.toss.im/api/v1/payments");
        webView = (WebView) findViewById(R.id.mainWebView);



    }

    class HttpPingAsyncTask extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... urls) {
            try {
                // HttpURLConnection.setFollowRedirects(false);
                HttpURLConnection connection = (HttpURLConnection) new URL(urls[0]).openConnection();
                //con.setInstanceFollowRedirects(false);

                connection.addRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);
                connection.setDoInput(true);

                JSONObject jsonBody = new JSONObject();
                jsonBody.put("orderNo", "0");
                jsonBody.put("amount", 35000);
                jsonBody.put("amountTaxFree", 0);
                jsonBody.put("productDesc", "토스티셔츠");
                jsonBody.put("apiKey", "sk_test_apikey1234567890");
                jsonBody.put("autoExecute", true);
                jsonBody.put("resultCallback", "http://localhost:3000/auth");
                jsonBody.put("retUrl", "http://YOUR-SITE.COM/ORDER-CHECK?orderno=1");
                jsonBody.put("retCancelUrl", "http://YOUR-SITE.COM/close");

                BufferedOutputStream bos = new BufferedOutputStream(connection.getOutputStream());

                bos.write(jsonBody.toString().getBytes(StandardCharsets.UTF_8));
                bos.flush();
                bos.close();


                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
                String line = null;
                int i= 0;
                while ((line = br.readLine()) != null) {
                    responseBody.append(line + " ");
                    Log.d("ㅇㅇㅇ",i+"");
                    Log.d("rd",line);
                    i++;
                }
                String[] temp = responseBody.toString().split(",");
                Log.d("ㅇㅇ",temp[3]);
                String result = temp[3].replace("checkoutPage\":\"", "");
                result = result.substring(1);
                result = result.substring(0, result.length() - 1);
                Log.d("ㅇㅇ",result);

                final String a = result;
                webView.post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        //webView.setWebViewClient(new TossWebViewClient(MainActivity.this    ));
                        WebSettings settings = webView.getSettings();

                        settings.setJavaScriptEnabled(true);
                        webView.loadUrl(a);

                        //동작
                    }
                });


                responseBody.toString().indexOf("checkoutPage\":\"");

                br.close();
                return (connection.getResponseCode() == HttpURLConnection.HTTP_OK);
            } catch (Exception ex) {
                ex.printStackTrace();
                return false;
            }
        }
    }


}
/*
public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        (new HttpPingAsyncTask()).execute("https:///v1/api/talk/profile");

// Create connection

    }

    static class HttpPingAsyncTask extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... urls) {
            try {
                // HttpURLConnection.setFollowRedirects(false);
                HttpURLConnection con = (HttpURLConnection) new URL(urls[0]).openConnection();
               //con.setInstanceFollowRedirects(false);

                con.setRequestProperty("Host", "kapi.kakao.com");
                con.setRequestProperty("Authorization",
                        "KakaoAK " + "c511b12fb7098c2790ae67d08a01bcb4");
                con.setRequestProperty("Accept",
                        "hathibelagal@example.com");

                if (con.getResponseCode() == 200) {
                    // Success
                    // Further processing here

                    InputStream responseBody = con.getInputStream();
                    InputStreamReader responseBodyReader =
                            new InputStreamReader(responseBody, "UTF-8");
                    JsonReader jsonReader = new JsonReader(responseBodyReader);
                    jsonReader.beginObject(); // Start processing the JSON object
                    while (jsonReader.hasNext()) { // Loop through all keys
                        String key = jsonReader.nextName(); // Fetch the next key
                       // if (key.equals("organization_url")) { // Check if desired key
                            // Fetch the value as a String
                            String value = jsonReader.nextString();
                            Log.d("ㅇㅇ",value);
                            // Do something with the value
                            // ...

                          //  break; // Break out of the loop
                      //  }
                    //else {
                    //        jsonReader.skipValue(); // Skip values of other keys
                   //     }
                    }

                    jsonReader.close();
                    con.disconnect();
                } else {
                    // Error handling code goes here
                }
                return (con.getResponseCode() == HttpURLConnection.HTTP_OK);

            } catch (Exception e) {
                return false;
            }
        }
}}
 */
