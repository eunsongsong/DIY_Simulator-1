package com.example.diy_simulator;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

public class HomeSearch_Store extends Fragment {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("판매자");
    DatabaseReference myRef2 = database.getReference("부자재");

    String material = "";
    String seller_name, seller_phone, seller_addr;
    TextView name, phone, addr;

    private ImageView no_item_img;
    public RecyclerView search_store_recyclerview;
    private final List<Material_Detail_Info> store_item = new ArrayList<>();
    private final HomeSearch_Store_Adapter storeAdapter = new HomeSearch_Store_Adapter(getContext(), store_item, R.layout.fragment_home_search_store);

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final ViewGroup rootview = (ViewGroup) inflater.inflate(R.layout.fragment_home_search_store, container, false);

        TextView toolbar_name = rootview.findViewById(R.id.search_store_toolbar_title);

        //툴바 뒤로가기 버튼 설정
        Toolbar tb = rootview.findViewById(R.id.search_store_toolbar) ;
        ((AppCompatActivity) getActivity()).setSupportActionBar(tb) ;
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);

        //그리드 레이아웃으로 한줄에 2개씩 제품 보여주기
        search_store_recyclerview = rootview.findViewById(R.id.search_store_recyclerView);
        no_item_img = rootview.findViewById(R.id.store_product_ready_img);
        final GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        search_store_recyclerview.setHasFixedSize(true);
        search_store_recyclerview.setLayoutManager(layoutManager);
        search_store_recyclerview.setAdapter(storeAdapter);

        store_item.clear();

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
                 if(!TextUtils.isEmpty(material)){
                     search_store_recyclerview.setVisibility(View.VISIBLE);
                     no_item_img.setVisibility(View.GONE);
                     findMaterialInfo(material);
                 }
                 else
                 {
                     no_item_img.setVisibility(View.VISIBLE);
                     return;
                 }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //아이템 클릭시 상품 상세 페이지로 이동
        storeAdapter.setOnItemClickListener(new HomeSearch_Store_Adapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                movetoProductDetail(position);
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
                    if(ds.child("image_url").getChildrenCount() == 0 )
                    {

                    }
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
                        String[] url = new String[(int)ds.child("image_url").getChildrenCount()];
                        int k = 0 ;
                        for(DataSnapshot ds2 : ds.child("image_url").getChildren()){
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
    public void addItemToRecyclerView(String name, String price, String preview, String[] url,
                                      String width, String height, String depth, String keyword, String stock, String storename, String unique){
        Material_Detail_Info item = new Material_Detail_Info(name, price+" 원", preview, url, width, height, depth, keyword, stock, storename, unique);
        store_item.add(item);
        storeAdapter.notifyDataSetChanged();
    }

    //부자재 정보 번들에 담아서 상품 상세 페이지로 이동
    public void movetoProductDetail(int position){
        //상품 상세 페이지 정보 가져오기
        String name = store_item.get(position).getName();
        String price = store_item.get(position).getPrice();
        String[] url = store_item.get(position).getImg_url();
        String width = store_item.get(position).getWidth();
        String height = store_item.get(position).getHeight();
        String depth = store_item.get(position).getDepth();
        String keyword = store_item.get(position).getKeyword();
        String stock = store_item.get(position).getStock();
        String storename = store_item.get(position).getStorename();
        String unique_num = store_item.get(position).getUnique_number();

        Fragment tab1 = new Product_Detail_Fragment();

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
        tab1.setArguments(bundle);

        //프래그먼트 카테고리 검색 -> 제품 상세 페이지로 교체
        FragmentManager fm = getActivity().getSupportFragmentManager();
        fm.beginTransaction()
                .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right)
                .replace(R.id.main_tab_view, tab1)
                .hide(HomeSearch_Store.this)
                .addToBackStack(null)
                .commit();
    }
}