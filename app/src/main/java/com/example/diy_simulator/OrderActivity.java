package com.example.diy_simulator;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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
import java.util.Arrays;
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

    String order_number;
    String uni_num, amount;

    String NOTIFICATION_TITLE;
    String NOTIFICATION_MESSAGE;
    String TOPIC;
    private ProgressDialog pd;

    public RecyclerView order_recyclerview;
    private List<Tab3_Cart_Info> order_item = new ArrayList<>();
    private List<Order_Info> order_confirm_info  = new ArrayList<>();
    private List<Order_Product_Info> order_confirm_product_info;
    private OrderInfo_Adapter orderInfoAdapter;

    public RecyclerView order_complete_recyclerview;
    private List<Order_Complete_Info> complete_item = new ArrayList<>();
    private Order_Complete_Adapter completeAdapter;

    private LinearLayout linearLayout;
    private RelativeLayout relativeLayout;

    private ImageButton copy_btn;
    private EditText ename, eaddr, ephone, ememo;
    private Button proceed_btn;
    int position;  //order_item 인덱스

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = database.getReference("판매자");
    private DatabaseReference myRef_customer = database.getReference("구매자");
    private DatabaseReference myRef_material = database.getReference("부자재");

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        copy_btn = findViewById(R.id.copy_btn);
        linearLayout = findViewById(R.id.order_view);
        relativeLayout = findViewById(R.id.order_confirm_show_account);
        ename = findViewById(R.id.order_delivery_orderer_name);
        eaddr = findViewById(R.id.order_delivery_address);
        ephone = findViewById(R.id.order_delivery_phone_number);
        ememo = findViewById(R.id.order_delivery_memo);
        proceed_btn = findViewById(R.id.order_proceed_btn);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();


        //툴바 뒤로가기 버튼 설정
        Toolbar tb = findViewById(R.id.order_toolbar) ;
        setSupportActionBar(tb) ;
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);


        // Adater에 넣을 값 초기화
        Intent intent = getIntent();
        order_item =  (ArrayList<Tab3_Cart_Info> )intent.getSerializableExtra("order_Info");
        // Tab3에서 받아온 order item 의 각 부자재 고유 번호, 수량
        uni_num = intent.getStringExtra("order_material_number");
        amount = intent.getStringExtra("order_material_amount");
        if(uni_num.endsWith("#")) uni_num = uni_num.substring(0, uni_num.length()-1);
        if(amount.endsWith("#")) amount = amount.substring(0, amount.length()-1);

        orderInfoAdapter = new OrderInfo_Adapter(getApplicationContext(), order_item, R.layout.order_item);
        //리사이클러뷰 리니어 레이아웃 매니저 설정 - vertical
        order_recyclerview = findViewById(R.id.order_recyclerView);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        order_recyclerview.setHasFixedSize(true);
        order_recyclerview.setLayoutManager(layoutManager);
        order_recyclerview.setAdapter(orderInfoAdapter);
        orderInfoAdapter.notifyDataSetChanged();


        // 구매 완료후 판매자 정보 및 계좌 뜨는 리사이클러뷰
        completeAdapter = new Order_Complete_Adapter(getApplicationContext(), complete_item, R.layout.order_complete_item);
        //리사이클러뷰 리니어 레이아웃 매니저 설정 - vertical
        order_complete_recyclerview = findViewById(R.id.order_complete_recyclerView);
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(getApplicationContext());
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        order_complete_recyclerview.setHasFixedSize(true);
        order_complete_recyclerview.setLayoutManager(layoutManager2);
        order_complete_recyclerview.setAdapter(completeAdapter);


        // 고객이 회원 가입시 입력한 배송 정보 띄우기
        myRef_customer.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    if(ds.child("email").getValue().toString().equals(firebaseUser.getEmail())){
                        ename.setText(ds.child("username").getValue().toString());
                        eaddr.setText(ds.child("address").getValue().toString());
                        ephone.setText(ds.child("phonenumber").getValue().toString());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
/*
        //계좌번호 복사 버튼
        copy_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("Account_Number",account_number_tv.getText()); //클립보드에 ID라는 이름표로 id 값을 복사하여 저장
                clipboardManager.setPrimaryClip(clipData);
                copy_btn.setBackground(getResources().getDrawable(R.drawable.check_mint));
            }
        });

 */

        //주문 진행
        proceed_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linearLayout.setVisibility(View.GONE);
                relativeLayout.setVisibility(View.VISIBLE);
                showProgress();

                decreaseMaterialStock();

                position = 0;
                myRef.orderByChild("storename").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot ds : dataSnapshot.getChildren())
                        {
                            if(!TextUtils.isEmpty(String.valueOf(ds.child("storename").getValue())))
                            {
                                if(position >= order_item.size()) break;
                                if(order_item.get(position).getStorename()
                                        .equals(String.valueOf(ds.child("storename").getValue())))
                                {
                                    order_confirm_product_info = new ArrayList<>();
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

                                    Order_Complete_Info item = new Order_Complete_Info(ds.child("storename").getValue().toString(),
                                            ds.child("username").getValue().toString(), ds.child("phonenumber").getValue().toString(),
                                            String.valueOf(order_price + Integer.parseInt(order_item.get(position).getDelivery_fee())),
                                            ds.child("bank_name").getValue().toString(), ds.child("account_number").getValue().toString());
                                    complete_item.add(item);
                                    completeAdapter.notifyDataSetChanged();

                                    Order_Info order_info = new Order_Info(order_item.get(position).getStorename(),
                                            order_item.get(position).getDelivery_fee(), String.valueOf(order_price), ds.child("account_number").getValue().toString(),
                                            ds.child("bank_name").getValue().toString(), ename.getText().toString(), eaddr.getText().toString(),
                                            ephone.getText().toString(), ememo.getText().toString(), "주문완료(입금대기)", order_confirm_product_info);
                                    order_confirm_info.add(order_info);

                                    /*
                                    customer_order_confirm_info = new Order_Info(order_item.get(position).getStorename(),
                                        order_item.get(position).getDelivery_fee(), String.valueOf(order_price), ds.child("account_number").getValue().toString(),
                                        ds.child("bank_name").getValue().toString(), ename.getText().toString(), eaddr.getText().toString(),
                                        ephone.getText().toString(), ememo.getText().toString(), "주문완료(입금대기)", customer_order_confirm_product_info);

                                     */

                                    StringTokenizer st = new StringTokenizer(ds.child("email").getValue().toString(), "@");

                                    //TOPIC = "/topics/userABC"; //topic has to match what the receiver subscribed to
                                    TOPIC = "/topics/" + st.nextToken() + st.nextToken();
                                    NOTIFICATION_TITLE = "주문요청";
                                    NOTIFICATION_MESSAGE = firebaseUser.getEmail()+"님이 주문을 요청하였어요!";

                                    final String user_id = firebaseUser.getEmail().substring(0, firebaseUser.getEmail().indexOf("@"));
                                    SimpleDateFormat format1 = new SimpleDateFormat("yyyyMMddHHmmss");
                                    final String format_time1 = format1.format (System.currentTimeMillis());
                                    order_number = format_time1 + user_id;
                                    Log.i("주문번호", format_time1 + user_id);

                                    order_confirm_info.get(position).setOrder_number(order_number+position);

                                    //판매자 DB에 주문 정보 저장
                                    myRef.child(ds.getKey()).child("orderinfo").child(order_number+position).setValue(order_confirm_info.get(position));

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
                                    position++;
                                }
                            }
                        }
                        setCustomerOrderInfo();
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
                        Toast.makeText(OrderActivity.this, "주문이 완료되었습니다. 마이페이지에서 주문 상태를 확인해주세요.", Toast.LENGTH_LONG).show();
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


    private void setCustomerOrderInfo() {
        //구매자 DB에 주문 정보 저장, 장바구니 DB 갱신
        myRef_customer.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (ds.child("email").getValue().toString().equals(firebaseUser.getEmail())) {
                        for(int i = 0; i<position; i++){
                            myRef_customer.child(ds.getKey()).child("orderinfo").child(order_number + i).setValue(order_confirm_info.get(i));
                        }
                        String cart = ds.child("cart").getValue().toString();
                        cart = deleteOrderItemFromCart(cart);
                        myRef_customer.child(ds.getKey()).child("cart").setValue(cart);
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //카트에서 주문한 아이템 삭제
    private String deleteOrderItemFromCart(String cart){
        //List<String> cart_list = Arrays.asList(cart.split("#"));
        String[] cart_arr = cart.split("#");
        ArrayList<String> cart_list = new ArrayList<>(Arrays.asList(cart_arr));
        List<String> uni_list = Arrays.asList(uni_num.split("#"));
        for(int i=0; i<uni_list.size(); i++){
            cart_list.remove(uni_list.get(i));
        }
        String result = "";
        for(int k=0; k<cart_list.size(); k++){
            result = result + cart_list.get(k) + "#";
        }
        if(result.endsWith("#")) result = result.substring(0, result.length()-1);

        return  result;
    }

    // 부자재 재고 감소
    private void decreaseMaterialStock(){
        final String[] uni_nums = uni_num.split("#");
        final String[] amount_s = amount.split("#");
        myRef_material.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int i=0;
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    if(i == uni_nums.length) break;
                    if(ds.getKey().equals(uni_nums[i])){
                        int stock = Integer.parseInt(ds.child("stock").getValue().toString());
                        myRef_material.child(ds.getKey()).child("stock").setValue(String.valueOf(stock - Integer.parseInt(amount_s[i])));
                        i++;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(OrderActivity.this, MainTabActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("page_num",2);
        startActivity(intent);
        finish();
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


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{ //toolbar의 back키 눌렀을 때 동작
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
