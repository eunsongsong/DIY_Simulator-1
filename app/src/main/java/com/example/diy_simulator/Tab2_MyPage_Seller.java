package com.example.diy_simulator;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

public class Tab2_MyPage_Seller extends Fragment {

    FirebaseAuth firebaseAuth;
    FirebaseUser mFirebaseUser;
    TextView sellerName;
    TextView sellerStorename;
    TextView sellerEmail;
    TextView sellerAddress;
    TextView sellerPhonenumber;
    TextView sellerBank;
    TextView sellerAccount;
    TextView sellerDelivery;

    Button sellerSignout;
    Button sellerModify;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("판매자");

    public RecyclerView mypage_order_recyclerview;
    public static List<Order_Info> mypage_seller_order_item = new ArrayList<>();
    private final Tab2_MyPage_Order_Adapter myPageOderAdapter = new Tab2_MyPage_Order_Adapter(getContext(), mypage_seller_order_item, R.layout.mypage_orderlist_item);

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final ViewGroup rootview = (ViewGroup) inflater.inflate(R.layout.fragment_tab2_mypage_seller, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = firebaseAuth.getCurrentUser();

        //리사이클러뷰 레이아웃 매니저 설정
        mypage_order_recyclerview = rootview.findViewById(R.id.seller_myPage_order_recyclerView);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        mypage_order_recyclerview.setHasFixedSize(true);
        mypage_order_recyclerview.setLayoutManager(layoutManager);
        mypage_order_recyclerview.setAdapter(myPageOderAdapter);

        sellerName = (TextView)rootview.findViewById(R.id.mypage_sellerId);
        sellerStorename = (TextView)rootview.findViewById(R.id.mypage_sellerStorename);
        sellerEmail = (TextView)rootview.findViewById(R.id.mypage_sellerEmail);
        sellerPhonenumber = (TextView)rootview.findViewById(R.id.mypage_sellerPhonenumber);
        sellerAddress = (TextView)rootview.findViewById(R.id.mypage_sellerAddress);
        sellerBank = (TextView)rootview.findViewById(R.id.mypage_sellerBank);
        sellerAccount = (TextView)rootview.findViewById(R.id.mypage_sellerAccount);
        sellerDelivery = (TextView)rootview.findViewById(R.id.mypage_sellerDelivery);

        sellerSignout = rootview.findViewById(R.id.mypage_seller_signout);
        sellerModify = rootview.findViewById(R.id.mypage_seller_modify);


        //Current 유저 찾아서 DB에 저장된 정보 화면에 띄우기
        if (FirebaseDatabase.getInstance().getReference() != null) {
            if (mFirebaseUser != null) {
                myRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        mypage_seller_order_item.clear();
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            if (ds.child("email").getValue().toString().equals(mFirebaseUser.getEmail())) {
                                String target = ds.child("username").getValue().toString();
                                sellerName.setText(target);
                                target = ds.child("storename").getValue().toString();
                                sellerStorename.setText(target);
                                target = ds.child("phonenumber").getValue().toString();
                                sellerPhonenumber.setText(target);
                                target = ds.child("address").getValue().toString();
                                sellerAddress.setText(target);
                                target = ds.child("bank_name").getValue().toString();
                                sellerBank.setText(target);
                                target = ds.child("account_number").getValue().toString();
                                sellerAccount.setText(target);
                                target = ds.child("delivery_fee").getValue().toString();
                                sellerDelivery.setText(target);
                                if(!TextUtils.isEmpty(ds.child("orderinfo").getValue().toString())){
                                    for(DataSnapshot ds2 : ds.child("orderinfo").getChildren()){
                                        Order_Info order1 = ds2.getValue(Order_Info.class);
                                        mypage_seller_order_item.add(order1);
                                    }
                                    Collections.reverse(mypage_seller_order_item);
                                    myPageOderAdapter.notifyDataSetChanged();
                                }
                                break;
                            }
                        }
                        return;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                sellerEmail.setText(mFirebaseUser.getEmail());
            }
        }

        // 회원정보 수정
        //회원정보 수정은 ModifySeller액티비티에서 진행
        sellerModify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(),ModifySellerActivity.class));
            }
        });

        // 로그아웃
        sellerSignout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth = FirebaseAuth.getInstance();
                mFirebaseUser = firebaseAuth.getCurrentUser();
                //로그인 되어있는 경우 로그아웃

                StringTokenizer st = new StringTokenizer(mFirebaseUser.getEmail(), "@");

                FirebaseMessaging.getInstance().unsubscribeFromTopic(st.nextToken()+st.nextToken())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (mFirebaseUser != null)
                                    FirebaseAuth.getInstance().signOut();

                                PreferenceUtil.getInstance(getContext()).putBooleanExtra("isSeller",false);
                                Intent intent = new Intent(getActivity(), SignInActivity.class);
                                startActivity(intent);
                            }
                        });
            }
        });

        myPageOderAdapter.setOnItemClickListener(new Tab2_MyPage_Order_Adapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Intent intent = new Intent(getContext(), OrderDetailActivity.class);
                intent.putExtra("position", position);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.enter_from_right, R.anim.fade_out);
            }
        });

        return rootview;
    }
}