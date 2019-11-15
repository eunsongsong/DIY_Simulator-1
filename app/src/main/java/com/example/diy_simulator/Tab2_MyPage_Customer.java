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


public class Tab2_MyPage_Customer extends Fragment {

    FirebaseAuth firebaseAuth;
    FirebaseUser mFirebaseUser;
    TextView customerName;
    TextView customerEmail;
    TextView customerAddress;
    TextView customerPhonenumber;

    Button customerSignout;
    Button customerModify;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("구매자");

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final ViewGroup rootview = (ViewGroup) inflater.inflate(R.layout.fragment_tab2_mypage_customer, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = firebaseAuth.getCurrentUser();

        customerName = (TextView)rootview.findViewById(R.id.mypage_customerId) ;
        customerEmail = (TextView)rootview.findViewById(R.id.mypage_customerEmail);
        customerAddress = (TextView)rootview.findViewById(R.id.mypage_customerAddress);
        customerPhonenumber = (TextView)rootview.findViewById(R.id.mypage_customerPhonenumber);

        customerSignout = rootview.findViewById(R.id.mypage_customer_signout);
        customerModify = rootview.findViewById(R.id.mypage_customer_modify);

        //Current 유저 찾아서 DB에 저장된 정보 화면에 띄우기
        if (FirebaseDatabase.getInstance().getReference() != null) {
            if (mFirebaseUser != null) {
                myRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            if (ds.child("email").getValue().toString().equals(mFirebaseUser.getEmail())) {
                                String target = ds.child("username").getValue().toString();
                                customerName.setText(target);
                                target = ds.child("phonenumber").getValue().toString();
                                customerPhonenumber.setText(target);
                                target = ds.child("address").getValue().toString();
                                customerAddress.setText(target);
                            }
                        }
                        return;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                customerEmail.setText(mFirebaseUser.getEmail());
            }
        }

        // 회원정보 수정
        //회원정보 수정은 ModifyInfo액티비티에서 진행
        customerModify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(),ModifyInfoActivity.class));
            }
        });

        // 로그아웃
        customerSignout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth = FirebaseAuth.getInstance();
                mFirebaseUser = firebaseAuth.getCurrentUser();
                //로그인 되어있는 경우 로그아웃
                if (mFirebaseUser != null)
                    FirebaseAuth.getInstance().signOut();
                PreferenceUtil.getInstance(getContext()).putBooleanExtra("isSeller",false);
                Intent intent = new Intent(getActivity(), SignInActivity.class);
                startActivity(intent);
            }
        });
        return rootview;
    }
}
