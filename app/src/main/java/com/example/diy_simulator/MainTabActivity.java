package com.example.diy_simulator;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
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

    private SwipeViewPager mViewPager;
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

        showProgress();

        mViewPager = (SwipeViewPager) findViewById(R.id.mainViewPager);
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
                        if(tab.getPosition() == 3)
                            mViewPager.setPagingEnabled(false);
                        else
                            mViewPager.setPagingEnabled(true);


                        if(tab.getPosition() == 1 || tab.getPosition() == 2) {
                            firebaseAuth = FirebaseAuth.getInstance();
                            mFirebaseUser = firebaseAuth.getCurrentUser();
                            //로그인 되어있지 않으면 로그인 요청
                            if (mFirebaseUser == null)  {
                                Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                                startActivity(intent);
                                finish();
                            }
                            else Log.d("현재 유저", mFirebaseUser.getEmail());
                        }
                        mViewPager.setCurrentItem(tab.getPosition(),true);
                    }
                    //선택된 프레임을 제외하고 다 제거
                    @Override
                    public void onTabUnselected(TabLayout.Tab tab) {
                        FragmentManager fm = getSupportFragmentManager();
                        for(int i = 0; i < fm.getBackStackEntryCount(); ++i) {
                            fm.popBackStack();
                        }
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
    public void showProgress() {
        if( pd == null ) { // 객체를 1회만 생성한다
            pd = new ProgressDialog(this, R.style.NewDialog); // 생성한다.
            pd.setCancelable(false); // 백키로 닫는 기능을 제거한다.
        }
        pd.show(); // 화면에 띠워라//
    }
    public void hideProgress(){
        if( pd != null && pd.isShowing() ){
            pd.dismiss();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{ //toolbar의 back키 눌렀을 때 동작
                super.onBackPressed();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
