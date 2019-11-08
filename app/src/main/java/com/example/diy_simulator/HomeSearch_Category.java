package com.example.diy_simulator;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
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

    String category = "";
    String[] material = new String[5];
    Button sub1, sub2, sub3;
    String[] acc_sub = new String[2];  //액세서리의 세부카테고리
    String[] acc_sub_sub = new String[5]; //액세서리 - 세부의 세부카테고리

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef2 = database.getReference("부자재");

    public RecyclerView search_category_recyclerview;
    private final List<Material_Detail_Info> category_item = new ArrayList<>();
    private final HomeSearch_Category_Adapter categoryAdapter = new HomeSearch_Category_Adapter(getContext(),
            category_item, R.layout.fragment_home_search_category);

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final ViewGroup rootview = (ViewGroup) inflater.inflate(R.layout.fragment_home_search_category, container, false);

        TextView title = rootview.findViewById(R.id.search_category_toolbar_title);
        final TextView cur_category = rootview.findViewById(R.id.current_category_search);
        sub1 = rootview.findViewById(R.id.category_detail_btn1);
        sub2 = rootview.findViewById(R.id.category_detail_btn2);
        sub3 = rootview.findViewById(R.id.category_detail_btn3);

        //그리드 레이아웃으로 한줄에 2개씩 제품 보여주기
        search_category_recyclerview = rootview.findViewById(R.id.search_category_recyclerView);
        final GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        search_category_recyclerview.setHasFixedSize(true);
        search_category_recyclerview.setLayoutManager(layoutManager);
        search_category_recyclerview.setAdapter(categoryAdapter);

        category_item.clear();

        //툴바 뒤로가기 버튼 설정
        Toolbar tb = rootview.findViewById(R.id.search_category_toolbar) ;
        ((AppCompatActivity) getActivity()).setSupportActionBar(tb) ;
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);

        //탭1(홈) 프래그먼트에서 보낸 카테고리 스트링 얻어오기
        category = getArguments().getString("category");
        //데이터베이스 참조를 해당 카테고리로 지정
        DatabaseReference myRef = database.getReference("카테고리").child(category);
        //카테고리를 툴바 타이틀로 지정
        title.setText(category);
        cur_category.setText(category);

        if(category.equals("액세서리")){
            // 세부 카테고리와 부자재 번호 찾아오기 - 액세서리
            // 액세서리인 경우 child 한번 더 깊이 검색
            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                int i = 0;
                int k = 0;
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for(DataSnapshot ds : dataSnapshot.getChildren()){
                        //세부 카테고리 버튼 텍스트 설정
                        acc_sub[i] = ds.getKey();
                        if(i==0) sub1.setText(acc_sub[i]);
                        else if(i==1) sub2.setText(acc_sub[i]);
                        //부자재 번호 가져와서 부자재 정보 찾는 함수로 넘기기
                        for(DataSnapshot ds2 : ds.getChildren()){
                            acc_sub_sub[k] = ds2.getKey();
                            material[k] = ds2.getValue().toString();
                            if(!TextUtils.isEmpty(material[k])) findMaterialInfo(material[k]);
                            k++;
                        }
                        i++;
                    }
                    //액세서리의 child가 2개이므로 3번째 버튼 invisible 설정
                    sub3.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        else{  //세부 카테고리와 부자재 번호 찾아오기 - 키링, 폰케이스, 기타
            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                int i =0;
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    // 현재 3개 카테고리의 child가 3개로 모두 동일하므로 예외 처리 하지 않았음
                    for(DataSnapshot ds : dataSnapshot.getChildren()){
                        //부자재 번호 가져와서 부자재 정보 찾는 함수로 넘기기
                        material[i] = ds.getValue().toString();
                        if(!TextUtils.isEmpty(material[i])) findMaterialInfo(material[i]);
                        //세부 카테고리 버튼 텍스트 설정
                        if(i==0) {
                            sub1.setText(ds.getKey());
                        }
                        else if(i==1){
                            sub2.setText(ds.getKey());
                        }
                        else{
                            sub3.setText(ds.getKey());
                        }
                        i++;
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        //아이템 클릭시 상품 상세 페이지로 이동
        categoryAdapter.setOnItemClickListener(new HomeSearch_Category_Adapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                movetoProductDetail(position);
            }
        });

        //세부 카테고리 버튼 클릭시 해당 카테고리의 아이템만 보이기
        sub1.setOnClickListener(new View.OnClickListener() { //버튼1
            @Override
            public void onClick(View v) {
                //카테고리 액세서리 - 귀걸이 클릭
                if (sub1.getText().toString().equals(acc_sub[0])) {
                    sub1.setText(acc_sub_sub[0]);
                    sub2.setText(acc_sub_sub[1]);
                    showSubCategoryResult(0);
                    showSubCategoryResult(1);
                    cur_category.setText(category + " > " + acc_sub[0]);
                }
                //귀걸이 or 팔찌의 세부 카테고리 클릭
                else {
                    sub1.setTextColor(Color.parseColor("#3DC1AB"));
                    sub2.setTextColor(Color.parseColor("#181818"));
                    sub3.setTextColor(Color.parseColor("#181818"));
                    //카테고리 액세서리
                    //귀걸이의 세부 카테고리 - 귀걸이 침 클릭
                    if (sub1.getText().toString().equals(acc_sub_sub[0])){
                        showSubCategoryResult(0);
                        cur_category.setText(category + " > " + acc_sub[0] + " > " + acc_sub_sub[0]);
                    }
                    //팔찌의 세부 카테고리 - 파츠 클릭
                    else if (sub1.getText().toString().equals(acc_sub_sub[2])){
                        showSubCategoryResult(2);
                        cur_category.setText(category + " > " + acc_sub[1] + " > " + acc_sub_sub[2]);
                    }
                    //카테고리가 액세서리가 아닐시
                    else{
                        showSubCategoryResult(0);
                        cur_category.setText(category + " > " +sub1.getText().toString());
                    }
                }
            }
        });
        sub2.setOnClickListener(new View.OnClickListener() { //버튼2
            @Override
            public void onClick(View v) {
                //카테고리 액세서리 - 팔찌 클릭
                if(sub2.getText().toString().equals(acc_sub[1])) {
                    sub1.setText(acc_sub_sub[2]);
                    sub2.setText(acc_sub_sub[3]);
                    sub3.setVisibility(View.VISIBLE);
                    sub3.setText(acc_sub_sub[4]);
                    showSubCategoryResult(2);
                    showSubCategoryResult(3);
                    showSubCategoryResult(4);
                    cur_category.setText(category + " > " + acc_sub[1]);
                }
                else {
                    sub1.setTextColor(Color.parseColor("#181818"));
                    sub2.setTextColor(Color.parseColor("#3DC1AB"));
                    sub3.setTextColor(Color.parseColor("#181818"));
                    //카테고리 액세서리
                    //귀걸이의 세부 카테고리 - 팬던트 클릭
                    if(sub2.getText().toString().equals(acc_sub_sub[1])) {
                        showSubCategoryResult(1);
                        cur_category.setText(category + " > " + acc_sub[0] + " > " + acc_sub_sub[1]);
                    }
                    //팔찌의 세부 카테고리 - 팔찌대 클릭
                    else if(sub2.getText().toString().equals(acc_sub_sub[3])) {
                        showSubCategoryResult(3);
                        cur_category.setText(category + " > " + acc_sub[1] + " > " + acc_sub_sub[3]);
                    }
                    //카테고리가 액세서리가 아닐시
                    else {
                        showSubCategoryResult(1);
                        cur_category.setText(category + " > " +sub2.getText().toString());
                    }
                }
            }
        });
        sub3.setOnClickListener(new View.OnClickListener() { //버튼3
            @Override
            public void onClick(View v) {
                sub1.setTextColor(Color.parseColor("#181818"));
                sub2.setTextColor(Color.parseColor("#181818"));
                sub3.setTextColor(Color.parseColor("#3DC1AB"));
                //팔찌의 세부 카테고리 - 팬던트_참 클릭
                if(category.equals("액세서리") && sub3.getText().toString().equals(acc_sub_sub[4])){
                    showSubCategoryResult(4);
                    cur_category.setText(category + " > " + acc_sub[1] + " > " + acc_sub_sub[4]);
                }
                else{
                    //카테고리가 액세서리가 아닐시
                    showSubCategoryResult(2);
                    cur_category.setText(category + " > " +sub3.getText().toString());
                }
            }
        });

        return rootview;
    }

    //부자재 번호로 부자재 정보 찾기
    public void findMaterialInfo(final String material) {
        final String[] material_each = material.split("#");
        myRef2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int i = 0;
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if ( i == material_each.length)
                        break;
                    if(material_each[i].equals(ds.getKey())){
                        String name = ds.child("material_name").getValue().toString();
                        String price = ds.child("price").getValue().toString();
                        String width = ds.child("size_width").getValue().toString();
                        String height = ds.child("size_height").getValue().toString();
                        String depth = ds.child("size_depth").getValue().toString();
                        String stock = ds.child("stock").getValue().toString();
                        String keyword = ds.child("keyword").getValue().toString();
                        String storename = ds.child("storename").getValue().toString();
                        //이미지 url 가져오기
                        String[] data = new String[(int)ds.child("image_data").getChildrenCount()];
                        int k = 0 ;
                        for(DataSnapshot ds2 : ds.child("image_data").getChildren()){
                            data[k] = ds2.getValue().toString();
                            k++;
                        }
                        //이미지 url의 0번이 상품 대표 이미지
                        String preview = data[0];
                        //리사이클러뷰에 아이템 add
                        addItemToRecyclerView(name, price, preview, data, width, height, depth, keyword, stock, storename, ds.getKey());
                        i++;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    //리사이클러뷰에 제품 이름, 가격, 이미지 url, 가게이름으로 아이템 나타내기
    public void addItemToRecyclerView(String name, String price, String preview, String[] data,
                                      String width, String height, String depth, String keyword, String stock, String storename, String unique){
        Material_Detail_Info item = new Material_Detail_Info(name, price+" 원", preview, data, width, height, depth, keyword, stock, storename, unique);
        category_item.add(item);
        categoryAdapter.notifyDataSetChanged();
    }

    //부자재 정보 번들에 담아서 상품 상세 페이지로 이동
    public void movetoProductDetail(int position) {
        //상품 상세 페이지 정보 가져오기
        String name = category_item.get(position).getName();
        String price = category_item.get(position).getPrice();
        String[] data = category_item.get(position).getImg_data();
        String width = category_item.get(position).getWidth();
        String height = category_item.get(position).getHeight();
        String depth = category_item.get(position).getDepth();
        String keyword = category_item.get(position).getKeyword();
        String stock = category_item.get(position).getStock();
        String storename = category_item.get(position).getStorename();
        String unique_num = category_item.get(position).getUnique_number();

        Fragment tab1 = new Product_Detail_Fragment();

        //번들에 부자재 상세정보 담아서 가게 상세 페이지 프래그먼트로 보내기
        Bundle bundle = new Bundle();
        bundle.putString("name", name);
        bundle.putString("price", price);
        bundle.putStringArray("data", data);
        bundle.putString("width", width);
        bundle.putString("height", height);
        bundle.putString("depth", depth);
        bundle.putString("keyword", keyword);
        bundle.putString("stock", stock);
        bundle.putString("storename", storename);
        bundle.putString("unique_number", unique_num);
        tab1.setArguments(bundle);

        //프래그먼트 카테고리 검색 -> 제품 상세 페이지로 교체
        FragmentManager fm = getActivity().getSupportFragmentManager();
        fm.beginTransaction()
          .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right)
          .replace(R.id.main_tab_view, tab1)
          .hide(HomeSearch_Category.this)
          .addToBackStack(null)
          .commit();
    }

    //버튼 눌림에 따라 세부 카테고리로 필터링한 아이템 보여주기
    public void showSubCategoryResult(int number){
        category_item.clear();
        if(!TextUtils.isEmpty(material[number])) findMaterialInfo(material[number]);
        else categoryAdapter.notifyDataSetChanged();
    }

}
