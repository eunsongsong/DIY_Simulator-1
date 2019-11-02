package com.example.diy_simulator;

import android.os.Bundle;
import android.util.Log;
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
import androidx.fragment.app.FragmentTransaction;
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
    String[] material = new String[3];

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef2 = database.getReference("부자재");

    public RecyclerView search_category_recyclerview;
    private final List<HomeSearch_Category_Info> category_item = new ArrayList<>();
    private final HomeSearch_Category_Adapter categoryAdapter = new HomeSearch_Category_Adapter(getContext(),
            category_item, R.layout.fragment_home_search_category);

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final ViewGroup rootview = (ViewGroup) inflater.inflate(R.layout.fragment_home_search_category, container, false);

        TextView title = rootview.findViewById(R.id.search_category_toolbar_title);
        final Button sub1 = rootview.findViewById(R.id.category_detail_btn1);
        final Button sub2 = rootview.findViewById(R.id.category_detail_btn2);
        final Button sub3 = rootview.findViewById(R.id.category_detail_btn3);

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
        category = getArguments().getString("category");
        //데이터베이스 참조를 해당 카테고리로 지정
        DatabaseReference myRef = database.getReference("카테고리").child(category);
        //카테고리를 툴바 타이틀로 지정
        title.setText(category);

        //세부 카테고리와 부자재 번호 찾아오기
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            int i =0;
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    //세부 카테고리 버튼 텍스트 설정
                    if(i==0) {
                        material[0] = ds.getValue().toString();
                        sub1.setText(ds.getKey());
                        findMaterialInfo(material[0]);
                        i++;
                    }
                    else if(i==1){
                        material[1] = ds.getValue().toString();
                        sub2.setText(ds.getKey());
                        findMaterialInfo(material[1]);
                        i++;
                    }
                    else{
                        material[2] = ds.getValue().toString();
                        sub3.setText(ds.getKey());
                        findMaterialInfo(material[2]);
                        i++;
                    }
                }
                //세부 카테고리가 2개인 경우 세번째 버튼 안보이게 설정
                if(i==2) sub3.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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
                        String url = ds.child("image_url").child(ds.getKey()).getValue().toString();
                        addItemToRecyclerView(name, price, url);
                        i++;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    //리사이클러뷰에 제품 이름, 가격, 이미지 url으로 아이템 나타내기
    public void addItemToRecyclerView(String name, String price, String url){
        HomeSearch_Category_Info item = new HomeSearch_Category_Info(name, price+"원", url, "테스트");
        category_item.add(item);
        categoryAdapter.notifyDataSetChanged();
    }

    //다른 탭으로 이동시 프래그먼트 destroy
    @Override
    public void onPause(){
        super.onPause();
        FragmentManager fm = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.remove(HomeSearch_Category.this).commit();
        fm.popBackStack();
    }

}
