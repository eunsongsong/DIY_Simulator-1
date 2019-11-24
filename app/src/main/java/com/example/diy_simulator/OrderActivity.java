package com.example.diy_simulator;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class OrderActivity extends AppCompatActivity {
    private String FCM_API = "https://fcm.googleapis.com/fcm/send";
    private String serverKey =
           "key=" +  " AIzaSyDuojHfyCu4-xWWWjCZje5LKtsXE1XKOLY";
    private String contentType = "application/json";
    private static final String TAG = "mFirebaseIIDService";
    private static final String SUBSCRIBE_TO = "userABC";

    EditText edtTitle;
    EditText edtMessage;
    String NOTIFICATION_TITLE;
    String NOTIFICATION_MESSAGE;
    String TOPIC;
    String token;

    public RecyclerView order_recyclerview;
    private List<Tab3_Cart_Info> order_item = new ArrayList<>();
    private List<Tab3_Cart_In_Item_Info> in_item = new ArrayList<>();
    private OrderInfo_Adapter orderInfoAdapter;
    //= new Tab3_Cart_Adapter(getContext(), cart_item, R.layout.tab3_cart_item, Tab3_Cart.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);


        // Adater에 넣을 값 초기화
        Intent intent = getIntent();
        order_item =  (ArrayList<Tab3_Cart_Info> )intent.getSerializableExtra("order_Info");

        orderInfoAdapter = new OrderInfo_Adapter(getApplicationContext(), order_item, R.layout.order_item);
        //리사이클러뷰 리니어 레이아웃 매니저 설정 - vertical
        order_recyclerview = findViewById(R.id.order_recyclerView);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        order_recyclerview.setHasFixedSize(true);
        order_recyclerview.setLayoutManager(layoutManager);
        order_recyclerview.setAdapter(orderInfoAdapter);
        orderInfoAdapter.notifyDataSetChanged();

        /*
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TOPIC = "/topics/userABC"; //topic has to match what the receiver subscribed to
                NOTIFICATION_TITLE = edtTitle.getText().toString();
                NOTIFICATION_MESSAGE = edtMessage.getText().toString();

                JSONObject notification = new JSONObject();
                JSONObject notifcationBody = new JSONObject();
                try {
                    notifcationBody.put("title", NOTIFICATION_TITLE);
                    notifcationBody.put("message", NOTIFICATION_MESSAGE);

                    notification.put("to", TOPIC);
                    notification.put("data", notifcationBody);
                } catch (JSONException e) {
                    Log.e(TAG, "onCreate: " + e.getMessage() );
                }
                sendNotification(notification);
            }
        });


         */


        String topic = "/topics/Enter_your_topic_name"; //topic has to match what the receiver subscribed to"
// Get token

    }

    private void sendNotification(JSONObject notification) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(FCM_API, notification,

                new com.android.volley.Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i(TAG, "onResponse: " + response.toString());
                        edtTitle.setText("");
                        edtMessage.setText("");
                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(OrderActivity.this, "Request error", Toast.LENGTH_LONG).show();
                        Log.i(TAG, "onErrorResponse: Didn't work");
                    }
                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", serverKey);
                params.put("Content-Type", contentType);
                return params;
            }
        };
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
