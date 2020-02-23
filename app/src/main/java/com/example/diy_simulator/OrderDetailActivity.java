package com.example.diy_simulator;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import static com.example.diy_simulator.Tab2_MyPage_Customer.mypage_order_item;
import static com.example.diy_simulator.Tab2_MyPage_Seller.mypage_seller_order_item;

public class OrderDetailActivity extends AppCompatActivity {

    private String order_name;
    public RecyclerView order_detail_recyclerView;
    public List<Order_Product_Info> in_order_item = new ArrayList<>();
    private final Order_Detail_Adapter oderDetailAdapter = new Order_Detail_Adapter(OrderDetailActivity.this, in_order_item, R.layout.order_in_item);

    private Button deposit_btn;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = database.getReference("구매자");
    private DatabaseReference myRef_seller = database.getReference("판매자");

    private ImageButton send_btn;
    private ImageButton msg_content_btn;

    private RecyclerView msg_content_recyclerview;
    private List<Message_Info> message_infos = new ArrayList<>();
    private Message_Adapter message_adapter;
    private Activity activity;
    private View view;

    AlertDialog.Builder ad;
    // EditText 삽입하기
    EditText et;
    private String FCM_API = "https://fcm.googleapis.com/fcm/send";
    private String serverKey =
            "key=" +  " AIzaSyDuojHfyCu4-xWWWjCZje5LKtsXE1XKOLY";
    private String contentType = "application/json";
    private static final String TAG = "mFirebaseIIDService";
    String NOTIFICATION_TITLE;
    String NOTIFICATION_MESSAGE;
    String TOPIC;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    ProgressDialog pd;
    private String receiver_email;
    private final int m_nMaxLengthOfDeviceName = 16;
    private int position;
    private boolean isSeller;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        activity = this;
        message_adapter = new Message_Adapter(activity, message_infos, R.layout.msg_recycle_item);
        position = getIntent().getIntExtra("position",0);
        //메세지 보내기 처리
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        showProgress();

        //리사이클러뷰 레이아웃 매니저 설정
        order_detail_recyclerView = findViewById(R.id.order_detail_items_recyclerView);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        order_detail_recyclerView.setHasFixedSize(true);
        order_detail_recyclerView.setLayoutManager(layoutManager);
        order_detail_recyclerView.setAdapter(oderDetailAdapter);

