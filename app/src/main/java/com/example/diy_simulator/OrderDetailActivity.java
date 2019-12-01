package com.example.diy_simulator;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

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

        boolean isSeller = PreferenceUtil.getInstance(OrderDetailActivity.this).getBooleanExtra("isSeller");
        if(isSeller){

            deposit_btn.setVisibility(View.VISIBLE);

            int position = getIntent().getIntExtra("position",0);
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
        }
        else{
            int position = getIntent().getIntExtra("position",0);
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
                                        //myRef.child(ds.getKey()).child(ds1.getKey()).child("orderinfo").child(order_name).child("order_state").setValue("입금 확인 완료");
                                        myRef.child(ds.getKey()).child(ds1.getKey()).child(ds2.getKey()).child("order_state").setValue("입금 확인 완료");
                                        Log.d("뽑기", ds.getKey().toString());
                                        Log.d("뽑기", ds1.getKey().toString());
                                        Log.d("뽑기", ds2.getKey().toString());

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

                                        //myRef_seller.child(ds.getKey()).child(ds1.getKey()).child("orderinfo").child(order_name).child("order_state").setValue("입금 확인 완료");
                                   // return;
                                    //myRef.child(ds.getKey()).child.child("orderinfo").child(order_name).child("order_state").setValue("입금 확인 완료");
                                    //Log.d("뽑기",ds2.getKey().toString());


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
}
