package com.example.diy_simulator;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;


public class SimulationMenu extends Fragment {

    public RecyclerView simulation_menu_recycler;
    private final List<Simulation_Menu_Info> cart_item = new ArrayList<>();
    private final Simulation_Menu_Adapter menuAdapter = new Simulation_Menu_Adapter(getContext(), cart_item, R.layout.fragment_simulation_menu);

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final ViewGroup rootview = (ViewGroup) inflater.inflate(R.layout.fragment_simulation_menu, container, false);


        ImageButton x_btn = rootview.findViewById(R.id.x_button);

        x_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPause();
            }
        });


        //그리드 레이아웃으로 한줄에 2개씩 제품 보여주기
        simulation_menu_recycler = rootview.findViewById(R.id.simulation_menu_recycler);
        final GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        simulation_menu_recycler.setHasFixedSize(true);
        simulation_menu_recycler.setLayoutManager(layoutManager);
        simulation_menu_recycler.setAdapter(menuAdapter);

        return rootview;
    }

    @Override
    public void onPause(){
        super.onPause();
        FragmentManager fm = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right);
        fragmentTransaction.remove(SimulationMenu.this).commit();
        fm.popBackStack();
    }
}
