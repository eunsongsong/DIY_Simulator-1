package com.example.diy_simulator;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
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

import java.util.regex.Pattern;

public class SignInActivity extends AppCompatActivity {

    public static int TIME_OUT = 1001;

    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^[a-zA-Z0-9!@.#$%^&*?_~]{4,16}$");
    private String email = "";
    private String password = "";
    private String[] sellers = {}; //모든 판매자 이메일
    boolean isSeller = false;
    ProgressDialog dialog;
    private EditText email_login;
    private EditText pwd_login;
    private Button seller_join, customer_join;

    FirebaseAuth firebaseAuth;
    FirebaseUser mFirebaseUser;

    // Write a message to the database
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("구매자");
    DatabaseReference myRef2 = database.getReference("판매자");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        //툴바 설정
        Toolbar tb = findViewById(R.id.sign_in_toolbar) ;
        setSupportActionBar(tb) ;
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);

        email_login = (EditText) findViewById(R.id.signin_email);
        pwd_login = (EditText) findViewById(R.id.signin_pwd);
        seller_join = (Button) findViewById(R.id.seller_signup);
        customer_join = (Button) findViewById(R.id.customer_signup);

        firebaseAuth = FirebaseAuth.getInstance();

        //회원 가입 누르면 판매자인지 고객인지 스트링 보내면서 인텐트 시작
        seller_join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
                intent.putExtra("who", "seller");
                startActivity(intent);
            }
        });
        customer_join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
                intent.putExtra("who", "customer");
                startActivity(intent);
            }
        });


        //판매자 이메일 전부 가져와서 스트링 배열 sellers에 저장
        myRef2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                sellers = new String[(int)dataSnapshot.getChildrenCount()];
                int i = 0;
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String tmp = ds.child("email").getValue().toString();
                    sellers[i] = tmp;
                    i++;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == TIME_OUT) { // 타임아웃이 발생하면
                dialog.dismiss(); // ProgressDialog를 종료
            }
        }
    };

    /**
     * sign in 버튼 클릭시 실행
     */
    public void sign_In(View v) {
        //로그인 되어있는 경우 로그아웃
        if (mFirebaseUser != null)
            FirebaseAuth.getInstance().signOut();

        email = email_login.getText().toString();
        password = pwd_login.getText().toString();

        //이메일 입력 칸이 빈칸인 경우 알림
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "이메일을 입력해 주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        //비밀번호 입력 칸이 빈칸인 경우 알림
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "비밀번호를 입력해 주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        //로그인 성공 여부를 firebase를 통해서 확인
        //이메일 인증을 한경우에만 로그인
        if (isValidEmail() && isValidPasswd()) {

            //로그인하는 이메일이 판매자 아이디인지 확인
            for(int i=0; i<sellers.length; i++){
                if(email.equals(sellers[i])) {
                    isSeller = true;
                    break;
                }
                else isSeller = false;
            }

            firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                mFirebaseUser = firebaseAuth.getCurrentUser();
                                if (mFirebaseUser != null) {
                                    if (!(mFirebaseUser.isEmailVerified())) { //인증안되면
                                        Toast.makeText(SignInActivity.this, "이메일 인증을 해주세요", Toast.LENGTH_LONG).show();
                                        return;
                                    } else { //인증되면

                                        //다이얼로그 로딩화면
                                        dialog = new ProgressDialog(SignInActivity.this, R.style.NewDialog);
                                        dialog.show();

                                        email_login.setText(null);
                                        pwd_login.setText(null);

                                        Log.d("판매자인지?", isSeller+"");
                                        Intent intent = new Intent(getApplicationContext(), MainTabActivity.class);
                                        intent.putExtra("whoIs", isSeller);
                                        //로그인 이전 액티비티 스택을 비운다
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);
                                        dialog.dismiss();
                                        finish();
                                    }
                                }
                            } else {
                                // 로그인 실패
                                Toast.makeText(SignInActivity.this, R.string.failed_login, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

        } else {
            Toast.makeText(SignInActivity.this, R.string.failed_login, Toast.LENGTH_LONG).show();
            return;
        }
    }

    // 이메일 유효성 검사
    private boolean isValidEmail() {
        if (email.isEmpty()) {
            // 이메일 공백
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            // 이메일 형식 불일치
            return false;
        } else {
            return true;
        }
    }

    // 비밀번호 유효성 검사
    private boolean isValidPasswd() {
        if (password.isEmpty()) {
            // 비밀번호 공백
            return false;
        } else if (!PASSWORD_PATTERN.matcher(password).matches()) {
            // 비밀번호 형식 불일치
            return false;
        } else {
            return true;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{ //toolbar의 back키 눌렀을 때 동작
                Intent intent = new Intent(getApplicationContext(), MainTabActivity.class);
                startActivity(intent);
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), MainTabActivity.class);
        startActivity(intent);
        finish();
    }
}

