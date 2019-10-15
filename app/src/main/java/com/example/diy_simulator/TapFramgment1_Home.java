package com.example.diy_simulator;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TapFramgment1_Home extends AppCompatActivity implements View.OnClickListener {

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = database.getReference("판매자");

    int count = 0; //판매자 명수 (storename 개수)
    final List<Tab1_Home_StorenameInfo> storename_item =new ArrayList<>();
    final Tab1_Home_Storename_Adapter storenameAdapter = new Tab1_Home_Storename_Adapter(TapFramgment1_Home.this,storename_item,R.layout.activity_tap_framgment1_home);

    String[][] categorize_storename = new String[15][];
    String n1,n2,n3,n4,n5,n6,n7,n8,n9,n10,n11,n12,n13,n14,n15;

    Button btn1,btn2,btn3,btn4,btn5,btn6,btn7,btn8,btn9,btn10,btn11,btn12,btn13,btn14,btn15;

    public RecyclerView storename_recyclerview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tap_framgment1_home);

        storename_recyclerview = findViewById(R.id.home_storename_recyclerView);
        final LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        storename_recyclerview.setHasFixedSize(true);
        storename_recyclerview.setLayoutManager(layoutManager);
        storename_recyclerview.setAdapter(storenameAdapter);

        getStoreNameCategorize();

        btn1 = findViewById(R.id.kor1);
        btn2 = findViewById(R.id.kor2);
        btn3 = findViewById(R.id.kor3);
        btn4 = findViewById(R.id.kor4);
        btn5 = findViewById(R.id.kor5);
        btn6 = findViewById(R.id.kor6);
        btn7 = findViewById(R.id.kor7);
        btn8 = findViewById(R.id.kor8);
        btn9 = findViewById(R.id.kor9);
        btn10 = findViewById(R.id.kor10);
        btn11 = findViewById(R.id.kor11);
        btn12 = findViewById(R.id.kor12);
        btn13 = findViewById(R.id.kor13);
        btn14 = findViewById(R.id.kor14);
        btn15 = findViewById(R.id.kor15);

        btn1.setOnClickListener((View.OnClickListener) this);
        btn2.setOnClickListener((View.OnClickListener) this);
        btn3.setOnClickListener((View.OnClickListener) this);
        btn4.setOnClickListener((View.OnClickListener) this);
        btn5.setOnClickListener((View.OnClickListener) this);
        btn6.setOnClickListener((View.OnClickListener) this);
        btn7.setOnClickListener((View.OnClickListener) this);
        btn8.setOnClickListener((View.OnClickListener) this);
        btn9.setOnClickListener((View.OnClickListener) this);
        btn10.setOnClickListener((View.OnClickListener) this);
        btn11.setOnClickListener((View.OnClickListener) this);
        btn12.setOnClickListener((View.OnClickListener) this);
        btn13.setOnClickListener((View.OnClickListener) this);
        btn14.setOnClickListener((View.OnClickListener) this);
        btn15.setOnClickListener((View.OnClickListener) this);

    }

    //파이어베이스에서 판매자의 storname을 모두 가져와서 names[] 배열에 넣기
    //names[]를 initial_Categorizing 넣어서 실행
    private void getStoreNameCategorize(){
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                count =(int) dataSnapshot.getChildrenCount();
                String[] names = new String[count];
                int i = 0;
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    names[i] = ds.child("storename").getValue().toString(); //상호명
                    i++;
                }
                initial_Categorizing(names);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //상호명을 가나다순으로 분류해 n1~n15 스트링에 #으로 구분하여 담아오는 함수
    private void initial_Categorizing(String[] names){
        for(int i = 0; i < names.length; i++){
            String init  = getInitialSound(names[i]); //상호명의 초성
            //etc - 영어, 숫자로 시작
            if(TextUtils.isEmpty(init)){
                if(TextUtils.isEmpty(n15)) n15 = names[i];
                else n15 = n15+"#"+names[i];
                Log.d("기타 : ", n15);
            }
            //ㄱ - 기역 + 쌍기역
            else if(init.equals("ㄱ")){
                if(TextUtils.isEmpty(n1)) n1 = names[i];
                else n1 = n1+"#"+names[i];
                Log.d("기역 : ", n1);
            }
            else if(init.equals("ㄲ")){
                if(TextUtils.isEmpty(n1)) n1 = names[i];
                else n1 = n1+"#"+names[i];
                Log.d("쌍기역 : ", n1);
            }
            //ㄴ
            else if(init.equals("ㄴ")){
                if(TextUtils.isEmpty(n2)) n2 = names[i];
                else n2 = n2+"#"+names[i];
                Log.d("니은 : ", n2);
            }
            //ㄷ - 디귿 + 쌍디귿
            else if(init.equals("ㄷ")){
                if(TextUtils.isEmpty(n3)) n3 = names[i];
                else n3 = n3+"#"+names[i];
                Log.d("디귿 : ", n3);
            }
            else if(init.equals("ㄸ")){
                if(TextUtils.isEmpty(n3)) n3 = names[i];
                else n3 = n3+"#"+names[i];
                Log.d("쌍디귿 : ", n3);
            }
            //ㄹ
            else if(init.equals("ㄹ")){
                if(TextUtils.isEmpty(n4)) n4 = names[i];
                else n4 = n4+"#"+names[i];
                Log.d("리을 : ", n4);
            }
            //ㅁ
            else if(init.equals("ㅁ")){
                if(TextUtils.isEmpty(n5)) n5 = names[i];
                else n5 = n5+"#"+names[i];
                Log.d("미음 : ", n5);
            }
            //ㅂ - 비읍 + 쌍비읍
            else if(init.equals("ㅂ")){
                if(TextUtils.isEmpty(n6)) n6 = names[i];
                else n6 = n6+"#"+names[i];
                Log.d("비읍 : ", n6);
            }
            else if(init.equals("ㅃ")){
                if(TextUtils.isEmpty(n6)) n6 = names[i];
                else n6 = n6+"#"+names[i];
                Log.d("쌍비읍 : ", n6);
            }
            //ㅅ - 시옷 + 쌍시옷
            else if(init.equals("ㅅ")){
                if(TextUtils.isEmpty(n7)) n7 = names[i];
                else n7 = n7+"#"+names[i];
                Log.d("시옷 : ", n7);
            }
            else if(init.equals("ㅆ")){
                if(TextUtils.isEmpty(n7)) n7 = names[i];
                else n7 = n7+"#"+names[i];
                Log.d("쌍시옷 : ", n7);
            }
            //ㅇ
            else if(init.equals("ㅇ")){
                if(TextUtils.isEmpty(n8)) n8 = names[i];
                else n8 = n8+"#"+names[i];
                Log.d("이응 : ", n8);
            }
            //ㅈ - 지읒 + 쌍지읒
            else if(init.equals("ㅈ")){
                if(TextUtils.isEmpty(n9)) n9 = names[i];
                else n9 = n9+"#"+names[i];
                Log.d("지읒 : ", n9);
            }
            else if(init.equals("ㅉ")){
                if(TextUtils.isEmpty(n9)) n9 = names[i];
                else n9 = n9+"#"+names[i];
                Log.d("쌍지읒 : ", n9);
            }
            //ㅊ
            else if(init.equals("ㅊ")){
                if(TextUtils.isEmpty(n10)) n10 = names[i];
                else n10 = n10+"#"+names[i];
                Log.d("치읓 : ", n10);
            }
            //ㅋ
            else if(init.equals("ㅋ")){
                if(TextUtils.isEmpty(n11)) n11 = names[i];
                else n11 = n11+"#"+names[i];
                Log.d("키읔 : ", n11);
            }
            //ㅌ
            else if(init.equals("ㅌ")){
                if(TextUtils.isEmpty(n12)) n12 = names[i];
                else n12 = n12+"#"+names[i];
                Log.d("티읕 : ", n12);
            }
            //ㅍ
            else if(init.equals("ㅍ")){
                if(TextUtils.isEmpty(n13)) n13 = names[i];
                else n13 = n13+"#"+names[i];
                Log.d("피읖 : ", n13);
            }
            //ㅎ
            else if(init.equals("ㅎ")){
                if(TextUtils.isEmpty(n14)) n14 = names[i];
                else n14 = n14+"#"+names[i];
                Log.d("히읗 : ", n14);
            }
        }
    }

    //스트링의 초성 자음을 얻어오는 함수
    private String getInitialSound(String text) {
        String[] chs = {
                "ㄱ", "ㄲ", "ㄴ", "ㄷ", "ㄸ",
                "ㄹ", "ㅁ", "ㅂ", "ㅃ", "ㅅ",
                "ㅆ", "ㅇ", "ㅈ", "ㅉ", "ㅊ",
                "ㅋ", "ㅌ", "ㅍ", "ㅎ"
        };

        if(text.length() > 0) {
            char chName = text.charAt(0);
            if(chName >= 0xAC00)
            {
                int uniVal = chName - 0xAC00;
                int cho = ((uniVal - (uniVal % 28))/28)/21;

                return chs[cho];
            }
        }
        return null;
    }

    //버튼에 따라 가게 이름 출력
    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.kor1 :
                storename_item.clear();
                showStoreNameList(n1, 1);
                break;
            case R.id.kor2 :
                storename_item.clear();
                showStoreNameList(n2, 2);
                break;
            case R.id.kor3 :
                storename_item.clear();
                showStoreNameList(n3, 3);
                break;
            case R.id.kor4 :
                storename_item.clear();
                showStoreNameList(n4, 4);
                break;
            case R.id.kor5 :
                storename_item.clear();
                showStoreNameList(n5, 5);
                break;
            case R.id.kor6 :
                storename_item.clear();
                showStoreNameList(n6, 6);
                break;
            case R.id.kor7 :
                storename_item.clear();
                showStoreNameList(n7, 7);
                break;
            case R.id.kor8 :
                storename_item.clear();
                showStoreNameList(n8, 8);
                break;
            case R.id.kor9 :
                storename_item.clear();
                showStoreNameList(n9, 9);
                break;
            case R.id.kor10 :
                storename_item.clear();
                showStoreNameList(n10, 10);
                break;
            case R.id.kor11:
                storename_item.clear();
                showStoreNameList(n11, 11);
                break;
            case R.id.kor12 :
                storename_item.clear();
                showStoreNameList(n12, 12);
                break;
            case R.id.kor13 :
                storename_item.clear();
                showStoreNameList(n13, 13);
                break;
            case R.id.kor14 :
                storename_item.clear();
                showStoreNameList(n14, 14);
                break;
            case R.id.kor15 :
                storename_item.clear();
                showStoreNameList(n15, 15);
                break;

        }
    }
    //리사이클러뷰에 가게 이름 add
    private void showStoreNameList(String cho , int idx){
        if(!TextUtils.isEmpty(cho)){
            categorize_storename[idx - 1] = cho.split("#");
            Arrays.sort(categorize_storename[idx - 1]);  //사전순 정렬
            Tab1_Home_StorenameInfo[] item = new Tab1_Home_StorenameInfo[categorize_storename[idx - 1].length];
            for (int i = 0; i < categorize_storename[idx - 1].length; i++) {
                item[i] = new Tab1_Home_StorenameInfo(categorize_storename[idx - 1][i]);
                storename_item.add(item[i]);
            }
        }
        else {
            storename_item.add(new Tab1_Home_StorenameInfo("결과가 없습니다."));
        }
        storenameAdapter.notifyDataSetChanged();
    }

}
