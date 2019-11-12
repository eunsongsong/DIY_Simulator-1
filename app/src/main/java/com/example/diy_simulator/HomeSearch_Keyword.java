package com.example.diy_simulator;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
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
    TextView non_result, num_result;
    public RecyclerView search_keyword_recyclerview;
    private final List<Material_Detail_Info> keyword_item = new ArrayList<>();
    private final HomeSearch_Keyword_Adapter keywordAdapter = new HomeSearch_Keyword_Adapter(getContext(),
            keyword_item, R.layout.fragment_home_search_keyword, HomeSearch_Keyword.this);

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("부자재");

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final ViewGroup rootview = (ViewGroup) inflater.inflate(R.layout.fragment_home_search_keyword, container, false);

        final EditText editText = rootview.findViewById(R.id.search_keyword_edit_text);
        non_result = rootview.findViewById(R.id.keyword_result_none); //검색 결과 없음
        num_result = rootview.findViewById(R.id.number_of_keyword_result); //검색 결과 몇개이다

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
        final ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
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
                num_result.setVisibility(View.GONE);
                search_keyword_recyclerview.setVisibility(View.GONE);
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
                    search_keyword_recyclerview.setVisibility(View.VISIBLE);
                    //키보드 내리기
                    InputMethodManager immhide = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
                    immhide.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                    return true;
                }
                return false;
            }
        });

        return rootview;
    }
}
