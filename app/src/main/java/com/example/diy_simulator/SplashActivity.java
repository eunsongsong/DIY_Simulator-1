package com.example.diy_simulator;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SplashActivity extends AppCompatActivity {
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = database.getReference("판매자");
    String[] names = {};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //파이어베이스에서 판매자의 storname을 모두 가져와서 names[] 배열에 넣기
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int count = (int) dataSnapshot.getChildrenCount();
                names = new String[count];
                int i = 0;
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    names[i] = ds.child("storename").getValue().toString(); //상호명
                    Log.d("하는 중?", names[i]+"");
                    i++;
                }
                //홈액티비티로 전환
                Intent mainIntent = new Intent(getApplicationContext(), MainTabActivity.class);
                mainIntent.putExtra("names", names);
                startActivity(mainIntent);
                finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}


