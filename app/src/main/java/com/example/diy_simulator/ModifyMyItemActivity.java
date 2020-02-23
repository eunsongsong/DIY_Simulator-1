package com.example.diy_simulator;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static  com.example.diy_simulator.Tab4_Simulation.modi_item2;

public class ModifyMyItemActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = database.getReference("구매자");
    ProgressDialog pd;

    private EditText mname, mwidth, mheight;
    private String name, width, height;
    private Button modi_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_myitem);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        modi_btn = findViewById(R.id.my_modi_item_complete_btn);
        mname = findViewById(R.id.modi_material_name);
        mwidth = findViewById(R.id.modi_size_width);
        mheight = findViewById(R.id.modi_size_height);

        //툴바 뒤로가기 버튼 설정
        Toolbar tb = findViewById(R.id.modi_image_upload_toolbar) ;
        setSupportActionBar(tb) ;
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);

        modi_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateMaterialDB();
            }
        });

    }
    private void UpdateMaterialDB() {
        showProgress();

        name = mname.getText().toString();
        width = mwidth.getText().toString();
        height = mheight.getText().toString();

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){

                    if(String.valueOf(ds.child("email").getValue()).equals(firebaseUser.getEmail())){
                        if(!TextUtils.isEmpty(name))
                            myRef.child(ds.getKey()).child("my_image_url").child(modi_item2.getUnique_number()).child("material_name").setValue(name);
                        if(!TextUtils.isEmpty(width))
                            myRef.child(ds.getKey()).child("my_image_url").child(modi_item2.getUnique_number()).child("size_width").setValue(width);
                        if(!TextUtils.isEmpty(height))
                            myRef.child(ds.getKey()).child("my_image_url").child(modi_item2.getUnique_number()).child("size_height").setValue(height);
                        break;
                    }
                }
                hideProgress();
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.exit_to_right);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    // 프로그레스 다이얼로그 보이기
    public void showProgress() {
        if( pd == null ) { // 객체를 1회만 생성한다
            pd = new ProgressDialog(ModifyMyItemActivity.this, R.style.NewDialog); // 생성한다.
            pd.setCancelable(false); // 백키로 닫는 기능을 제거한다.
        }
        pd.show(); // 화면에 띠워라//
    }
    public void hideProgress() {
        if (pd != null && pd.isShowing()) {
            pd.dismiss();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{ //toolbar의 back키 눌렀을 때 동작
                //super.onBackPressed();
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.exit_to_right);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.exit_to_right);
    }
}
