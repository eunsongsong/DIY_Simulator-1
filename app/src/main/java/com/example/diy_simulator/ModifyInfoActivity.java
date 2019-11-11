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

public class ModifyInfoActivity extends AppCompatActivity {

    Button modifyBtn;

    EditText modifyName;
    EditText modifyPhonenumber;
    EditText modifyAddress;

    String reName;
    String rePhonenumber;
    String reAddress;


    FirebaseAuth firebaseAuth;
    FirebaseUser mFirebaseUser;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = database.getReference("구매자");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_info);

        modifyBtn = (Button)findViewById(R.id.modify_customer_button);
        modifyName = (EditText)findViewById(R.id.modify_customer_name);
        modifyPhonenumber = (EditText)findViewById(R.id.modify_customer_phonenumber);
        modifyAddress = (EditText)findViewById(R.id.modify_customer_address);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.useAppLanguage();

        //결과 저장 함수 실행, 정보수정화면은 null로
        modifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ModifyUserInfo();

                modifyName.setText(null);
                modifyPhonenumber.setText(null);
                modifyAddress.setText(null);
            }
        });
    }
    //수정된 사항 유저 DB에 저장
    public void ModifyUserInfo(){
        reName = modifyName.getText().toString();
        rePhonenumber = modifyPhonenumber.getText().toString();
        reAddress = modifyAddress.getText().toString();


        firebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = firebaseAuth.getCurrentUser();

        //변경사항이 없을 때 - 메세지만 띄우기
        if ((TextUtils.isEmpty(reName)) && (TextUtils.isEmpty(rePhonenumber)) && (TextUtils.isEmpty((reAddress)))) {
            Toast.makeText(this, "변경사항이 없습니다.", Toast.LENGTH_SHORT).show();
        }
        //변경사항이 있을 때 - DB 업데이트
        else {
            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        String target = ds.child("email").getValue().toString();
                        if (mFirebaseUser != null) {
                            if (target.equals(mFirebaseUser.getEmail())) {

                                //변경사항이 있을 때 - Null 아닌 부분만 DB에 저장
                                if (!(TextUtils.isEmpty(reName))) {
                                    StringTokenizer st = new StringTokenizer(mFirebaseUser.getEmail(), "@");
                                    StringTokenizer st_two = new StringTokenizer(ds.getKey(), ":");
                                    myRef.child(st_two.nextToken() + ":" + st.nextToken()).child("username").setValue(reName);
                                }
                                if (!(TextUtils.isEmpty(rePhonenumber))) {
                                    StringTokenizer st = new StringTokenizer(mFirebaseUser.getEmail(), "@");
                                    StringTokenizer st_two = new StringTokenizer(ds.getKey(), ":");
                                    myRef.child(st_two.nextToken() + ":" + st.nextToken()).child("phonenumber").setValue(rePhonenumber);
                                }
                                if (!(TextUtils.isEmpty(reAddress))) {
                                    StringTokenizer st = new StringTokenizer(mFirebaseUser.getEmail(), "@");
                                    StringTokenizer st_two = new StringTokenizer(ds.getKey(), ":");
                                    myRef.child(st_two.nextToken() + ":" + st.nextToken()).child("address").setValue(reAddress);
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
