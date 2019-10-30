package com.example.diy_simulator;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
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

public class HomeSearch_Store extends Fragment {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("판매자");
    DatabaseReference myRef2 = database.getReference("부자재");

    String material = "";
    String seller_name, seller_phone, seller_addr;
    TextView name, phone, addr;

    public RecyclerView search_store_recyclerview;
    private final List<HomeSearch_Store_Info> store_item = new ArrayList<>();
    private final HomeSearch_Store_Adapter storeAdapter = new HomeSearch_Store_Adapter(getContext(), store_item, R.layout.fragment_home_search_store);

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final ViewGroup rootview = (ViewGroup) inflater.inflate(R.layout.fragment_home_search_store, container, false);

        TextView toolbar_name = rootview.findViewById(R.id.search_store_toolbar_title);

        //그리드 레이아웃으로 한줄에 2개씩 제품 보여주기
        search_store_recyclerview = rootview.findViewById(R.id.search_store_recyclerView);
        final GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        search_store_recyclerview.setHasFixedSize(true);
        search_store_recyclerview.setLayoutManager(layoutManager);
        search_store_recyclerview.setAdapter(storeAdapter);

        name = rootview.findViewById(R.id.search_seller_name);
        phone = rootview.findViewById(R.id.search_seller_phone);
        addr = rootview.findViewById(R.id.search_seller_addr);

        //가게 이름을 툴바 타이틀로 지정
        final String storename = getArguments().getString("storename");
        toolbar_name.setText(storename);

        //가게 이름을 통해 판매자 정보, 부자재 정보 가져오기
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                 for (DataSnapshot ds : dataSnapshot.getChildren()) {
                     if (storename.equals(ds.child("storename").getValue().toString())){
                         material = ds.child("material").getValue().toString();
                         seller_name = ds.child("username").getValue().toString();
                         seller_phone = ds.child("phonenumber").getValue().toString();
                         seller_addr = ds.child("address").getValue().toString();
                         break;
                     }
                }
                 name.setText("판매자 : " + seller_name);
                 phone.setText("전화번호 : " + seller_phone);
                 addr.setText("가게 주소 : " + seller_addr);
                 findMaterialInfo(material);
                 Log.d("나와", material + "/" +seller_name +"/"+seller_phone +"/"+seller_addr);
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
        HomeSearch_Store_Info[] item = new HomeSearch_Store_Info[1];
        item[0] = new HomeSearch_Store_Info(name, price+"원", url);
        store_item.add(item[0]);

        storeAdapter.notifyDataSetChanged();
    }

    //다른 탭으로 이동시 프래그먼트 destroy
    @Override
    public void onPause(){
        super.onPause();
        FragmentManager fm = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.remove(HomeSearch_Store.this).commit();
        fm.popBackStack();
    }
}