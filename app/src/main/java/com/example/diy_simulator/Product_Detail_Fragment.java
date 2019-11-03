package com.example.diy_simulator;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
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
        Toolbar tb = rootview.findViewById(R.id.product_detail_toolbar) ;
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

        //뷰에 나타내기
        Glide.with(getContext())
                .load(url[0])
                .into(representive);
        v_name.setText(name);
        v_price.setText(price);
        v_keyword.setText(keyword);
        v_store.setText(store);
        String size = width+" x "+height+" x "+depth;
        v_size.setText(size);

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
            public void onClick(View v) {
                //로그인한 경우
                if (mFirebaseUser != null) {
                    //장바구니에 담으면 구매자 DB에 부자재 번호 추가
                    myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                if(mFirebaseUser.getEmail().equals(ds.child("email").getValue().toString())){
                                    String tmp = ds.child("cart").getValue().toString();
                                    if(TextUtils.isEmpty(tmp)) myRef.child(ds.getKey()).child("cart").setValue(uni_num);
                                    else myRef.child(ds.getKey()).child("cart").setValue(tmp+"#"+uni_num);
                                    Toast.makeText(getContext(), "장바구니에 담겼습니다.", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(getContext(), "로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
                    /*
                    Intent intent = new Intent(getContext(), SignInActivity.class);
                    startActivity(intent);
                    */
                }
            }
        });

        return rootview;
    }
}
