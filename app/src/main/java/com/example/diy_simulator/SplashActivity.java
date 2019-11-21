package com.example.diy_simulator;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            //스플래시 화면 1.3초 보여주기
            Thread.sleep(1300);

            //홈액티비티로 전환
            Intent mainIntent = new Intent(getApplicationContext(), OrderActivity.class);
            startActivity(mainIntent);
            finish();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}


