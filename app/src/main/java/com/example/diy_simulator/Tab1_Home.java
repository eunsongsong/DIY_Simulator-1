package com.example.diy_simulator;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
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

public class Tab1_Home extends Fragment implements View.OnClickListener {

    private final List<Tab1_Home_StorenameInfo> storename_item = new ArrayList<>();
    private final Tab1_Home_Storename_Adapter storenameAdapter = new Tab1_Home_Storename_Adapter(getContext(), storename_item, R.layout.fragment_tab1_home);

    private String[][] categorize_storename = new String[15][];
    private ArrayList<String> names;  //모든 상호명이 담긴 배열
    private String n1, n2, n3, n4, n5, n6, n7, n8, n9, n10, n11, n12, n13, n14, n15;

    public RecyclerView storename_recyclerview;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final ViewGroup rootview = (ViewGroup) inflater.inflate(R.layout.fragment_tab1_home, container, false);

        if(getArguments() != null)
            names = getArguments().getStringArrayList("names");

        //names를 초성별로 나누기
        Initial_Categorizing_Names(names);
        Button btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8, btn9, btn10, btn11, btn12, btn13, btn14, btn15;
        storename_recyclerview = rootview.findViewById(R.id.home_storename_recyclerView);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        storename_recyclerview.setHasFixedSize(true);
        storename_recyclerview.setLayoutManager(layoutManager);
        storename_recyclerview.setAdapter(storenameAdapter);