        //툴바 설정
        Toolbar tb = findViewById(R.id.order_detail_toolbar) ;
        setSupportActionBar(tb) ;
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);

        TextView order_number = findViewById(R.id.order_detail_order_number);
        TextView storename = findViewById(R.id.order_detail_order_storename);
        TextView price = findViewById(R.id.order_detail_order_price);
        TextView fee = findViewById(R.id.order_detail_order_delivery_fee);
        final TextView state = findViewById(R.id.order_detail_order_state);
        TextView bank = findViewById(R.id.order_detail_order_bank);
        TextView account = findViewById(R.id.order_detail_order_account);
        TextView recipient = findViewById(R.id.order_detail_order_recipient);
        TextView des = findViewById(R.id.order_detail_order_destination);
        TextView phone = findViewById(R.id.order_detail_order_phone);
        TextView memo = findViewById(R.id.order_detail_order_delivery_memo);

        deposit_btn = findViewById(R.id.deposit_btn);
        send_btn = findViewById(R.id.send_img_btn);
        msg_content_btn = findViewById(R.id.message_content_btn);

        isSeller = PreferenceUtil.getInstance(OrderDetailActivity.this).getBooleanExtra("isSeller");
        if(isSeller){

            deposit_btn.setVisibility(View.VISIBLE);

            for(int i=0; i<mypage_seller_order_item.get(position).getOrder_items().size(); i++)
                in_order_item.add(mypage_seller_order_item.get(position).getOrder_items().get(i));
            oderDetailAdapter.notifyDataSetChanged();

            order_number.setText("주문번호 " + mypage_seller_order_item.get(position).getOrder_number());
            storename.setText("상점 " + mypage_seller_order_item.get(position).getStorename());
            price.setText("주문 금액 " + mypage_seller_order_item.get(position).getOrder_price());
            fee.setText("배송비 " + mypage_seller_order_item.get(position).getDelivery_fee());
            state.setText("주문 상태 " + mypage_seller_order_item.get(position).getOrder_state());
            bank.setText("계좌 정보 " + mypage_seller_order_item.get(position).getBank_name());
            account.setText(mypage_seller_order_item.get(position).getAccount_number());
            recipient.setText("수령인 " + mypage_seller_order_item.get(position).getDelivery_recipient());
            des.setText("배송지 " + mypage_seller_order_item.get(position).getDelivery_destination());
            phone.setText("전화번호 " + mypage_seller_order_item.get(position).getDelivery_phone());
            memo.setText("배송 메모 " + mypage_seller_order_item.get(position).getDelivery_memo());

            order_name = mypage_seller_order_item.get(position).getOrder_number();

            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        for (DataSnapshot ds1 : ds.getChildren())
                            for (DataSnapshot ds2 : ds1.getChildren())
                                if (ds2.getKey().equals(order_name)) {
                                    receiver_email = ds.child("email").getValue().toString();
                                    setEditAlertDialog(false);
                                    hideProgress();
                                    break;
                                }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        else{
            for(int i=0; i<mypage_order_item.get(position).getOrder_items().size(); i++)
                in_order_item.add(mypage_order_item.get(position).getOrder_items().get(i));
            oderDetailAdapter.notifyDataSetChanged();

            order_number.setText("주문번호 " + mypage_order_item.get(position).getOrder_number());
            storename.setText("상점 " + mypage_order_item.get(position).getStorename());
            price.setText("주문 금액 " + mypage_order_item.get(position).getOrder_price());
            fee.setText("배송비 " + mypage_order_item.get(position).getDelivery_fee());
            state.setText("주문 상태 " + mypage_order_item.get(position).getOrder_state());
            bank.setText("계좌 정보 " + mypage_order_item.get(position).getBank_name());
            account.setText(mypage_order_item.get(position).getAccount_number());
            recipient.setText("수령인 " + mypage_order_item.get(position).getDelivery_recipient());
            des.setText("배송지 " + mypage_order_item.get(position).getDelivery_destination());
            phone.setText("전화번호 " + mypage_order_item.get(position).getDelivery_phone());
            memo.setText("배송 메모 " + mypage_order_item.get(position).getDelivery_memo());
            order_name = mypage_order_item.get(position).getOrder_number();

            myRef_seller.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        for (DataSnapshot ds1 : ds.getChildren())
                            for (DataSnapshot ds2 : ds1.getChildren())
                                if (ds2.getKey().equals(order_name)) {
                                    receiver_email = ds.child("email").getValue().toString();
                                    setEditAlertDialog(false);
                                    hideProgress();
                                    break;
                                }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        deposit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot ds : dataSnapshot.getChildren())
                        {
                            for (DataSnapshot ds1 : ds.getChildren()){
                                for(DataSnapshot ds2 : ds1.getChildren()) {
                                    if (ds2.getKey().equals(order_name)) {
                                        myRef.child(ds.getKey()).child(ds1.getKey()).child(ds2.getKey()).child("order_state").setValue("입금 확인 완료");
                                    }
                                }

                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                myRef_seller.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot ds : dataSnapshot.getChildren())
                        {
                            for (DataSnapshot ds1 : ds.getChildren()){
                                for(DataSnapshot ds2 : ds1.getChildren()) {
                                    if (ds2.getKey().equals(order_name)) {
                                        myRef_seller.child(ds.getKey()).child(ds1.getKey()).child(ds2.getKey()).child("order_state").setValue("입금 확인 완료");
                                        state.setText("주문 상태 입금 확인 완료");
                                    }
                                }

                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });


        send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ad.show();
                //TOPIC = "/topics/userABC"; //topic has to match what the receiver subscribed to
              //  TOPIC = "/topics/" + st.nextToken() + st.nextToken();
            }
        });

        msg_content_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createMessageInfo();
            }
        });
    }

    //EditText Dialog
    private void setEditAlertDialog(boolean msg){

        if(msg) {
            if (et.getParent() != null) {
                ((ViewGroup) et.getParent()).removeView(et);
                ad = new AlertDialog.Builder(OrderDetailActivity.this);
                et = new EditText(OrderDetailActivity.this);
                et.setHint("16자이내로 전송 가능합니다.");
                ad.setView(et);
            }
        }
        else{
            ad = new AlertDialog.Builder(OrderDetailActivity.this);
            et = new EditText(OrderDetailActivity.this);
            et.setHint("16자이내로 전송 가능합니다.");
            ad.setView(et);
        }

        //et.setInputType(InputType.TYPE_CLASS_TEXT);
        et.setSingleLine();
        et.setFilters(new InputFilter[] { new InputFilter.LengthFilter(m_nMaxLengthOfDeviceName) });
        ad.setTitle("쪽지 보내기");       // 제목 설정
        ad.setMessage(receiver_email+"님에게 쪽지를 보냅니다.");   // 내용 설정
        ad.setCancelable(false);
        ad.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                // Text 값 받아서 로그 남기기
                StringTokenizer st = new StringTokenizer(receiver_email, "@");
                TOPIC =  "/topics/"+ st.nextToken() + st.nextToken();
                if(isSeller)
                    NOTIFICATION_TITLE = mypage_seller_order_item.get(position).getStorename() + "으로부터 쪽지가 도착했습니다.";
                else
                    NOTIFICATION_TITLE = firebaseUser.getEmail() + "님으로부터 쪽지가 도착했습니다.";

                NOTIFICATION_MESSAGE = et.getText().toString();
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

                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot ds : dataSnapshot.getChildren())
                        {
                            if(isSeller){
                                if(ds.child("email").getValue().equals(receiver_email)) {
                                    myRef.child(ds.getKey()).child("orderinfo").child(order_name).child("msginfo").push().setValue(new Message_Info("판매자",NOTIFICATION_MESSAGE,getCurrentTime()));
                                    dialog.dismiss();     //닫기
                                    showProgress();
                                }
                            }
                            else
                            {
                                if(ds.child("email").getValue().equals(firebaseUser.getEmail())) {
                                    myRef.child(ds.getKey()).child("orderinfo").child(order_name).child("msginfo").push().setValue(new Message_Info("구매자",NOTIFICATION_MESSAGE,getCurrentTime()));
                                    dialog.dismiss();     //닫기
                                    showProgress();
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


                // Event
            }
        });
        // 취소 버튼 설정
        ad.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();     //닫기
                // Event
                setEditAlertDialog(true);
            }
        });
    }

    private void sendNotification(JSONObject notification) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(FCM_API, notification,

                new com.android.volley.Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(OrderDetailActivity.this, "메세지가 전송 되었어요~", Toast.LENGTH_LONG).show();
                        setEditAlertDialog(true);
                        hideProgress();
                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(OrderDetailActivity.this, "Request error", Toast.LENGTH_LONG).show();
                        setEditAlertDialog(true);
                        hideProgress();
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

    private void createMessageInfo(){
        view = activity.getLayoutInflater().inflate(R.layout.msg_recyclerview_layout, null);
        msg_content_recyclerview = (RecyclerView) view.findViewById(R.id.msg_recycler);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        msg_content_recyclerview.setHasFixedSize(true);
        msg_content_recyclerview.setLayoutManager(layoutManager);
        msg_content_recyclerview.setAdapter(message_adapter);

        final ImageView empty_img = (ImageView) view.findViewById(R.id.empty_img);


        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                message_infos.clear();
                for(DataSnapshot ds : dataSnapshot.getChildren())
                {
                    for (DataSnapshot ds1 : ds.child("orderinfo").getChildren()){

                        if (ds1.getKey().equals(order_name)) {
                            for(DataSnapshot ds2 :ds1.getChildren())
                            {
                                if(ds2.getKey().equals("msginfo"))
                                {
                                    for(DataSnapshot ds3: ds2.getChildren()){
                                        if(ds3.child("who").getValue().equals("판매자")){ //판매자가 남긴 쪽지를 읽은 경우
                                            if(isSeller) //사용자가 판매자인경우
                                                message_infos.add(new Message_Info("보낸 쪽지",ds3.child("msg_content").getValue().toString()
                                                        ,ds3.child("time").getValue().toString()));
                                            else
                                            {
                                                message_infos.add(new Message_Info("받은 쪽지",ds3.child("msg_content").getValue().toString()
                                                        ,ds3.child("time").getValue().toString()));
                                            }
                                        }
                                        else{ //구매자가 남긴 쪽지를 읽은 경우
                                            if(isSeller) //사용자가 판매자인경우
                                                message_infos.add(new Message_Info("받은 쪽지",ds3.child("msg_content").getValue().toString()
                                                        ,ds3.child("time").getValue().toString()));
                                            else
                                            {
                                                message_infos.add(new Message_Info("보낸 쪽지",ds3.child("msg_content").getValue().toString()
                                                        ,ds3.child("time").getValue().toString()));
                                            }
                                        }
                                    }
                                }
                            }

                        }
                    }
                }

                // 다이얼로그 생성
                AlertDialog.Builder listViewDialog = new AlertDialog.Builder(activity);
                // 리스트뷰 설정된 레이아웃
                listViewDialog.setView(view);
                // 확인버튼
                listViewDialog.setPositiveButton("확인", null);


                message_adapter.notifyDataSetChanged();

                if(message_infos.size() == 0){
                    msg_content_recyclerview.setVisibility(View.GONE);
                    empty_img.setVisibility(View.VISIBLE);
                    listViewDialog.setTitle("쪽지함이 비어있습니다.");
                }
                else {
                    msg_content_recyclerview.setVisibility(View.VISIBLE);
                    empty_img.setVisibility(View.GONE);
                    listViewDialog.setTitle("쪽지함");
                }
                msg_content_recyclerview.scrollToPosition(message_infos.size() - 1);
                // 다이얼로그 보기
                listViewDialog.show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }

    public String getCurrentTime(){
        // 현재시간을 msec 으로 구한다.
        long now = System.currentTimeMillis();
        // 현재시간을 date 변수에 저장한다.
        Date date = new Date(now);
        // 시간을 나타냇 포맷을 정한다 ( yyyy/MM/dd 같은 형태로 변형 가능 )
        SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        // nowDate 변수에 값을 저장한다.

        String formatDate = sdfNow.format(date);

        return formatDate;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{ //toolbar의 back키 눌렀을 때 동작
                //super.onBackPressed();
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.exit_to_right);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.exit_to_right);
    }

    // 프로그레스 다이얼로그 보이기
    public void showProgress() {
        if( pd == null ) { // 객체를 1회만 생성한다
            pd = new ProgressDialog(OrderDetailActivity.this, R.style.NewDialog); // 생성한다.
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
