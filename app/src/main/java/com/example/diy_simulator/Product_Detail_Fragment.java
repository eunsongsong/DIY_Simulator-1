package com.example.diy_simulator;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Product_Detail_Fragment extends Fragment {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("구매자");

    public RecyclerView detail_recyclerview;
    private final List<Product_Detail_Info> detail_item = new ArrayList<>();
    private final Product_Detail_Adapter detailAdapter = new Product_Detail_Adapter(getContext(),
            detail_item, R.layout.product_detail_fragment);

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final ViewGroup rootview = (ViewGroup) inflater.inflate(R.layout.product_detail_fragment, container, false);

        Button cart_btn = rootview.findViewById(R.id.detail_cart_btn);

        //리사이클러뷰 레이아웃 매니저 설정 - vertical
        detail_recyclerview = rootview.findViewById(R.id.product_detail_recyclerView);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        detail_recyclerview.setHasFixedSize(true);
        detail_recyclerview.setLayoutManager(layoutManager);
        detail_recyclerview.setAdapter(detailAdapter);

        //툴바 뒤로가기 버튼 설정
        final Toolbar tb = rootview.findViewById(R.id.product_detail_toolbar) ;
        ((AppCompatActivity) getActivity()).setSupportActionBar(tb) ;
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);

        ImageView representive = rootview.findViewById(R.id.detail_represent_image);
        TextView v_store = rootview.findViewById(R.id.detail_store_name);
        TextView v_name = rootview.findViewById(R.id.detail_product_name);
        TextView v_price = rootview.findViewById(R.id.detail_product_price);
        TextView v_keyword = rootview.findViewById(R.id.detail_product_keyword);
        TextView v_size = rootview.findViewById(R.id.detail_product_size);
        TextView v_category = rootview.findViewById(R.id.detail_product_category);

        //부자재 세부 정보 가져오기
        String name = getArguments().getString("name");
        String price = getArguments().getString("price");
        String[] url = getArguments().getStringArray("url");
        String width = getArguments().getString("width");
        String height = getArguments().getString("height");
        String depth = getArguments().getString("depth");
        String keyword = getArguments().getString("keyword");
        String stock = getArguments().getString("stock");
        String store = getArguments().getString("storename");
        final String uni_num = getArguments().getString("unique_number");
        String category = getArguments().getString("category");

        //뷰에 나타내기
        RequestOptions requestOptions = new RequestOptions();
        requestOptions = requestOptions.placeholder(R.drawable.mungmung);
        Glide.with(getContext())
                .setDefaultRequestOptions(requestOptions)
                .load(url[0])
                .into(representive);

        v_name.setText(name);
        v_price.setText(price);
        v_keyword.setText(keyword);
        v_store.setText(store);
        String size = width+" x "+height+" x "+depth;
        v_size.setText(size);

        //카테고리 나타내기
        if(!TextUtils.isEmpty(category)) {
            String[] category_detail = category.split("#");
            category = "";
            for (String s : category_detail) {
                category = category + s + "\n";
            }
        }
        v_category.setText(category);

        //대표 이미지 제외 나머지 이미지 리사이클러뷰에 나타내기
        Product_Detail_Info[] item = new Product_Detail_Info[url.length-1];
        for(int i=0; i < url.length-1; i++){
            item[i] = new Product_Detail_Info(url[i+1]);
            Log.d("아이템",url[i+1]+"");
            detail_item.add(item[i]);
        }
        detailAdapter.notifyDataSetChanged();

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        final FirebaseUser mFirebaseUser = firebaseAuth.getCurrentUser();

        //장바구니 버튼을 눌렀을 때
        cart_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                //로그인한 경우
                if (mFirebaseUser != null) {
                    //장바구니에 담으면 구매자 DB에 부자재 번호 추가
                    myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                //메인 탭 액티비티 변수 isSeller 가져오기
                                boolean isSeller = PreferenceUtil.getInstance(getContext()).getBooleanExtra("isSeller");
                                //break; 판매자일 경우 장바구니에 담을 수 없음
                                if(isSeller) break;

                                if(mFirebaseUser.getEmail().equals(ds.child("email").getValue().toString())){
                                    //현재 유저의 장바구니 값 받아오기
                                    String cart_num = String.valueOf(ds.child("cart").getValue());
                                    //장바구니 숫자 정렬
                                    if(TextUtils.isEmpty(cart_num)) cart_num = uni_num;
                                    else cart_num = sortMaterialNumber(cart_num+"#"+uni_num);
                                    //토스트 메세지 설정
                                    View view = View.inflate(getContext(), R.layout.custom_cart_add_toast_message, null);
                                    Toast toast = new Toast(getContext());
                                    toast.setView(view);
                                    toast.setGravity(Gravity.CENTER,0,0);
                                    toast.setDuration(Toast.LENGTH_SHORT);
                                    //디비에 정렬된 장바구니 값 저장
                                    myRef.child(ds.getKey()).child("cart").setValue(cart_num);
                                    toast.show();
                                    break;
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
                //로그인 하지 않은 경우
                else{
                    //다이얼로그를 띄워 로그인 의사 묻기
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("로그인이 필요한 서비스입니다.").setMessage("로그인하시겠습니까?");
                    builder.setPositiveButton("네", new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            Intent intent = new Intent(getContext(), SignInActivity.class);
                            startActivity(intent);
                        }
                    });

                    builder.setNegativeButton("아니오", new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            }
        });

        return rootview;
    }

    //장바구니에 담겨있던 숫자 오름차순 정렬하는 함수
    public String sortMaterialNumber(String number){
        Log.i("카트 번호 디테일 페이지", number);
        String result = "";
        // 0#6#2#10 ... 로 되어있는 스트링을 배열로 쪼갠다
        String[] s_num = number.split("#");
        // 카트에 중복된 아이템인지 확인 - 이미 카트에 담긴 아이템은 중복 담기 없음
        boolean exist = false;
        for(int i=0; i<s_num.length-1; i++){
            if(s_num[s_num.length-1].equals(s_num[i])){
                exist = true;
            }
        }
        // 이미 카트에 있던 아이템일 경우 (중복 O)
        if(exist){
            result = number.substring(0, number.length()-2);
        }
        // 카트에 없던 아이템일 경우 (중복 X)
        else{
            // 쪼갠 스트링 배열만큼 인티저 배열을 생성한다
            int[] i_num = new int[s_num.length];
            // 스트링을 인티저 배열로 옮긴 후 정렬한다
            for(int i=0; i<s_num.length; i++){
                i_num[i] = Integer.parseInt(s_num[i]);
            }
            Arrays.sort(i_num);
            // 정렬된 배열에 #을 붙여서 다시 스트링으로 리턴한다
            for(int i=0; i<i_num.length; i++){
                if(i == i_num.length-1) result = result + i_num[i];
                else result = result + i_num[i] + "#";
            }
        }
        return  result;
    }
}
