package com.example.diy_simulator;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
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

public class Tab3_Cart extends Fragment {

    FirebaseAuth firebaseAuth;
    FirebaseUser mFirebaseUser;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("구매자");
    DatabaseReference myRef2 = database.getReference("부자재");

    String cart = "";
    ImageView empty;

    public RecyclerView cart_recyclerview;
    private final List<Material_Detail_Info> cart_item = new ArrayList<>();
    private final Tab3_Cart_Adapter cartAdapter = new Tab3_Cart_Adapter(getContext(), cart_item, R.layout.tab3_cart_item);

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final ViewGroup rootview = (ViewGroup) inflater.inflate(R.layout.fragment_tab3_cart, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = firebaseAuth.getCurrentUser();

        empty = rootview.findViewById(R.id.empty_cart_img);

        //리사이클러뷰 리니어 레이아웃 매니저 설정 - vertical
        cart_recyclerview = rootview.findViewById(R.id.cart_recyclerView);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        cart_recyclerview.setHasFixedSize(true);
        cart_recyclerview.setLayoutManager(layoutManager);
        cart_recyclerview.setAdapter(cartAdapter);

        //로그인이 되어있으면 장바구니 불러오기
        if(mFirebaseUser != null) getCartInfo();

        return rootview;
    }

    //구매자 아이디(이메일)를 통해 장바구니에 담긴 부자재 번호 가져오기
    public void getCartInfo(){
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (mFirebaseUser.getEmail().equals(ds.child("email").getValue().toString())){
                        cart = ds.child("cart").getValue().toString();
                        break;
                    }
                }
                //카트가 null 이 아닐경우 카트 아이템 불러오기
                if(!TextUtils.isEmpty(cart)) {
                    empty.setVisibility(View.GONE);
                    findMaterialInfo(cart);
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
                        String[] data = new String[(int) ds.child("image_data").getChildrenCount()];
                        int k = 0;
                        for (DataSnapshot ds2 : ds.child("image_data").getChildren()) {
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

    //리사이클러뷰에 제품 이름, 가격, 가게이름, 이미지 url으로 아이템 나타내기
    public void addItemToRecyclerView(String name, String price, String preview, String[] data,
                                      String width, String height, String depth, String keyword, String stock, String storename, String unique){
        Material_Detail_Info item = new Material_Detail_Info(name, price+" 원", preview, data, width, height, depth, keyword, stock, storename, unique);
        cart_item.add(item);

        cartAdapter.notifyDataSetChanged();
    }

    public void isEmptyCart(){
        empty.setVisibility(View.VISIBLE);
    }

}
