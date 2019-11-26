package com.example.diy_simulator;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;


public class OrderActivity extends AppCompatActivity {
    private String FCM_API = "https://fcm.googleapis.com/fcm/send";
    private String serverKey =
           "key=" +  " AIzaSyDuojHfyCu4-xWWWjCZje5LKtsXE1XKOLY";
    private String contentType = "application/json";
    private static final String TAG = "mFirebaseIIDService";
    private static final String SUBSCRIBE_TO = "userABC";

    String NOTIFICATION_TITLE;
    String NOTIFICATION_MESSAGE;
    String TOPIC;
    private ProgressDialog pd;

    public RecyclerView order_recyclerview;
    private List<Tab3_Cart_Info> order_item = new ArrayList<>();
    private Order_Info order_confirm_info;
    private List<Order_Product_Info> order_confirm_product_info = new ArrayList<>();
    private OrderInfo_Adapter orderInfoAdapter;

    private LinearLayout linearLayout;
    private RelativeLayout relativeLayout;

    private TextView storename_tv;
    private TextView seller_tv;
    private TextView bank_tv;
    private TextView account_number_tv;
    private ImageButton copy_btn;
    private EditText ename, eaddr, ephone, ememo;

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = database.getReference("판매자");
    private DatabaseReference myRef_customer = database.getReference("구매자");
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        copy_btn = findViewById(R.id.copy_btn);
        linearLayout = findViewById(R.id.order_view);
        relativeLayout = findViewById(R.id.order_confirm_show_account);
        storename_tv = findViewById(R.id.order_storename);
        seller_tv  = findViewById(R.id.order_seller_name);
        bank_tv = findViewById(R.id.order_bank_name);
        account_number_tv = findViewById(R.id.order_account_number);
        ename = findViewById(R.id.order_delivery_orderer_name);
        eaddr = findViewById(R.id.order_delivery_address);
        ephone = findViewById(R.id.order_delivery_phone_number);
        ememo = findViewById(R.id.order_delivery_memo);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

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

        copy_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("Account_Number",account_number_tv.getText()); //클립보드에 ID라는 이름표로 id 값을 복사하여 저장
                clipboardManager.setPrimaryClip(clipData);
                copy_btn.setBackground(getResources().getDrawable(R.drawable.check_mint));
            }
        });

        orderInfoAdapter.setOnItemClickListener(new OrderInfo_Adapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, final int position) {
                linearLayout.setVisibility(View.GONE);
                relativeLayout.setVisibility(View.VISIBLE);
                showProgress();

                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot ds : dataSnapshot.getChildren())
                        {
                            if(!TextUtils.isEmpty(ds.child("storename").getValue().toString()))
                            {
                                if(ds.child("storename").getValue().toString()
                                        .equals(order_item.get(position).getStorename()))
                                {
                                    int order_price = 0;
                                    for(int k = 0; k<order_item.get(position).getIn_items().size(); k++){
                                        Order_Product_Info order_product_info
                                                = new Order_Product_Info(order_item.get(position).getIn_items().get(k).getName(),
                                                order_item.get(position).getIn_items().get(k).getPrice(),
                                                String.valueOf(order_item.get(position).getIn_items().get(k).getAmount()),
                                                order_item.get(position).getIn_items().get(k).getPreview_img_url() );

                                        order_price = order_price + Integer.parseInt(order_item.get(position).getIn_items().get(k).getPrice())
                                                        * order_item.get(position).getIn_items().get(k).getAmount();
                                        order_confirm_product_info.add(order_product_info);
                                    }

                                    bank_tv.setText( "은행 : " + ds.child("bank_name").getValue().toString());
                                    storename_tv.setText("가게이름 : "+ ds.child("storename").getValue().toString());
                                    account_number_tv.setText(ds.child("account_number").getValue().toString());
                                    seller_tv.setText("판매자 이름 : " + ds.child("username").getValue().toString());

                                    order_confirm_info = new Order_Info(order_item.get(position).getStorename(),
                                            order_item.get(position).getDelivery_fee(), String.valueOf(order_price), ds.child("account_number").getValue().toString(),
                                            ds.child("bank_name").getValue().toString(), ename.getText().toString(), eaddr.getText().toString(),
                                            ephone.getText().toString(), ememo.getText().toString(), "주문완료", order_confirm_product_info);

                                    StringTokenizer st = new StringTokenizer(ds.child("email").getValue().toString(), "@");

                                    //TOPIC = "/topics/userABC"; //topic has to match what the receiver subscribed to
                                    TOPIC = "/topics/" + st.nextToken() + st.nextToken();
                                    NOTIFICATION_TITLE = "주문요청";
                                    NOTIFICATION_MESSAGE = firebaseUser.getEmail()+"님이 주문을 요청하였어요!";

                                    final String user_id = firebaseUser.getEmail().substring(0, firebaseUser.getEmail().indexOf("@"));
                                    SimpleDateFormat format1 = new SimpleDateFormat("yyyyMMddHHmmss");
                                    final String format_time1 = format1.format (System.currentTimeMillis());
                                    final String order_number = format_time1 + user_id;
                                    Log.i("주문번호", format_time1 + user_id);

                                    order_confirm_info.setOrder_number(order_number);

                                    //판매자 DB에 주문 정보 저장
                                    myRef.child(ds.getKey()).child("orderinfo").child(order_number).setValue(order_confirm_info);
                                    //구매자 DB에 주문 정보 저장
                                    myRef_customer.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            for(DataSnapshot ds : dataSnapshot.getChildren()){
                                                if(ds.child("email").getValue().toString().equals(firebaseUser.getEmail())){
                                                    myRef_customer.child(ds.getKey()).child("orderinfo").child(order_number).setValue(order_confirm_info);
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });

                                    Log.d("ㅇㅇ",ds.child("email").getValue().toString());
                                    Log.d("ㅇㅇ", TOPIC);
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
                                    break;
                                }
                            }
                        }
                        hideProgress();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

// Get token

    }

    private void sendNotification(JSONObject notification) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(FCM_API, notification,

                new com.android.volley.Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(OrderActivity.this, "주문 신청이 잘 되었어요~ 마이페이지에서 주문상태를 확인해주세요", Toast.LENGTH_LONG).show();
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
            //처리 필요

    }


    // 프로그레스 다이얼로그 보이기
    public void showProgress() {
        if( pd == null ) { // 객체를 1회만 생성한다
            pd = new ProgressDialog(OrderActivity.this, R.style.NewDialog); // 생성한다.
            pd.setCancelable(false); // 백키로 닫는 기능을 제거한다.
        }
        pd.show(); // 화면에 띠워라//
    }
    public void hideProgress() {
        if (pd != null && pd.isShowing()) {
            pd.dismiss();
        }
    }
}
