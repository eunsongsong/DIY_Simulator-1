package com.example.diy_simulator;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class Tab4_Simulation extends Fragment {

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final ViewGroup rootview = (ViewGroup) inflater.inflate(R.layout.fragment_tab4_simulation, container, false);

        ImageButton menubtn = rootview.findViewById(R.id.simulation_menu_button);

        menubtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment tab4 = new SimulationMenu();

                //프래그먼트 tab4 -> SimulationMenu 로 교체
                FragmentManager fm = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fm.beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.replace(R.id.tab4_layout, tab4).commit();
            }
        });



        return rootview;
    }





    }
