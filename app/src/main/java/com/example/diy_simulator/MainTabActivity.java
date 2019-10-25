package com.example.diy_simulator;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainTabActivity extends AppCompatActivity {

    private ViewPager mViewPager;
    private TabLayout mTabLayout;

    FirebaseAuth firebaseAuth;
    FirebaseUser mFirebaseUser;

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = database.getReference("판매자");
    ArrayList<String> names;
    Boolean isSeller;
    private ProgressDialog pd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_tab);

        showProgress("잠시만요~");

        mViewPager = (ViewPager) findViewById(R.id.mainViewPager);
        mTabLayout = (TabLayout) findViewById(R.id.mainTabLayout);

        Drawable drawable1 = getResources().getDrawable(R.drawable.tab1_home_selector);
        Drawable drawable2 = getResources().getDrawable(R.drawable.tab2_mypage_selector);
        Drawable drawable3_cus = getResources().getDrawable(R.drawable.tab3_customer_cart_selector);
        Drawable drawable3_sell = getResources().getDrawable(R.drawable.tab3_seller_cart_selector);
        Drawable drawable4 = getResources().getDrawable(R.drawable.tab4_simulation_selector);

         isSeller = getIntent().getBooleanExtra("whoIs",false);

        mTabLayout.addTab(mTabLayout.newTab().setIcon(drawable1));
        mTabLayout.addTab(mTabLayout.newTab().setIcon(drawable2));
        if(isSeller) mTabLayout.addTab(mTabLayout.newTab().setIcon(drawable3_sell));
        else mTabLayout.addTab(mTabLayout.newTab().setIcon(drawable3_cus));
        mTabLayout.addTab(mTabLayout.newTab().setIcon(drawable4));

        //파이어베이스에서 판매자의 storname을 모두 가져와서 names[] 배열에 넣기
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int count = (int) dataSnapshot.getChildrenCount();
                names = new ArrayList<>(count);
                int i = 0;
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    names.add(ds.child("storename").getValue().toString()); //상호명
                    //Log.d("하는 중?", names[i]+"");
                    i++;
                }
                //페이지어답터 설정
                final MainTabPagerAdapter adapter = new MainTabPagerAdapter(getSupportFragmentManager(), mTabLayout.getTabCount(), isSeller, names);
                mViewPager.setAdapter(adapter);
                hideProgress();
                mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));

                mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                    @Override
                    public void onTabSelected(TabLayout.Tab tab) {
                        mViewPager.setCurrentItem(tab.getPosition(),true);

                        if(tab.getPosition() != 0) {
                            firebaseAuth = FirebaseAuth.getInstance();
                            mFirebaseUser = firebaseAuth.getCurrentUser();
                            //로그인 되어있지 않으면 로그인 요청
                            if (mFirebaseUser == null)  {
                                Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                                startActivity(intent);
                            }
                            else Log.d("현재 유저", mFirebaseUser.getEmail());
                        }
                    }

                    @Override
                    public void onTabUnselected(TabLayout.Tab tab) {

                    }

                    @Override
                    public void onTabReselected(TabLayout.Tab tab) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }
    // 프로그레스 다이얼로그 보이기
    public void showProgress(String msg) {
        if( pd == null ) { // 객체를 1회만 생성한다
            pd = new ProgressDialog(this); // 생성한다.
            pd.setCancelable(false); // 백키로 닫는 기능을 제거한다.
            pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        } pd.setMessage(msg); // 원하는 메시지를 세팅한다.
        pd.show(); // 화면에 띠워라//
    }
    public void hideProgress(){
        if( pd != null && pd.isShowing() ){
            pd.dismiss();
        }
    }
}
