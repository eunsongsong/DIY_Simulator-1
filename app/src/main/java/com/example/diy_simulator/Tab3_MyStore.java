package com.example.diy_simulator;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Tab3_MyStore extends Fragment {

    FirebaseAuth firebaseAuth;
    FirebaseUser mFirebaseUser;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("판매자");
    DatabaseReference myRef2 = database.getReference("부자재");

    String material = "";
    TextView mystore_title;

    public RecyclerView mystore_recyclerview;
    private final List<Tab3_MyStore_Info> mystore_item = new ArrayList<>();
    private final Tab3_MyStore_Adater mystoreAdapter = new Tab3_MyStore_Adater(getContext(), mystore_item, R.layout.tab3_my_store_item);

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final ViewGroup rootview = (ViewGroup) inflater.inflate(R.layout.fragment_tab3_my_store, container, false);

        Button upload_btn = rootview.findViewById(R.id.image_upload_btn_tab3);
        mystore_title = rootview.findViewById(R.id.mystore_toolbar_title);
        firebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = firebaseAuth.getCurrentUser();

        //그리드 레이아웃으로 한줄에 2개씩 제품 보여주기
        mystore_recyclerview = rootview.findViewById(R.id.mystore_recyclerView);
        final GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        mystore_recyclerview.setHasFixedSize(true);
        mystore_recyclerview.setLayoutManager(layoutManager);
        mystore_recyclerview.setAdapter(mystoreAdapter);

        upload_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //이미지 업로드 액티비티로 전환
                Intent mainIntent = new Intent(getContext(), ImageUploadActivity.class);
                startActivity(mainIntent);
            }
        });
        return rootview;
    }

    //판매자 아이디(이메일)를 통해 판매자가 올린 부자재 정보 가져오기
    public void findSellerOwnMaterial(){
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (mFirebaseUser.getEmail().equals(ds.child("email").getValue().toString())){
                        material = ds.child("material").getValue().toString();
                        String name = ds.child("storename").getValue().toString();
                        mystore_title.setText(name);
                        break;
                    }
                }
                findMaterialInfo(material);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    //부자재 번호로 부자재 정보 찾기
    public void findMaterialInfo(final String material) {
        final String[] material_each = material.split("#");

        mystore_item.clear();

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
        Tab3_MyStore_Info item = new Tab3_MyStore_Info(name, price+"원", url);
        mystore_item.add(item);

        mystoreAdapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
        if ( PreferenceUtil.getInstance(getContext()).getBooleanExtra("금지" )) {
            Log.d("아니","모야");
            Handler delayHandler = new Handler();
            delayHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // TODO
                    findSellerOwnMaterial();
                    PreferenceUtil.getInstance(getContext()).removePreference("금지" );
                }
            }, 3000);
        }
        else {
            Log.d("아니","모야213123");
            findSellerOwnMaterial();
        }

        //이미지 업로드를 완료하고 다시 MyStore 프래그먼트로 돌아오면 다시 판매자 부자재 검색

    }
}