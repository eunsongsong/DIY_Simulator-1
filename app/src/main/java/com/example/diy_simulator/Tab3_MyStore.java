package com.example.diy_simulator;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
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

    private ImageView no_item_img;

    public RecyclerView mystore_recyclerview;
    private final List<Material_Detail_Info> mystore_item = new ArrayList<>();
    private final Tab3_MyStore_Adater mystoreAdapter = new Tab3_MyStore_Adater(getContext(), mystore_item, R.layout.tab3_my_store_item);

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final ViewGroup rootview = (ViewGroup) inflater.inflate(R.layout.fragment_tab3_my_store, container, false);

        Button upload_btn = rootview.findViewById(R.id.image_upload_btn_tab3);
        mystore_title = rootview.findViewById(R.id.mystore_toolbar_title);
        no_item_img = rootview.findViewById(R.id.mystore_product_ready_img);
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

        //아이템 클릭시 상품 상세 페이지로 이동
        mystoreAdapter.setOnItemClickListener(new Tab3_MyStore_Adater.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                movetoProductDetail(position);
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
                        //툴바 타이틀을 판매자의 가게 이름으로 설정
                        mystore_title.setText(name);
                        break;
                    }
                }
                if(!TextUtils.isEmpty(material)) {
                    mystore_recyclerview.setVisibility(View.VISIBLE);
                    no_item_img.setVisibility(View.GONE);
                    findMaterialInfo(material);
                }
                else
                {
                    no_item_img.setVisibility(View.VISIBLE);
                    mystore_recyclerview.setVisibility(View.GONE);
                }

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
                    if (i == material_each.length)
                        break;
                    if (material_each[i].equals(ds.getKey())) {
                        String name = ds.child("material_name").getValue().toString();
                        String price = ds.child("price").getValue().toString();
                        String width = ds.child("size_width").getValue().toString();
                        String height = ds.child("size_height").getValue().toString();
                        String depth = ds.child("size_depth").getValue().toString();
                        String stock = ds.child("stock").getValue().toString();
                        String keyword = ds.child("keyword").getValue().toString();
                        String storename = ds.child("storename").getValue().toString();
                        //이미지 url 가져오기
                        String[] url = new String[(int) ds.child("image_url").getChildrenCount()];
                        int k = 0;
                        for (DataSnapshot ds2 : ds.child("image_url").getChildren()) {
                            url[k] = ds2.getValue().toString();
                            k++;
                        }
                        //이미지 url의 0번이 상품 대표 이미지
                        String preview = url[0];
                        //리사이클러뷰에 아이템 add
                        addItemToRecyclerView(name, price, preview, url, width, height, depth, keyword, stock, storename, ds.getKey());
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
    public void addItemToRecyclerView(String name, String price, String preview, String[] data,
                                      String width, String height, String depth, String keyword, String stock, String storename, String unique){
        Material_Detail_Info item = new Material_Detail_Info(name, price+" 원", preview, data, width, height, depth, keyword, stock, storename, unique);
        mystore_item.add(item);

        mystoreAdapter.notifyDataSetChanged();
    }

    //부자재 정보 번들에 담아서 상품 상세 페이지로 이동
    public void movetoProductDetail(int position){
        //상품 상세 페이지 정보 가져오기
        String name = mystore_item.get(position).getName();
        String price = mystore_item.get(position).getPrice();
        String[] url = mystore_item.get(position).getImg_url();
        String width = mystore_item.get(position).getWidth();
        String height = mystore_item.get(position).getHeight();
        String depth = mystore_item.get(position).getDepth();
        String keyword = mystore_item.get(position).getKeyword();
        String stock = mystore_item.get(position).getStock();
        String storename = mystore_item.get(position).getStorename();
        String unique_num = mystore_item.get(position).getUnique_number();

        Fragment tab3 = new Product_Detail_Fragment();

        //번들에 부자재 상세정보 담아서 가게 상세 페이지 프래그먼트로 보내기
        Bundle bundle = new Bundle();
        bundle.putString("name", name);
        bundle.putString("price", price);
        bundle.putStringArray("url", url);
        bundle.putString("width", width);
        bundle.putString("height", height);
        bundle.putString("depth", depth);
        bundle.putString("keyword", keyword);
        bundle.putString("stock", stock);
        bundle.putString("storename", storename);
        bundle.putString("unique_number", unique_num);
        tab3.setArguments(bundle);

        //프래그먼트 tab3 내 가게 -> 제품 상세 페이지로 교체
        FragmentManager fm = getActivity().getSupportFragmentManager();
        fm.beginTransaction()
                .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right)
                .replace(R.id.main_tab_view, tab3)
                .hide(Tab3_MyStore.this)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onResume() {
        super.onResume();

        findSellerOwnMaterial();
        //이미지 업로드를 완료하고 다시 MyStore 프래그먼트로 돌아오면 다시 판매자 부자재 검색
    }

}