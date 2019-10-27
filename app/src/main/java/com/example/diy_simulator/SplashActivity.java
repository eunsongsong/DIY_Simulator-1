package com.example.diy_simulator;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseUser mFirebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = firebaseAuth.getCurrentUser();
        //로그인 되어있는 경우 로그아웃
        if (mFirebaseUser != null)
            FirebaseAuth.getInstance().signOut();

        //홈액티비티로 전환
        Intent mainIntent = new Intent(getApplicationContext(), Final.class);
        startActivity(mainIntent);
        finish();
    }
}


