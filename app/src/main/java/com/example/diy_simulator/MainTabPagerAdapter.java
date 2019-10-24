package com.example.diy_simulator;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class MainTabPagerAdapter extends FragmentStatePagerAdapter {
    // Count number of tabs
    private int tabCount;
    boolean isSeller;

    public MainTabPagerAdapter(FragmentManager fm, int tabCount, boolean isSeller) {
        super(fm);
        this.tabCount = tabCount;
        this.isSeller = isSeller;
    }

    @Override
    public Fragment getItem(int position) {

        // Returning the current tabs
        switch (position) {
            case 0: //홈
                Tab1_Home tab1 = new Tab1_Home();
                return tab1;
            case 1: //마이페이지
                Tab2_MyPage tab2 = new Tab2_MyPage();
                return tab2;
            case 2: //판매자 - 내가게, 고객 - 장바구니
                if(isSeller) return new Tab3_MyStore();
                else return new Tab3_Cart();
            case 3: //시뮬레이션
                Tab4_Simulation tab4 = new Tab4_Simulation();
                return tab4;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return tabCount;
    }
}


