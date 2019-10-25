package com.example.diy_simulator;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Tab2_MyPage_Customer extends Fragment {

    FirebaseAuth firebaseAuth;
    FirebaseUser mFirebaseUser;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final ViewGroup rootview = (ViewGroup) inflater.inflate(R.layout.fragment_tab2_mypage_customer, container, false);

        Button signout = rootview.findViewById(R.id.sign_out_btn);

        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth = FirebaseAuth.getInstance();
                mFirebaseUser = firebaseAuth.getCurrentUser();
                //로그인 되어있는 경우 로그아웃
                if (mFirebaseUser != null)
                    FirebaseAuth.getInstance().signOut();
            }
        });
        return rootview;
    }
}
