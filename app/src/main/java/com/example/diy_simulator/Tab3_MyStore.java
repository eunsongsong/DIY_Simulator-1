package com.example.diy_simulator;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

public class Tab3_MyStore extends Fragment {
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final ViewGroup rootview = (ViewGroup) inflater.inflate(R.layout.fragment_tab3_my_store, container, false);

        Button upload_btn = rootview.findViewById(R.id.image_upload_btn_tab3);

        upload_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //이미지 업로드 액티비티로 전환
                Intent mainIntent = new Intent(getContext(), ImageUploadActivity.class);
                startActivity(mainIntent);
            }
        });
        return rootview;
    }
}