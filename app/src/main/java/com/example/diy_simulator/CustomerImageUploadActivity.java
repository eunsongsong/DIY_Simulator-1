package com.example.diy_simulator;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.opencv.android.OpenCVLoader;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Objects;

public class CustomerImageUploadActivity extends AppCompatActivity {

    private ProgressDialog pd;
    FirebaseStorage storage = FirebaseStorage.getInstance("gs://diy-simulator-607c9.appspot.com");
    StorageReference storageRef = storage.getReference();
    StorageReference mountainImagesRef;
    ArrayList<byte[]> rm_data_arr;
    UploadTask uploadTask;
    ByteArrayOutputStream baos;
    FirebaseAuth firebaseAuth;
    FirebaseUser mFirebaseUser;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = database.getReference("구매자");

    int count;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_image_upload);
        rm_data_arr = new ArrayList<>();

        firebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = firebaseAuth.getCurrentUser();
        if (!OpenCVLoader.initDebug()) {
            // Handle initialization error
            finish();
        } getMyFinalMaterialNumber();
    }

    // 마지막 부자재의 고유 번호 가져오기
    private void getMyFinalMaterialNumber(){
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (ds.child("email").getValue().toString().equals(mFirebaseUser.getEmail())) {
                        myRef.child(ds.getKey()).child("my_image_url").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                count = 0;
                                for (DataSnapshot ds : dataSnapshot.getChildren())
                                    count = Integer.parseInt(Objects.requireNonNull(ds.getKey())) + 1;
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }
                Log.d("우람",count+"");
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
                /*
                count = 0;
                for(DataSnapshot ds : dataSnapshot.getChildren())
                    count = Integer.parseInt(Objects.requireNonNull(ds.getKey())) + 1;
                Log.i("카운트", count+"");

                 */
        });
    }
}