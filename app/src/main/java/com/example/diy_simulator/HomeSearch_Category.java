package com.example.diy_simulator;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeSearch_Category extends Fragment {

    String BIGcategory = "";
    private Button sub1, sub2, sub3;
    private String[] acc_sub = {"귀걸이", "팔찌"};  //액세서리의 세부카테고리
    private String[] acc_ear_sub = {"귀걸이침", "팬던트"}; //액세서리 - 귀걸이의 세부카테고리
    private String[] acc_brac_sub = {"파츠", "팔찌대", "팬던트_참"}; //액세서리 - 팔찌의 세부카테고리
    private String[] key_sub = {"체인", "키링용고리", "팬던트"}; //키링의 세부카테고리
    private String[] phone_sub = {"파츠", "소프트케이스", "하드케이스"}; //폰케이스의 세부카테고리
    private String[] etc_sub = {"공구", "부자재", "접착제"}; //기타의 세부카테고리
    ProgressDialog pd;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("부자재");

    private ImageView noitem;
    public RecyclerView search_category_recyclerview;
    private List<Material_Detail_Info> category_item = new ArrayList<>();
    private final HomeSearch_Category_Adapter categoryAdapter = new HomeSearch_Category_Adapter(getContext(),
            category_item, R.layout.fragment_home_search_category, HomeSearch_Category.this);

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final ViewGroup rootview = (ViewGroup) inflater.inflate(R.layout.fragment_home_search_category, container, false);

        TextView title = rootview.findViewById(R.id.search_category_toolbar_title);
        final TextView cur_category = rootview.findViewById(R.id.current_category_search);
        //버튼 3개
        sub1 = rootview.findViewById(R.id.category_detail_btn1);
        sub2 = rootview.findViewById(R.id.category_detail_btn2);
        sub3 = rootview.findViewById(R.id.category_detail_btn3);
        noitem = rootview.findViewById(R.id.category_product_ready_img);

        //그리드 레이아웃으로 한줄에 2개씩 제품 보여주기
        search_category_recyclerview = rootview.findViewById(R.id.search_category_recyclerView);
        final GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        search_category_recyclerview.setHasFixedSize(true);
        search_category_recyclerview.setLayoutManager(layoutManager);
        search_category_recyclerview.setAdapter(categoryAdapter);

        //툴바 뒤로가기 버튼 설정
        Toolbar tb = rootview.findViewById(R.id.search_category_toolbar) ;
        ((AppCompatActivity) getActivity()).setSupportActionBar(tb) ;
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);

        //탭1(홈) 프래그먼트에서 보낸 카테고리 스트링 얻어오기
        BIGcategory = getArguments().getString("category");
        Log.i("카테고리", BIGcategory+"");
        //카테고리를 툴바 타이틀로 지정
        title.setText(BIGcategory);
        cur_category.setText(BIGcategory);

        category_item.clear();
        showProgress();

        //해당 카테고리의 아이템 리사이클러뷰에 add
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    if(ds.child("category").getValue().toString().contains(BIGcategory)){
                        String name = ds.child("material_name").getValue().toString();
                        String price = ds.child("price").getValue().toString();
                        String width = ds.child("size_width").getValue().toString();
                        String height = ds.child("size_height").getValue().toString();
                        String depth = ds.child("size_depth").getValue().toString();
                        String stock = ds.child("stock").getValue().toString();
                        String keyword = ds.child("keyword").getValue().toString();
                        String storename = ds.child("storename").getValue().toString();
                        String category = ds.child("category").getValue().toString();
                        //이미지 url 가져오기
                        String[] url = new String[(int)ds.child("image_url").getChildrenCount()];
                        int k = 0 ;
                        for(DataSnapshot ds2 : ds.child("image_url").getChildren()){
                            url[k] = ds2.getValue().toString();
                            k++;
                        }
                        //이미지 url의 0번이 상품 대표 이미지
                        String preview = url[0];
                        //리사이클러뷰에 아이템 add
                        Material_Detail_Info item = new Material_Detail_Info(name, price+" 원",
                                preview, url, width, height, depth, keyword, stock, storename, ds.getKey(), category);
                        category_item.add(item);
                    }
                }
                hideProgress();
                categoryAdapter.getFilter().filter("");
                showEmptyOrNot();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //버튼 텍스트 설정
        if(BIGcategory.equals("액세서리")){
            sub1.setText(acc_sub[0]);
            sub2.setText(acc_sub[1]);
            sub3.setVisibility(View.INVISIBLE);
        }
        else if(BIGcategory.equals("키링")) {
            sub1.setText(key_sub[0]);
            sub2.setText(key_sub[1]);
            sub3.setText(key_sub[2]);
        }
        else if(BIGcategory.equals("폰케이스")){
            sub1.setText(phone_sub[0]);
            sub2.setText(phone_sub[1]);
            sub3.setText(phone_sub[2]);
        }
        else {
            sub1.setText(etc_sub[0]);
            sub2.setText(etc_sub[1]);
            sub3.setText(etc_sub[2]);
        }

        //세부 카테고리 버튼 클릭시 해당 카테고리의 아이템만 보이기
        sub1.setOnClickListener(new View.OnClickListener() { //버튼1
            @Override
            public void onClick(View v) {

                categoryAdapter.getFilter().filter(sub1.getText().toString());
                category_item = categoryAdapter.getFilteredList();
                categoryAdapter.getFilter().filter(sub1.getText().toString());
                category_item = categoryAdapter.getFilteredList();

                //카테고리 액세서리 - 귀걸이 클릭
                if (sub1.getText().toString().equals(acc_sub[0])) {
                    sub1.setText(acc_ear_sub[0]);
                    sub2.setText(acc_ear_sub[1]);
                    cur_category.setText(BIGcategory + " > " + acc_sub[0]);
                }

                //귀걸이 or 팔찌의 세부 카테고리 클릭
                else{
                    sub1.setTextColor(Color.parseColor("#3DC1AB"));
                    sub2.setTextColor(Color.parseColor("#181818"));
                    sub3.setTextColor(Color.parseColor("#181818"));
                    //카테고리 액세서리
                    //귀걸이의 세부 카테고리 - 귀걸이 침 클릭
                    if (sub1.getText().toString().equals(acc_ear_sub[0])){
                        cur_category.setText(BIGcategory + " > " + acc_sub[0] + " > " + acc_ear_sub[0]);
                    }
                    //팔찌의 세부 카테고리 - 파츠 클릭
                    else if (BIGcategory.equals("액세서리") && sub1.getText().toString().equals(acc_brac_sub[0])){
                        cur_category.setText(BIGcategory + " > " + acc_sub[1] + " > " + acc_brac_sub[0]);
                    }
                    //카테고리가 액세서리가 아닐시
                    else{
                        cur_category.setText(BIGcategory + " > " +sub1.getText().toString());
                    }
                }
                showEmptyOrNot();
            }
        });
        sub2.setOnClickListener(new View.OnClickListener() { //버튼2
            @Override
            public void onClick(View v) {

                categoryAdapter.getFilter().filter(sub2.getText().toString());
                category_item = categoryAdapter.getFilteredList();
                categoryAdapter.getFilter().filter(sub2.getText().toString());
                category_item = categoryAdapter.getFilteredList();

                //카테고리 액세서리 - 팔찌 클릭
                if(sub2.getText().toString().equals(acc_sub[1])) {
                    sub1.setText(acc_brac_sub[0]);
                    sub2.setText(acc_brac_sub[1]);
                    sub3.setText(acc_brac_sub[2]);
                    sub3.setVisibility(View.VISIBLE);
                    cur_category.setText(BIGcategory + " > " + acc_sub[1]);
                }
                else {
                    sub1.setTextColor(Color.parseColor("#181818"));
                    sub2.setTextColor(Color.parseColor("#3DC1AB"));
                    sub3.setTextColor(Color.parseColor("#181818"));
                    //카테고리 액세서리
                    //귀걸이의 세부 카테고리 - 팬던트 클릭
                    if(sub2.getText().toString().equals(acc_ear_sub[1])) {
                        cur_category.setText(BIGcategory + " > " + acc_sub[0] + " > " + acc_ear_sub[1]);
                    }
                    //팔찌의 세부 카테고리 - 팔찌대 클릭
                    else if(sub2.getText().toString().equals(acc_brac_sub[1])) {
                        cur_category.setText(BIGcategory + " > " + acc_sub[1] + " > " + acc_brac_sub[1]);
                    }
                    //카테고리가 액세서리가 아닐시
                    else {
                        cur_category.setText(BIGcategory + " > " +sub2.getText().toString());
                    }
                }
                showEmptyOrNot();
            }
        });
        sub3.setOnClickListener(new View.OnClickListener() { //버튼3
            @Override
            public void onClick(View v) {
                categoryAdapter.getFilter().filter(sub3.getText().toString());
                category_item = categoryAdapter.getFilteredList();
                categoryAdapter.getFilter().filter(sub3.getText().toString());
                category_item = categoryAdapter.getFilteredList();

                sub1.setTextColor(Color.parseColor("#181818"));
                sub2.setTextColor(Color.parseColor("#181818"));
                sub3.setTextColor(Color.parseColor("#3DC1AB"));
                //팔찌의 세부 카테고리 - 팬던트_참 클릭
                if(BIGcategory.equals("액세서리") && sub3.getText().toString().equals(acc_brac_sub[2])){
                    cur_category.setText(BIGcategory + " > " + acc_sub[1] + " > " + acc_brac_sub[2]);
                }
                else{
                    //카테고리가 액세서리가 아닐시
                    cur_category.setText(BIGcategory + " > " +sub3.getText().toString());
                }
                showEmptyOrNot();
            }
        });

        return rootview;
    }

    //버튼 눌림에 따라 세부 카테고리로 필터링한 아이템 보여주기
    public void showEmptyOrNot(){
        if(category_item.isEmpty()) {
            Log.i("노아이템", "노노");
            noitem.setVisibility(View.VISIBLE);
            search_category_recyclerview.setVisibility(View.GONE);
        }
        else{
            Log.i("예스", "아이템");
            noitem.setVisibility(View.GONE);
            search_category_recyclerview.setVisibility(View.VISIBLE);
            categoryAdapter.notifyDataSetChanged();
        }
    }

    // 프로그레스 다이얼로그 보이기
    public void showProgress() {
        if( pd == null ) { // 객체를 1회만 생성한다
            pd = new ProgressDialog(getContext(), R.style.NewDialog); // 생성한다.
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
