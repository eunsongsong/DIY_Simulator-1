package com.example.diy_simulator;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
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

public class HomeSearch_Keyword extends Fragment {

    ArrayList<String> items = new ArrayList<>();
    TextView non_result;
    public RecyclerView search_keyword_recyclerview;
    private final List<Material_Detail_Info> keyword_item = new ArrayList<>();
    private final HomeSearch_Keyword_Adapter keywordAdapter = new HomeSearch_Keyword_Adapter(getContext(),
            keyword_item, R.layout.fragment_home_search_keyword, HomeSearch_Keyword.this);

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("부자재");

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final ViewGroup rootview = (ViewGroup) inflater.inflate(R.layout.fragment_home_search_keyword, container, false);

        final EditText editText = rootview.findViewById(R.id.search_keyword_edit_text);
        non_result = rootview.findViewById(R.id.keyword_result_none);

        //그리드 레이아웃으로 한줄에 2개씩 제품 보여주기
        search_keyword_recyclerview = rootview.findViewById(R.id.search_keyword_recyclerView);
        final GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        search_keyword_recyclerview.setHasFixedSize(true);
        search_keyword_recyclerview.setLayoutManager(layoutManager);
        search_keyword_recyclerview.setAdapter(keywordAdapter);

        //툴바 뒤로가기 버튼 설정
        final Toolbar tb = rootview.findViewById(R.id.keyword_search_toolbar) ;
        ((AppCompatActivity) getActivity()).setSupportActionBar(tb) ;
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);

        //리사이클러뷰에 아이템 add
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
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
                    Material_Detail_Info item = new Material_Detail_Info(name, price+" 원",
                            preview, data, width, height, depth, keyword, stock, storename, ds.getKey());
                    keyword_item.add(item);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    keywordAdapter.getFilter().filter(editText.getText());
                    return true;
                }
                return false;
            }
        });

        //아이템 클릭시 상품 상세 페이지로 이동
        keywordAdapter.setOnItemClickListener(new HomeSearch_Keyword_Adapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                movetoProductDetail(position);
            }
        });

        return rootview;
    }

    //부자재 정보 번들에 담아서 상품 상세 페이지로 이동
    public void movetoProductDetail(int position){
        //상품 상세 페이지 정보 가져오기
        String name = keyword_item.get(position).getName();
        String price = keyword_item.get(position).getPrice();
        String[] data = keyword_item.get(position).getImg_data();
        String width = keyword_item.get(position).getWidth();
        String height = keyword_item.get(position).getHeight();
        String depth = keyword_item.get(position).getDepth();
        String keyword = keyword_item.get(position).getKeyword();
        String stock = keyword_item.get(position).getStock();
        String storename = keyword_item.get(position).getStorename();
        String unique_num = keyword_item.get(position).getUnique_number();

        Fragment tab1 = new Product_Detail_Fragment();

        //번들에 부자재 상세정보 담아서 가게 상세 페이지 프래그먼트로 보내기
        Bundle bundle = new Bundle();
        bundle.putString("name", name);
        bundle.putString("price", price);
        bundle.putStringArray("data", data);
        bundle.putString("width", width);
        bundle.putString("height", height);
        bundle.putString("depth", depth);
        bundle.putString("keyword", keyword);
        bundle.putString("stock", stock);
        bundle.putString("storename", storename);
        bundle.putString("unique_number", unique_num);
        tab1.setArguments(bundle);

        //프래그먼트 키워드 검색 -> 제품 상세 페이지로 교체
        FragmentManager fm = getActivity().getSupportFragmentManager();
        fm.beginTransaction()
                .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right)
                .replace(R.id.main_tab_view, tab1)
                .hide(HomeSearch_Keyword.this)
                .addToBackStack(null)
                .commit();
    }

}
