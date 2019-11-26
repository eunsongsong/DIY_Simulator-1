package com.example.diy_simulator;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import static com.example.diy_simulator.Tab3_MyStore.modi_item;

public class ModifySellerProductActivity extends AppCompatActivity {

    private Button modi_btn;
    private EditText mname, mprice, mwidth, mheight, mdepth, mstock, mkeyword;
    private String name, price, width, height, depth, stock, keyword, category;
    private CheckBox keycheck, casecheck, earcheck, bracecheck, etccheck;
    private Spinner keyspinner, casespinner, earspinner, bracespinner, etcspinner;
    private String keyspin, casespin, earspin, bracespin, etcspin;

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = database.getReference("부자재");
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_seller_product);

        modi_btn = (Button) findViewById(R.id.modi_item_complete_btn); //업로드 버튼
        //부자재 정보 - 판매자 입력
        mname = findViewById(R.id.modi_item_material_name);
        mprice = findViewById(R.id.modi_item_material_price);
        mwidth = findViewById(R.id.modi_item_size_width);
        mheight = findViewById(R.id.modi_item_size_height);
        mdepth = findViewById(R.id.modi_item_size_depth);
        mstock = findViewById(R.id.modi_item_material_stock);
        mkeyword = findViewById(R.id.modi_item_material_keyword);
        //세부 카테고리 나타내는 스피너
        keyspinner = findViewById(R.id.modi_item_spinner_keyring);
        casespinner = findViewById(R.id.modi_item_spinner_case);
        earspinner = findViewById(R.id.modi_item_spinner_earring);
        bracespinner = findViewById(R.id.modi_item_spinner_bracelet);
        etcspinner = findViewById(R.id.modi_item_spinner_etc);
        //큰 카테고리 선택 체크박스
        keycheck = findViewById(R.id.modi_item_category_check_keyring);
        casecheck = findViewById(R.id.modi_item_category_check_case);
        earcheck = findViewById(R.id.modi_item_category_check_earring);
        bracecheck = findViewById(R.id.modi_item_category_check_bracelet);
        etccheck = findViewById(R.id.modi_item_category_check_etc);

        //스피너 설정
        ArrayAdapter Adapter1 = ArrayAdapter.createFromResource(this, R.array.keyring, android.R.layout.simple_spinner_dropdown_item);
        keyspinner.setAdapter(Adapter1);
        ArrayAdapter Adapter2 = ArrayAdapter.createFromResource(this, R.array.phone_case, android.R.layout.simple_spinner_dropdown_item);
        casespinner.setAdapter(Adapter2);
        ArrayAdapter Adapter3 = ArrayAdapter.createFromResource(this, R.array.earring, android.R.layout.simple_spinner_dropdown_item);
        earspinner.setAdapter(Adapter3);
        ArrayAdapter Adapter4 = ArrayAdapter.createFromResource(this, R.array.bracelet, android.R.layout.simple_spinner_dropdown_item);
        bracespinner.setAdapter(Adapter4);
        ArrayAdapter Adapter5 = ArrayAdapter.createFromResource(this, R.array.etc, android.R.layout.simple_spinner_dropdown_item);
        etcspinner.setAdapter(Adapter5);

        //툴바 뒤로가기 버튼 설정
        Toolbar tb = findViewById(R.id.modify_product_toolbar) ;
        setSupportActionBar(tb) ;
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);


        //스피너 눌렸을 때 아이템 값 받아오기 - 키링
        keyspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(final AdapterView<?> parent, View view, final int position, long id) {
                keyspin = parent.getItemAtPosition(position).toString();
                Log.d("키링 스피너", keyspin+" 이다");
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //스피너 눌렸을 때 아이템 값 받아오기 - 폰케이스
        casespinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                casespin = parent.getItemAtPosition(position).toString();
                Log.d("폰케 스피너", casespin+" 이다");
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //스피너 눌렸을 때 아이템 값 받아오기 - 귀걸이
        earspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                earspin = parent.getItemAtPosition(position).toString();
                Log.d("귀걸이 스피너", earspin+" 이다");
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //스피너 눌렸을 때 아이템 값 받아오기 - 팔찌
        bracespinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                bracespin = parent.getItemAtPosition(position).toString();
                Log.d("팔찌 스피너", bracespin+" 이다");
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //스피너 눌렸을 때 아이템 값 받아오기 - 기타
        etcspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                etcspin = parent.getItemAtPosition(position).toString();
                Log.d("기타 스피너", etcspin+" 이다");
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mname.setText(modi_item.getName());
        mprice.setText(modi_item.getPrice());
        mwidth.setText(modi_item.getWidth());
        mheight.setText(modi_item.getHeight());
        mdepth.setText(modi_item.getDepth());
        mstock.setText(modi_item.getStock());
        mkeyword.setText(modi_item.getKeyword());

        String cur_category = modi_item.getCategory();
        if(cur_category.contains("키링")) {
            keycheck.setChecked(true);
            if(cur_category.contains("체인")) keyspinner.setSelection(1);
            else if(cur_category.contains("키링용고리")) keyspinner.setSelection(2);
            else if(cur_category.contains("팬던트")) keyspinner.setSelection(3);
        }
        if(cur_category.contains("폰케이스")) {
            casecheck.setChecked(true);
            if(cur_category.contains("소프트케이스")) casespinner.setSelection(1);
            else if(cur_category.contains("하드케이스")) casespinner.setSelection(2);
            else if(cur_category.contains("파츠")) casespinner.setSelection(3);
        }
        if(cur_category.contains("귀걸이")) {
            earcheck.setChecked(true);
            if(cur_category.contains("귀걸이침")) earspinner.setSelection(1);
            else if(cur_category.contains("팬던트")) earspinner.setSelection(2);
        }
        if(cur_category.contains("팔찌")) {
            bracecheck.setChecked(true);
            if(cur_category.contains("파츠")) bracespinner.setSelection(1);
            else if(cur_category.contains("팔찌대")) bracespinner.setSelection(2);
            else if(cur_category.contains("팬던트_참")) bracespinner.setSelection(3);
        }
        if(cur_category.contains("기타")) {
            etccheck.setChecked(true);
            if(cur_category.contains("공구")) etcspinner.setSelection(1);
            else if(cur_category.contains("부자재")) etcspinner.setSelection(2);
            else if(cur_category.contains("접착제")) etcspinner.setSelection(3);
        }

        modi_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateMaterialDB();
            }
        });

    }

    // 디비의 부자재 정보 변경
    private void UpdateMaterialDB(){

        showProgress();

        name = mname.getText().toString();
        price = mprice.getText().toString();
        width = mwidth.getText().toString();
        height = mheight.getText().toString();
        depth = mdepth.getText().toString();
        stock = mstock.getText().toString();
        keyword = mkeyword.getText().toString();

        //카테고리 가져오기
        String[] category_check = new String[5];
        category_check[0] = getSelectedCategory(keycheck, keyspin);
        category_check[1] = getSelectedCategory(casecheck, casespin);
        category_check[2] = getSelectedCategory(earcheck, earspin);
        category_check[3] = getSelectedCategory(bracecheck, bracespin);
        category_check[4] = getSelectedCategory(etccheck, etcspin);

        for(int k=0; k<5; k++){
            if(!TextUtils.isEmpty(category_check[k])){
                if(TextUtils.isEmpty(category)) category = category_check[k];
                else category = category + "#" + category_check[k];
            }
        }

        myRef.child(modi_item.getUnique_number()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                myRef.child(Objects.requireNonNull(dataSnapshot.getKey())).child("material_name").setValue(name);
                myRef.child(dataSnapshot.getKey()).child("price").setValue(price);
                myRef.child(dataSnapshot.getKey()).child("size_width").setValue(width);
                myRef.child(dataSnapshot.getKey()).child("size_height").setValue(height);
                myRef.child(dataSnapshot.getKey()).child("size_depth").setValue(depth);
                myRef.child(dataSnapshot.getKey()).child("stock").setValue(stock);
                myRef.child(dataSnapshot.getKey()).child("keyword").setValue(keyword);
                myRef.child(dataSnapshot.getKey()).child("category").setValue(category);
                hideProgress();
                //액티비티 종료
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.exit_to_right);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    //선택한 카테고리 스트링으로 반환
    private String getSelectedCategory(CheckBox check, String spinner){
        if(check.isChecked() && !spinner.equals("선택안함")){
            String s_category = check.getText().toString();
            if(s_category.equals("팔찌") || s_category.equals("귀걸이")){
                s_category = "액세서리>" + s_category + ">" +  spinner;
            }
            else s_category = s_category + ">" +  spinner;
            return s_category;
        }
        else return null;
    }

    // 프로그레스 다이얼로그 보이기
    public void showProgress() {
        if( pd == null ) { // 객체를 1회만 생성한다
            pd = new ProgressDialog(ModifySellerProductActivity.this, R.style.NewDialog); // 생성한다.
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
