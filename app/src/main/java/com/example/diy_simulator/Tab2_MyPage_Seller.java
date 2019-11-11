package com.example.diy_simulator;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Tab2_MyPage_Seller extends Fragment {

    FirebaseAuth firebaseAuth;
    FirebaseUser mFirebaseUser;
    TextView sellerName;
    TextView sellerStorename;
    TextView sellerEmail;
    TextView sellerAddress;
    TextView sellerPhonenumber;

    Button sellerSignout;
    Button sellerModify;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("판매자");

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final ViewGroup rootview = (ViewGroup) inflater.inflate(R.layout.fragment_tab2_mypage_seller, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = firebaseAuth.getCurrentUser();

        sellerName = (TextView)rootview.findViewById(R.id.mypage_sellerId);
        sellerStorename = (TextView)rootview.findViewById(R.id.mypage_sellerStorename);
        sellerEmail = (TextView)rootview.findViewById(R.id.mypage_sellerEmail);
        sellerPhonenumber = (TextView)rootview.findViewById(R.id.mypage_sellerPhonenumber);
        sellerAddress = (TextView)rootview.findViewById(R.id.mypage_sellerAddress);

        sellerSignout = rootview.findViewById(R.id.mypage_seller_signout);
        sellerModify = rootview.findViewById(R.id.mypage_seller_modify);

        //Current 유저 찾아서 DB에 저장된 정보 화면에 띄우기
        if (FirebaseDatabase.getInstance().getReference() != null) {
            if (mFirebaseUser != null) {
                myRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
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
        //회원정보 수정은 ModifyInfo액티비티에서 진행
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
                if (mFirebaseUser != null)
                    FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getActivity(), SignInActivity.class);
                startActivity(intent);
            }
        });


        return rootview;
    }
}