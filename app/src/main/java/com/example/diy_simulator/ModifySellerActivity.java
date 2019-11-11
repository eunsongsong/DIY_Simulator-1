package com.example.diy_simulator;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.StringTokenizer;

public class ModifySellerActivity extends AppCompatActivity {

    Button modifySellerBtn;

    EditText modifySellerName;
    EditText modifySellerPhonenumber;
    EditText modifySellerAddress;
    EditText modifySellerStorename;

    String reSellerName;
    String reSellerPhonenumber;
    String reSellerAddress;
    String reSellerStorename;


    FirebaseAuth firebaseAuth;
    FirebaseUser mFirebaseUser;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef2 = database.getReference("판매자");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_seller);

        modifySellerBtn = (Button)findViewById(R.id.modify_seller_button);
        modifySellerName = (EditText)findViewById(R.id.modify_seller_name);
        modifySellerStorename = (EditText)findViewById(R.id.modify_storename);
        modifySellerPhonenumber = (EditText)findViewById(R.id.modify_seller_phonenumber);
        modifySellerAddress = (EditText)findViewById(R.id.modify_seller_address);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.useAppLanguage();

        //결과 저장 함수 실행, 정보수정화면은 null로
        modifySellerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ModifyUserInfo();

                modifySellerName.setText(null);
                modifySellerStorename.setText(null);
                modifySellerPhonenumber.setText(null);
                modifySellerAddress.setText(null);
            }
        });
    }
    //수정된 사항 유저 DB에 저장
    public void ModifyUserInfo(){
        reSellerName = modifySellerName.getText().toString();
        reSellerStorename = modifySellerStorename.getText().toString();
        reSellerPhonenumber = modifySellerPhonenumber.getText().toString();
        reSellerAddress = modifySellerAddress.getText().toString();

        firebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = firebaseAuth.getCurrentUser();

        //변경사항이 없을 때 - 메세지만 띄우기
        if ((TextUtils.isEmpty(reSellerName)) && (TextUtils.isEmpty(reSellerStorename)) && (TextUtils.isEmpty(reSellerPhonenumber)) && (TextUtils.isEmpty(reSellerAddress))) {
            Toast.makeText(this, "변경사항이 없습니다.", Toast.LENGTH_SHORT).show();
        }
        //변경사항이 있을 때 - DB 업데이트
        else {
            myRef2.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        String target = ds.child("email").getValue().toString();
                        if (mFirebaseUser != null) {
                            if (target.equals(mFirebaseUser.getEmail())) {

                                //변경사항이 있을 때 - Null 아닌 부분만 DB에 저장
                                if (!(TextUtils.isEmpty(reSellerName))) {
                                    StringTokenizer st = new StringTokenizer(mFirebaseUser.getEmail(), "@");
                                    StringTokenizer st_two = new StringTokenizer(ds.getKey(), ":");
                                    myRef2.child(st_two.nextToken() + ":" + st.nextToken()).child("username").setValue(reSellerName);
                                }
                                if (!(TextUtils.isEmpty(reSellerStorename))) {
                                    StringTokenizer st = new StringTokenizer(mFirebaseUser.getEmail(), "@");
                                    StringTokenizer st_two = new StringTokenizer(ds.getKey(), ":");
                                    myRef2.child(st_two.nextToken() + ":" + st.nextToken()).child("storename").setValue(reSellerStorename);
                                }
                                if (!(TextUtils.isEmpty(reSellerPhonenumber))) {
                                    StringTokenizer st = new StringTokenizer(mFirebaseUser.getEmail(), "@");
                                    StringTokenizer st_two = new StringTokenizer(ds.getKey(), ":");
                                    myRef2.child(st_two.nextToken() + ":" + st.nextToken()).child("phonenumber").setValue(reSellerPhonenumber);
                                }
                                if (!(TextUtils.isEmpty(reSellerAddress))) {
                                    StringTokenizer st = new StringTokenizer(mFirebaseUser.getEmail(), "@");
                                    StringTokenizer st_two = new StringTokenizer(ds.getKey(), ":");
                                    myRef2.child(st_two.nextToken() + ":" + st.nextToken()).child("address").setValue(reSellerAddress);
                                }


                                //변경 사항이 저장되었음을 알림
                                Toast.makeText(getApplicationContext(), "변경사항이 저장되었습니다.", Toast.LENGTH_SHORT).show();

                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

    }
}
