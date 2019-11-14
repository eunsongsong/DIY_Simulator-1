package com.example.diy_simulator;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
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

    private String cart = "";
    private ImageView empty;
    private TextView money;
    int sum_of_money;

    public RecyclerView cart_recyclerview;
    private final List<Tab3_Cart_Info> cart_item = new ArrayList<>();
    private final Tab3_Cart_Adapter cartAdapter = new Tab3_Cart_Adapter(getContext(), cart_item, R.layout.tab3_cart_item, Tab3_Cart.this);

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final ViewGroup rootview = (ViewGroup) inflater.inflate(R.layout.fragment_tab3_cart, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = firebaseAuth.getCurrentUser();

        empty = rootview.findViewById(R.id.empty_cart_img);
        money = rootview.findViewById(R.id.cart_sum_money);

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
                setSum_of_money();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //리사이클러뷰에 제품 이름, 가격, 가게이름, 이미지 url으로 아이템 나타내기
    public void addItemToRecyclerView(String name, String price, String preview, String[] url,
                                      String width, String height, String depth, String keyword, String stock, String storename, String unique){
        Tab3_Cart_Info item = new Tab3_Cart_Info(name, price, preview, url, width, height, depth, keyword, stock, storename, unique, 1);
        cart_item.add(item);

        cartAdapter.notifyDataSetChanged();
    }

    // 주문 금액 총합 세팅하는 함수
    public void setSum_of_money() {
        sum_of_money = 0;
        // 장바구니 모든 아이템의 가격 x 수량을 해서 더함
        for (int i = 0; i < cart_item.size(); i++) {
            int amount = cart_item.get(i).getAmount();
            int price = Integer.parseInt(cart_item.get(i).getPrice());
            sum_of_money = sum_of_money + amount * price;
        }
        String str = "총 주문 금액 : " + sum_of_money + " 원";
        money.setText(str);  //텍스트뷰 설정
    }

    //부자재 정보 번들에 담아서 상품 상세 페이지로 이동
    public void movetoProductDetail(int position){
        //상품 상세 페이지 정보 가져오기
        String name = cart_item.get(position).getName();
        String price = cart_item.get(position).getPrice();
        String[] url = cart_item.get(position).getImg_url();
        String width = cart_item.get(position).getWidth();
        String height = cart_item.get(position).getHeight();
        String depth = cart_item.get(position).getDepth();
        String keyword = cart_item.get(position).getKeyword();
        String stock = cart_item.get(position).getStock();
        String storename = cart_item.get(position).getStorename();
        String unique_num = cart_item.get(position).getUnique_number();

        Fragment tab3 = new Product_Detail_Fragment();

        //번들에 부자재 상세정보 담아서 가게 상세 페이지 프래그먼트로 보내기
        Bundle bundle = new Bundle();
        bundle.putString("name", name);
        bundle.putString("price", price + " 원");
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
                .hide(Tab3_Cart.this)
                .addToBackStack(null)
                .commit();
    }

    // 고객이 장바구니에 담긴 물건 모두 삭제했을 때 빈 이미지 띄워줌
    public void isEmptyCart(){
        empty.setVisibility(View.VISIBLE);
    }

}
