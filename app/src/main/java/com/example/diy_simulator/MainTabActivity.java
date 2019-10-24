package com.example.diy_simulator;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainTabActivity extends AppCompatActivity {

    private ViewPager mViewPager;
    private TabLayout mTabLayout;

    FirebaseAuth firebaseAuth;
    FirebaseUser mFirebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_tab);

        mViewPager = (ViewPager) findViewById(R.id.mainViewPager);
        mTabLayout = (TabLayout) findViewById(R.id.mainTabLayout);

        Drawable drawable1 = getResources().getDrawable(R.drawable.tab1_home_selector);
        Drawable drawable2 = getResources().getDrawable(R.drawable.tab2_mypage_selector);
        Drawable drawable3_cus = getResources().getDrawable(R.drawable.tab3_customer_cart_selector);
        Drawable drawable3_sell = getResources().getDrawable(R.drawable.tab3_seller_cart_selector);
        Drawable drawable4 = getResources().getDrawable(R.drawable.tab4_simulation_selector);

        Boolean isSeller = getIntent().getBooleanExtra("whoIs",false);

        mTabLayout.addTab(mTabLayout.newTab().setIcon(drawable1));
        mTabLayout.addTab(mTabLayout.newTab().setIcon(drawable2));
        if(isSeller) mTabLayout.addTab(mTabLayout.newTab().setIcon(drawable3_sell));
        else mTabLayout.addTab(mTabLayout.newTab().setIcon(drawable3_cus));
        mTabLayout.addTab(mTabLayout.newTab().setIcon(drawable4));

        //페이지어답터 설정
        final MainTabPagerAdapter adapter = new MainTabPagerAdapter(getSupportFragmentManager(), mTabLayout.getTabCount());
        mViewPager.setAdapter(adapter);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));

        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition(),true);

                if(tab.getPosition() != 0) {
                    firebaseAuth = FirebaseAuth.getInstance();
                    mFirebaseUser = firebaseAuth.getCurrentUser();
                    //로그인 되어있지 않으면 로그인 요청
                    Log.d("언제까지","로그인할거야");
                    if (mFirebaseUser == null)  {
                        Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                        startActivity(intent);
                    }
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
}
