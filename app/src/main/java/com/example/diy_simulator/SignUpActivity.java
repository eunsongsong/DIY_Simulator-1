package com.example.diy_simulator;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.StringTokenizer;
import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity {

    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^[a-zA-Z0-9!@.#$%^&*?_~]{4,16}$");
    ProgressDialog dialog;

    private EditText email_join, pwd_join, check_pwd_join, name_join, address_join, phone_number_join, store_name_join, delivery_fee_join;
    private EditText account_join, bank_join;

    private TextView check_show; //비밀번호 일치 확인 텍스트
    private TextView password_guide;
    private Button sign_up_btn;

    FirebaseAuth firebaseAuth;
    FirebaseUser mFirebaseUser;

    private String email = "";
    private String password = "";
    private String name = "";
    private String address = "";
    private String phone_number = "";
    private String store_name = "";
    private String delivery_fee = "";
    private String userToken = "";
    private String account_number = "";
    private String back_name = "";

    long count = 0;
    String whois = ""; //판매자 가입인지 고객 가입인지 확인

    // Write a message to the database
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = database.getReference("구매자");
    private DatabaseReference myRef2 = database.getReference("판매자");

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //툴바 설정
        Toolbar tb = findViewById(R.id.sign_up_toolbar) ;
        setSupportActionBar(tb) ;
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);

        email_join = (EditText) findViewById(R.id.emailInput);
        pwd_join = (EditText) findViewById(R.id.passwordInput);
        check_pwd_join = (EditText) findViewById(R.id.passwordCheck);
        name_join = (EditText) findViewById(R.id.nameInput);
        phone_number_join = (EditText) findViewById(R.id.phonenumberInput);
        address_join = (EditText) findViewById(R.id.addressInput);
        store_name_join = (EditText) findViewById(R.id.storenameInput);
        delivery_fee_join = (EditText) findViewById(R.id.deliveryfeeinput);
        account_join = (EditText) findViewById(R.id.accountnumberInput);
        bank_join = (EditText) findViewById(R.id.banknameInput);

        check_show = (TextView) findViewById(R.id.checkText);
        password_guide = findViewById(R.id.sign_up_password_guide);

        sign_up_btn = (Button) findViewById(R.id.signup_button);

        //sign in 액티비티에서 스트링 받아옴
        final Intent intent = getIntent();
        String who = intent.getStringExtra("who");
        whois = who;
        if(who.equals("customer")) {
            store_name_join.setVisibility(View.GONE);
            delivery_fee_join.setVisibility(View.GONE);
        }

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.useAppLanguage();

        // [START retrieve_current_token]
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w("TAG", "getInstanceId failed", task.getException());
                            return;
                        }
                        // Get new Instance ID token
                        userToken = task.getResult().getToken();
                        //서비스 시작 (데모용 1분 푸시)
                    }
                });
        // [END retrieve_current_token]

        // 비밀번호 형식 안내문구
        pwd_join.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().isEmpty()){
                    password_guide.setVisibility(View.GONE);
                }
                else{
                    if(PASSWORD_PATTERN.matcher(s).matches()){
                        password_guide.setVisibility(View.GONE);
                    }
                    else  //비밀번호 형식 맞지 않을 때 안내문구 보여주기
                        password_guide.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        //비밀번호 일치 여부 확인
        //일치하면 회원가입 버튼 활성화
        check_pwd_join.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String comp1 = pwd_join.getText().toString();
                String comp2 = check_pwd_join.getText().toString();

                if (comp1.equals(comp2)) {
                    check_show.setText("비밀번호가 일치합니다");
                    sign_up_btn.setEnabled(true);
                } else {
                    check_show.setText("비밀번호가 일치하지 않습니다");
                }
            }
        });

        //회원가입 하면 유저를 등록
        sign_up_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean isValid = registerUser();
                if(isValid){
                    dialog = ProgressDialog.show(SignUpActivity.this, "회원가입 진행 중입니다!"
                            , "이메일로 인증 메일이 발송됩니다.", true);

                    email_join.setText(null);
                    pwd_join.setText(null);
                    check_pwd_join.setText(null);
                    name_join.setText(null);
                    phone_number_join.setText(null);
                    address_join.setText(null);
                    store_name_join.setText(null);
                    delivery_fee_join.setText(null);
                    account_join.setText(null);
                    bank_join.setText(null);
                }
            }
        });
    }

    // 이메일 유효성 검사
    private boolean isValidEmail() {
        if (email.isEmpty()) {
            // 이메일 공백
            return false;
        } else return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    // 비밀번호 유효성 검사
    private boolean isValidPasswd() {
        if (password.isEmpty()) {
            // 비밀번호 공백
            return false;
        } else return PASSWORD_PATTERN.matcher(password).matches();
    }

    // 회원가입
    //firebase에 이메일 인증 방식 사용
    //등록된 이메일이 아니면 firebase authentication에 등록되고 인증 메일 발송
    private void createUser(final String email, String password) {

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // 회원가입 성공
                            mFirebaseUser = firebaseAuth.getCurrentUser();
                            if (mFirebaseUser != null)
                                mFirebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            dialog.dismiss();
                                            Toast.makeText(SignUpActivity.this,
                                                    "인증 메일이 " + mFirebaseUser.getEmail()+ "로 발송되었습니다. 인증 후 이용해주세요.",
                                                    Toast.LENGTH_LONG).show();

                                            //디비에 유저 등록 - 고객
                                            if(whois.equals("customer")) {
                                                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                                            count++;
                                                        }
                                                        //구매자 - 유저정보를 디비에 넣는다.
                                                        Customer customer= new Customer(email, name, phone_number, address, account_number, back_name);

                                                        StringTokenizer st = new StringTokenizer(email, "@");
                                                        if (count >= 9) {
                                                            myRef.child("user0" + (count + 1) + ":" + st.nextToken()).setValue(customer);
                                                        } else
                                                            myRef.child("user00" + (count + 1) + ":" + st.nextToken()).setValue(customer);
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                    }
                                                });
                                            }
                                            //디비에 유저 등록 - 판매자
                                            else {
                                                myRef2.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                                            count++;
                                                        }
                                                        //판매자 - 유저정보를 디비에 넣는다.
                                                        Seller seller = new Seller(email, name, phone_number, address, store_name, delivery_fee, account_number, back_name);

                                                        StringTokenizer st = new StringTokenizer(email, "@");
                                                        if (count >= 9) {
                                                            myRef2.child("user0" + (count + 1) + ":" + st.nextToken()).setValue(seller);
                                                        } else
                                                            myRef2.child("user00" + (count + 1) + ":" + st.nextToken()).setValue(seller);
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                    }
                                                });
                                            }
                                            firebaseAuth.signOut();
                                            startActivity(new Intent(SignUpActivity.this, SignInActivity.class));

                                            finish();
                                        } else {                                             //메일 보내기 실패
                                            Toast.makeText(SignUpActivity.this,
                                                    "Failed to send verification email.",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                        } else {
                            // 회원가입 실패
                            Toast.makeText(SignUpActivity.this, R.string.failed_signup, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    //회원 등록 함수
    public Boolean registerUser() {
        email = email_join.getText().toString();
        password = pwd_join.getText().toString();
        name = name_join.getText().toString();
        phone_number = phone_number_join.getText().toString();
        address = address_join.getText().toString();
        store_name = store_name_join.getText().toString();
        delivery_fee = delivery_fee_join.getText().toString();
        account_number = account_join.getText().toString();
        back_name = bank_join.getText().toString();

        //이메일 입력 칸이 빈칸인 경우 알림
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "이메일을 입력해 주세요.", Toast.LENGTH_SHORT).show();
        }
        //비밀번호 입력 칸이 빈칸인 경우 알림
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "비밀번호를 입력해 주세요.", Toast.LENGTH_SHORT).show();
        }

        else{
            if(!isValidEmail()){
                Toast.makeText(this, "이메일 형식이 올바르지 않습니다.", Toast.LENGTH_SHORT).show();
            }
            else if(!isValidPasswd()){
                Toast.makeText(this, "비밀번호 형식이 올바르지 않습니다.", Toast.LENGTH_SHORT).show();
            }
            //유저 등록 함수 실행
            else if (isValidEmail() && isValidPasswd()) {
                createUser(email, password);
                return true;
            }
        }
        return false;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{ //toolbar의 back키 눌렀을 때 동작
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

}