        //가게별 검색 결과 클릭시 해당 가게 페이지로 이동
        storenameAdapter.setOnItemClickListener(new Tab1_Home_Storename_Adapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int positon) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fm.beginTransaction();
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.replace(R.id.tab1_layout, new HomeSearch_Store()).commit();
            }
        });

        btn1 = rootview.findViewById(R.id.kor1);
        btn2 = rootview.findViewById(R.id.kor2);
        btn3 = rootview.findViewById(R.id.kor3);
        btn4 = rootview.findViewById(R.id.kor4);
        btn5 = rootview.findViewById(R.id.kor5);
        btn6 = rootview.findViewById(R.id.kor6);
        btn7 = rootview.findViewById(R.id.kor7);
        btn8 = rootview.findViewById(R.id.kor8);
        btn9 = rootview.findViewById(R.id.kor9);
        btn10 = rootview.findViewById(R.id.kor10);
        btn11 = rootview.findViewById(R.id.kor11);
        btn12 = rootview.findViewById(R.id.kor12);
        btn13 = rootview.findViewById(R.id.kor13);
        btn14 = rootview.findViewById(R.id.kor14);
        btn15 = rootview.findViewById(R.id.kor15);

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

        return rootview;
    }

    //상호명을 가나다순으로 분류해 n1~n15 스트링에 #으로 구분하여 담아오는 함수
    private void Initial_Categorizing_Names(ArrayList<String> names) {
        for (int i = 0; i < names.size(); i++) {
            String init = getInitialSound(names.get(i)); //상호명의 초성
            //etc - 영어, 숫자로 시작
            if (TextUtils.isEmpty(init)) {
                if (TextUtils.isEmpty(n15)) n15 = names.get(i);
                else n15 = n15 + "#" + names.get(i);
                Log.d("기타 : ", n15);
            }
            //ㄱ - 기역 + 쌍기역
            else if (init.equals("ㄱ")) {
                if (TextUtils.isEmpty(n1)) n1 = names.get(i);
                else n1 = n1 + "#" + names.get(i);
                Log.d("기역 : ", n1);
            } else if (init.equals("ㄲ")) {
                if (TextUtils.isEmpty(n1)) n1 = names.get(i);
                else n1 = n1 + "#" + names.get(i);
                Log.d("쌍기역 : ", n1);
            }
            //ㄴ
            else if (init.equals("ㄴ")) {
                if (TextUtils.isEmpty(n2)) n2 = names.get(i);
                else n2 = n2 + "#" + names.get(i);
                Log.d("니은 : ", n2);
            }
            //ㄷ - 디귿 + 쌍디귿
            else if (init.equals("ㄷ")) {
                if (TextUtils.isEmpty(n3)) n3 = names.get(i);
                else n3 = n3 + "#" + names.get(i);
                Log.d("디귿 : ", n3);
            } else if (init.equals("ㄸ")) {
                if (TextUtils.isEmpty(n3)) n3 = names.get(i);
                else n3 = n3 + "#" + names.get(i);
                Log.d("쌍디귿 : ", n3);
            }
            //ㄹ
            else if (init.equals("ㄹ")) {
                if (TextUtils.isEmpty(n4)) n4 = names.get(i);
                else n4 = n4 + "#" + names.get(i);
                Log.d("리을 : ", n4);
            }
            //ㅁ
            else if (init.equals("ㅁ")) {
                if (TextUtils.isEmpty(n5)) n5 = names.get(i);
                else n5 = n5 + "#" + names.get(i);
                Log.d("미음 : ", n5);
            }
            //ㅂ - 비읍 + 쌍비읍
            else if (init.equals("ㅂ")) {
                if (TextUtils.isEmpty(n6)) n6 = names.get(i);
                else n6 = n6 + "#" + names.get(i);
                Log.d("비읍 : ", n6);
            } else if (init.equals("ㅃ")) {
                if (TextUtils.isEmpty(n6)) n6 = names.get(i);
                else n6 = n6 + "#" + names.get(i);
                Log.d("쌍비읍 : ", n6);
            }
            //ㅅ - 시옷 + 쌍시옷
            else if (init.equals("ㅅ")) {
                if (TextUtils.isEmpty(n7)) n7 = names.get(i);
                else n7 = n7 + "#" + names.get(i);
                Log.d("시옷 : ", n7);
            } else if (init.equals("ㅆ")) {
                if (TextUtils.isEmpty(n7)) n7 = names.get(i);
                else n7 = n7 + "#" + names.get(i);
                Log.d("쌍시옷 : ", n7);
            }
            //ㅇ
            else if (init.equals("ㅇ")) {
                if (TextUtils.isEmpty(n8)) n8 = names.get(i);
                else n8 = n8 + "#" +names.get(i);
                Log.d("이응 : ", n8);
            }
            //ㅈ - 지읒 + 쌍지읒
            else if (init.equals("ㅈ")) {
                if (TextUtils.isEmpty(n9)) n9 = names.get(i);
                else n9 = n9 + "#" + names.get(i);
                Log.d("지읒 : ", n9);
            } else if (init.equals("ㅉ")) {
                if (TextUtils.isEmpty(n9)) n9 = names.get(i);
                else n9 = n9 + "#" + names.get(i);
                Log.d("쌍지읒 : ", n9);
            }
            //ㅊ
            else if (init.equals("ㅊ")) {
                if (TextUtils.isEmpty(n10)) n10 = names.get(i);
                else n10 = n10 + "#" + names.get(i);
                Log.d("치읓 : ", n10);
            }
            //ㅋ
            else if (init.equals("ㅋ")) {
                if (TextUtils.isEmpty(n11)) n11 = names.get(i);
                else n11 = n11 + "#" + names.get(i);
                Log.d("키읔 : ", n11);
            }
            //ㅌ
            else if (init.equals("ㅌ")) {
                if (TextUtils.isEmpty(n12)) n12 = names.get(i);
                else n12 = n12 + "#" + names.get(i);
                Log.d("티읕 : ", n12);
            }
            //ㅍ
            else if (init.equals("ㅍ")) {
                if (TextUtils.isEmpty(n13)) n13 = names.get(i);
                else n13 = n13 + "#" + names.get(i);
                Log.d("피읖 : ", n13);
            }
            //ㅎ
            else if (init.equals("ㅎ")) {
                if (TextUtils.isEmpty(n14)) n14 = names.get(i);
                else n14 = n14 + "#" + names.get(i);
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

        if (text.length() > 0) {
            char chName = text.charAt(0);
            if (chName >= 0xAC00) {
                int uniVal = chName - 0xAC00;
                int cho = ((uniVal - (uniVal % 28)) / 28) / 21;

                return chs[cho];
            }
        }
        return null;
    }

    //버튼에 따라 가게 이름 출력
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.kor1:
                storename_item.clear();
                showStoreNameList(n1, 1);
                break;
            case R.id.kor2:
                storename_item.clear();
                showStoreNameList(n2, 2);
                break;
            case R.id.kor3:
                storename_item.clear();
                showStoreNameList(n3, 3);
                break;
            case R.id.kor4:
                storename_item.clear();
                showStoreNameList(n4, 4);
                break;
            case R.id.kor5:
                storename_item.clear();
                showStoreNameList(n5, 5);
                break;
            case R.id.kor6:
                storename_item.clear();
                showStoreNameList(n6, 6);
                break;
            case R.id.kor7:
                storename_item.clear();
                showStoreNameList(n7, 7);
                break;
            case R.id.kor8:
                storename_item.clear();
                showStoreNameList(n8, 8);
                break;
            case R.id.kor9:
                storename_item.clear();
                showStoreNameList(n9, 9);
                break;
            case R.id.kor10:
                storename_item.clear();
                showStoreNameList(n10, 10);
                break;
            case R.id.kor11:
                storename_item.clear();
                showStoreNameList(n11, 11);
                break;
            case R.id.kor12:
                storename_item.clear();
                showStoreNameList(n12, 12);
                break;
            case R.id.kor13:
                storename_item.clear();
                showStoreNameList(n13, 13);
                break;
            case R.id.kor14:
                storename_item.clear();
                showStoreNameList(n14, 14);
                break;
            case R.id.kor15:
                storename_item.clear();
                showStoreNameList(n15, 15);
                break;
        }
    }

    //리사이클러뷰에 가게 이름 add
    private void showStoreNameList(String cho, int idx) {
        if (!TextUtils.isEmpty(cho)) {
            categorize_storename[idx - 1] = cho.split("#");
            Arrays.sort(categorize_storename[idx - 1]);  //사전순 정렬
            Tab1_Home_StorenameInfo[] item = new Tab1_Home_StorenameInfo[categorize_storename[idx - 1].length];
            for (int i = 0; i < categorize_storename[idx - 1].length; i++) {
                item[i] = new Tab1_Home_StorenameInfo(categorize_storename[idx - 1][i]);
                storename_item.add(item[i]);
            }
        } else {
            storename_item.add(new Tab1_Home_StorenameInfo("결과가 없습니다."));
        }
        storenameAdapter.notifyDataSetChanged();
    }


}

