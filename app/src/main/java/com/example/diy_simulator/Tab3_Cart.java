package com.example.diy_simulator;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
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
import java.util.Collections;
import java.util.List;

public class Tab3_Cart extends Fragment {

    private ProgressDialog pd;

    FirebaseAuth firebaseAuth;
    FirebaseUser mFirebaseUser;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("구매자");
    DatabaseReference myRef_material = database.getReference("부자재");
    DatabaseReference myRef_seller = database.getReference("판매자");

    private String cart = "";
    private ImageView empty;
    TextView money, delivery_sum, guide, guide_title;
    Button pay_btn;
    private ArrayList<String> store_names = new ArrayList<>();
    String[] delivery_fee;
    int sum_of_money = 0;
    int sum_of_delivery_fee = 0;

    public RecyclerView cart_recyclerview;
    private List<Tab3_Cart_Info> cart_item = new ArrayList<>();
    private List<Tab3_Cart_In_Item_Info> in_item = new ArrayList<>();
    private final Tab3_Cart_Adapter cartAdapter = new Tab3_Cart_Adapter(getContext(), cart_item, R.layout.tab3_cart_item, Tab3_Cart.this);

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final ViewGroup rootview = (ViewGroup) inflater.inflate(R.layout.fragment_tab3_cart, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = firebaseAuth.getCurrentUser();

        empty = rootview.findViewById(R.id.empty_cart_img);
        money = rootview.findViewById(R.id.cart_sum_money);
        delivery_sum = rootview.findViewById(R.id.cart_sum_delivery_fee);
        guide = rootview.findViewById(R.id.cart_use_guide_txt);
        guide_title = rootview.findViewById(R.id.cart_use_guide_title);
        pay_btn = rootview.findViewById(R.id.cart_pay_button);

        //리사이클러뷰 리니어 레이아웃 매니저 설정 - vertical
        cart_recyclerview = rootview.findViewById(R.id.cart_recyclerView);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        cart_recyclerview.setHasFixedSize(true);
        cart_recyclerview.setLayoutManager(layoutManager);
        cart_recyclerview.setAdapter(cartAdapter);

        //로그인이 되어있으면 장바구니 불러오기
        if(mFirebaseUser != null) getCartInfo();

        // 상품 이미지 누르면 상품 상세 정보 페이지로 이동
        cartAdapter.setOnItemClickListener(new Tab3_Cart_Adapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int outer_position, int inner_position) {
                movetoProductDetail(outer_position, inner_position);
            }
        });

        pay_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), OrderActivity.class);
                ArrayList<Tab3_Cart_Info> orderinfo_arrayList = new ArrayList<>();
                String unique_num = "";
                String amount = "";

                for(int a = 0 ; a < cart_item.size(); a++)
                {
                    ArrayList<Tab3_Cart_In_Item_Info> order_in_info = new ArrayList<>();
                    for(int b = 0 ; b  < cart_item.get(a).getIn_items().size() ; b++)
                    {
                        if (cart_item.get(a).getIn_items().get(b).getCheckBox()) {
                            Tab3_Cart_In_Item_Info tab3_cart_in_item_info = cart_item.get(a).getIn_items().get(b);
                            unique_num = unique_num + cart_item.get(a).getIn_items().get(b).getUnique_number() + "#";
                            amount = amount + cart_item.get(a).getIn_items().get(b).getAmount() + "#";
                            order_in_info.add(tab3_cart_in_item_info);
                        }
                    }
                    if ( order_in_info.size() != 0)
                        orderinfo_arrayList.add(new Tab3_Cart_Info(cart_item.get(a).getStorename(), cart_item.get(a).getDelivery_fee(),order_in_info));
                }

                intent.putExtra("order_Info",  orderinfo_arrayList);
                intent.putExtra("order_material_number", unique_num);
                intent.putExtra("order_material_amount", amount);
                startActivity(intent);
            }
        });

        return rootview;
    }

    //구매자 아이디(이메일)를 통해 장바구니에 담긴 부자재 번호 가져오기
    public void getCartInfo(){
        showProgress();

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (mFirebaseUser.getEmail().equals(ds.child("email").getValue().toString())){
                        cart = ds.child("cart").getValue().toString();
                        Log.i("카트 번호", cart+"  dl");
                        break;
                    }
                }
                //카트가 null 이 아닐경우 카트 아이템 불러오기
                if(!TextUtils.isEmpty(cart)) {
                    empty.setVisibility(View.GONE);
                    findMaterialInfo(cart);
                }
                else{
                    guide_title.setVisibility(View.VISIBLE);
                    guide.setVisibility(View.VISIBLE);
                    hideProgress();
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

        myRef_material.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int i = 0;
                int m = 0;
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
                        String category = ds.child("category").getValue().toString();
                        //이미지 url 가져오기
                        String[] url = new String[(int) ds.child("image_url").getChildrenCount()];
                        int k = 0;
                        for (DataSnapshot ds2 : ds.child("image_url").getChildren()) {
                            url[k] = ds2.getValue().toString();
                            k++;
                        }
                        //이미지 url의 0번이 상품 대표 이미지
                        String preview = url[0];
                        Tab3_Cart_In_Item_Info in_item_info = new Tab3_Cart_In_Item_Info(name, price, preview, url, width, height, depth,
                                keyword, stock, storename, ds.getKey(), category, 1);
                        if(!store_names.contains(storename)){
                            Log.i("저장되는 스토어 네임", storename);
                            store_names.add(storename);
                            m++;
                        }
                        in_item.add(in_item_info);
                        i++;
                    }
                }
                findDeliveryFee(store_names);
                //setSum_of_money();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    // 장바구니 아이템 상점에 따라 나누기
    public List<Tab3_Cart_In_Item_Info> classifyInItems(String store_name, List<Tab3_Cart_In_Item_Info> in_items){
        List<Tab3_Cart_In_Item_Info> result_items = new ArrayList<>();
        if(in_items.size() <= 1) return in_items;
        else {
            for(int i=0; i<in_items.size(); i++){
                if(in_items.get(i).getStorename().equals(store_name)){
                    result_items.add(in_items.get(i));
                }
            }
            return result_items;
        }
    }

    // 판매자 DB에서 배송비 찾기
    public void findDeliveryFee(final ArrayList<String> storeNames){
        Collections.sort(storeNames);
        delivery_fee = new String[storeNames.size()];

        myRef_seller.orderByChild("storename").addListenerForSingleValueEvent(new ValueEventListener() {
            int i = 0;
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    if(i == storeNames.size()) break;
                    if(ds.child("storename").getValue().toString().equals(storeNames.get(i))){
                        delivery_fee[i] = ds.child("delivery_fee").getValue().toString();
                        Log.i("되니?", delivery_fee[i]+ " 원");
                        i++;
                    }
                }
                addItemToRecyclerView(in_item);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    //리사이클러뷰에 제품 이름, 가격, 가게이름, 이미지 url으로 아이템 나타내기
    public void addItemToRecyclerView(List<Tab3_Cart_In_Item_Info> in_items){
        if(in_item.size() == 1) {
            Tab3_Cart_Info item = new Tab3_Cart_Info(in_item.get(0).getStorename(), delivery_fee[0], in_item);
            cart_item.add(item);
        }
        else{
            for(int i=0; i<store_names.size(); i++){
                Log.i("찾는 스토어 네임", store_names.get(i));

                List<Tab3_Cart_In_Item_Info> list = classifyInItems(store_names.get(i), in_items);
                Tab3_Cart_Info item = new Tab3_Cart_Info(store_names.get(i), delivery_fee[i], list);
                cart_item.add(item);
            }
        }
        setSum_of_money();
        hideProgress();
        guide_title.setVisibility(View.VISIBLE);
        guide.setVisibility(View.VISIBLE);
        cartAdapter.notifyDataSetChanged();
    }


    // 주문 금액 총합 세팅하는 함수
    public void setSum_of_money() {
        sum_of_money = 0;
        // 장바구니 모든 아이템의 가격 x 수량을 해서 더함
        for (int i = 0; i < cart_item.size(); i++) {
            for(int k = 0; k < cart_item.get(i).getIn_items().size(); k++){
                if(cart_item.get(i).getIn_items().get(k).getCheckBox()){
                    int amount = cart_item.get(i).getIn_items().get(k).getAmount();
                    int price = Integer.parseInt( cart_item.get(i).getIn_items().get(k).getPrice());
                    sum_of_money = sum_of_money + amount * price;
                }
            }
        }
        Log.i("주문 금액 합   ", sum_of_money + "");
        sum_of_delivery_fee = 0;
        for(int j=0; j<cart_item.size(); j++){
            if(cart_item.get(j).getAnySelected()){
                sum_of_delivery_fee = sum_of_delivery_fee + Integer.parseInt(cart_item.get(j).getDelivery_fee());
            }
        }
        String str = "총 배송비 : " + sum_of_delivery_fee + " 원";
        String str2 = "총 주문 금액 : " + (sum_of_money) + " 원";
        money.setText(str2);  //텍스트뷰 설정
        delivery_sum.setText(str);
        String str3 = "구매하기 (￦" + (sum_of_money + sum_of_delivery_fee) +")";
        pay_btn.setText(str3);
    }

    //부자재 정보 번들에 담아서 상품 상세 페이지로 이동
    public void movetoProductDetail(int outer_pos, int inner_pos){
        //상품 상세 페이지 정보 가져오기
        String name = cart_item.get(outer_pos).getIn_items().get(inner_pos).getName();
        String price = cart_item.get(outer_pos).getIn_items().get(inner_pos).getPrice();
        String[] url = cart_item.get(outer_pos).getIn_items().get(inner_pos).getImg_url();
        String width = cart_item.get(outer_pos).getIn_items().get(inner_pos).getWidth();
        String height = cart_item.get(outer_pos).getIn_items().get(inner_pos).getHeight();
        String depth = cart_item.get(outer_pos).getIn_items().get(inner_pos).getDepth();
        String keyword = cart_item.get(outer_pos).getIn_items().get(inner_pos).getKeyword();
        String stock = cart_item.get(outer_pos).getIn_items().get(inner_pos).getStock();
        String storename = cart_item.get(outer_pos).getIn_items().get(inner_pos).getStorename();
        String unique_num = cart_item.get(outer_pos).getIn_items().get(inner_pos).getUnique_number();
        String category = cart_item.get(outer_pos).getIn_items().get(inner_pos).getCategory();

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
        bundle.putString("category", category);
        tab3.setArguments(bundle);

        //프래그먼트 tab3 내 가게 -> 제품 상세 페이지로 교체
        FragmentManager fm = getActivity().getSupportFragmentManager();
        fm.beginTransaction()
                .setCustomAnimations(R.anim.enter_from_right, R.anim.fade_out)
                .replace(R.id.main_tab_view, tab3)
                .hide(Tab3_Cart.this)
                .addToBackStack(null)
                .commit();
    }

    // 고객이 장바구니에 담긴 물건 모두 삭제했을 때 빈 이미지 띄워줌
    public void isEmptyCart(){
        empty.setVisibility(View.VISIBLE);
    }


    // 프로그레스 다이얼로그 보이기
    public void showProgress() {
        if( pd == null ) { // 객체를 1회만 생성한다
            pd = new ProgressDialog(getContext(), R.style.NewDialog); // 생성한다.
            pd.setCancelable(false); // 백키로 닫는 기능을 제거한다.
            Log.d("ㅇㅇ","dnfka");
        }
        pd.show(); // 화면에 띠워라//
    }
    public void hideProgress(){
        if( pd != null && pd.isShowing() ){
            pd.dismiss();
        }
    }


}
