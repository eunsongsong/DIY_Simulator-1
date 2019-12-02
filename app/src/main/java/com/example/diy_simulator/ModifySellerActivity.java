package com.example.diy_simulator;

import android.app.ProgressDialog;
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
    EditText modifySellerBank;
    EditText modifySellerAccount;
    EditText modifySellerDelivery;

    String reSellerName;
    String reSellerPhonenumber;
    String reSellerAddress;
    String reSellerStorename;
    String reSellerBank;
    String reSellerAccount;
    String reSellerDelivery;

    FirebaseAuth firebaseAuth;
    FirebaseUser mFirebaseUser;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef2 = database.getReference("판매자");
    private DatabaseReference myRef3 = database.getReference("부자재");

    String before_storename;
    ProgressDialog pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_seller);

        modifySellerBtn = (Button)findViewById(R.id.modify_seller_button);
        modifySellerName = (EditText)findViewById(R.id.modify_seller_name);
        modifySellerStorename = (EditText)findViewById(R.id.modify_storename);
        modifySellerPhonenumber = (EditText)findViewById(R.id.modify_seller_phonenumber);
        modifySellerAddress = (EditText)findViewById(R.id.modify_seller_address);
        modifySellerBank = (EditText)findViewById(R.id.modify_seller_bank);
        modifySellerAccount = (EditText)findViewById(R.id.modify_seller_account);
        modifySellerDelivery = (EditText)findViewById(R.id.modify_seller_delivery);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.useAppLanguage();

        //결과 저장 함수 실행, 정보수정화면은 null로
        modifySellerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ModifySellerInfo();

                modifySellerName.setText(null);
                modifySellerStorename.setText(null);
                modifySellerPhonenumber.setText(null);
                modifySellerAddress.setText(null);
                modifySellerBank.setText(null);
                modifySellerAccount.setText(null);
                modifySellerDelivery.setText(null);
            }
        });
    }

    //수정된 사항 판매자 DB에 저장
    public void ModifySellerInfo(){
        showProgress();
        reSellerName = modifySellerName.getText().toString();
        reSellerStorename = modifySellerStorename.getText().toString();
        reSellerPhonenumber = modifySellerPhonenumber.getText().toString();
        reSellerAddress = modifySellerAddress.getText().toString();
        reSellerBank = modifySellerBank.getText().toString();
        reSellerAccount = modifySellerAccount.getText().toString();
        reSellerDelivery = modifySellerDelivery.getText().toString();

        firebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = firebaseAuth.getCurrentUser();

        //변경사항이 없을 때 - 메세지만 띄우기
        if ((TextUtils.isEmpty(reSellerName)) && (TextUtils.isEmpty(reSellerStorename))
                && (TextUtils.isEmpty(reSellerPhonenumber)) && (TextUtils.isEmpty(reSellerAddress)
                && (TextUtils.isEmpty(reSellerBank) && (TextUtils.isEmpty(reSellerAccount))
                && (TextUtils.isEmpty(reSellerDelivery))))) {
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
                                before_storename = ds.child("storename").getValue().toString();
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
                                if (!(TextUtils.isEmpty(reSellerBank))) {
                                    StringTokenizer st = new StringTokenizer(mFirebaseUser.getEmail(), "@");
                                    StringTokenizer st_two = new StringTokenizer(ds.getKey(), ":");
                                    myRef2.child(st_two.nextToken() + ":" + st.nextToken()).child("bank_name").setValue(reSellerBank);
                                }
                                if (!(TextUtils.isEmpty(reSellerAccount))) {
                                    StringTokenizer st = new StringTokenizer(mFirebaseUser.getEmail(), "@");
                                    StringTokenizer st_two = new StringTokenizer(ds.getKey(), ":");
                                    myRef2.child(st_two.nextToken() + ":" + st.nextToken()).child("account_number").setValue(reSellerAccount);
                                }
                                if (!(TextUtils.isEmpty(reSellerDelivery))) {
                                    StringTokenizer st = new StringTokenizer(mFirebaseUser.getEmail(), "@");
                                    StringTokenizer st_two = new StringTokenizer(ds.getKey(), ":");
                                    myRef2.child(st_two.nextToken() + ":" + st.nextToken()).child("delivery_fee").setValue(reSellerDelivery);
                                }

                                myRef3.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        for(DataSnapshot ds : dataSnapshot.getChildren()){
                                            if(ds.child("storename").getValue().toString().equals(before_storename)){
                                                myRef3.child(ds.getKey()).child("storename").setValue(reSellerStorename);
                                            }
                                        }
                                        hideProgress();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });


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
    // 프로그레스 다이얼로그 보이기
    public void showProgress() {
        if( pd == null ) { // 객체를 1회만 생성한다
            pd = new ProgressDialog(ModifySellerActivity.this, R.style.NewDialog); // 생성한다.
            pd.setCancelable(false); // 백키로 닫는 기능을 제거한다.
        }
        pd.show(); // 화면에 띠워라//
    }
    public void hideProgress() {
        if (pd != null && pd.isShowing()) {
            pd.dismiss();
        }
    }

}
