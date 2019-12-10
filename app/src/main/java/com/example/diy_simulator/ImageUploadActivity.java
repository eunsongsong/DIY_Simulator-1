package com.example.diy_simulator;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringTokenizer;


public class ImageUploadActivity extends AppCompatActivity {

    ImageButton light_button;
    private ProgressDialog pd;
    FirebaseStorage storage = FirebaseStorage.getInstance("gs://diy-simulator-607c9.appspot.com");
    // Create a storage reference from our app
    StorageReference storageRef = storage.getReference();
    // Create a reference to 'images/mountains.jpg'

    //이 레퍼런스 child() 매개변수를 수정 하면 끝!
    StorageReference mountainImagesRef;
    StorageReference mountainImagesRef2;

    FirebaseAuth firebaseAuth;
    FirebaseUser mFirebaseUser;
    String storename;

    private Button upload_btn;
    private EditText mname, mprice, mwidth, mheight, mdepth, mstock, mkeyword;
    private String name, price, width, height, depth, stock, keyword, category;
    private CheckBox keycheck, casecheck, earcheck, bracecheck, etccheck;

    private Spinner keyspinner, casespinner, earspinner, bracespinner, etcspinner;
    private String keyspin, casespin, earspin, bracespin, etcspin;
    private String imagePath;
    ArrayList<byte[]> data_arr;
    ArrayList<byte[]> rm_data_arr;
    ArrayList<byte[]> rm_side_data_arr;
    int count = 0;  //DB의 부자재 개수

    private CheckedTextView checkedTextView;
    private CheckedTextView checkedTextView_side;

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = database.getReference("부자재");
    private DatabaseReference myRef2 = database.getReference("판매자");

    private List<Preview_Image_Info> preview_image_infos = new ArrayList<>();
    private Preview_Image_Adapter preview_image_adapter =
            new Preview_Image_Adapter(ImageUploadActivity.this, preview_image_infos, R.layout.activity_image_upload);

    private RecyclerView preview_recycler_view;
    UploadTask uploadTask;
    ByteArrayOutputStream baos;

    @SuppressLint("WrongThread")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_upload);
        data_arr = new ArrayList<>();
        rm_data_arr = new ArrayList<>();
        rm_side_data_arr = new ArrayList<>();
        if (!OpenCVLoader.initDebug()) {
            // Handle initialization error
            finish();
        } getMyFinalMaterialNumber();
        //사진 찍어서 or 갤러리에서 가져온 사진 나타내는 이미지뷰
        checkedTextView = (CheckedTextView) findViewById(R.id.check_remove);
        checkedTextView_side = findViewById(R.id.check_remove_side);
        //imageView = (ImageView) findViewById(R.id.preview);
        upload_btn = (Button) findViewById(R.id.upload_btn); //업로드 버튼
        //부자재 정보 - 판매자 입력
        mname = (EditText) findViewById(R.id.material_name);
        mprice = (EditText) findViewById(R.id.material_price);
        mwidth = (EditText) findViewById(R.id.size_width);
        mheight = (EditText) findViewById(R.id.size_height);
        mdepth = (EditText) findViewById(R.id.size_depth);
        mstock = (EditText) findViewById(R.id.material_stock);
        mkeyword = (EditText) findViewById(R.id.material_keyword);
        //세부 카테고리 나타내는 스피너
        keyspinner = (Spinner) findViewById(R.id.spinner_keyring);
        casespinner = (Spinner) findViewById(R.id.spinner_case);
        earspinner = (Spinner) findViewById(R.id.spinner_earring);
        bracespinner = (Spinner) findViewById(R.id.spinner_bracelet);
        etcspinner = (Spinner) findViewById(R.id.spinner_etc);
        //큰 카테고리 선택 체크박스
        keycheck = (CheckBox) findViewById(R.id.category_check_keyring);
        casecheck = (CheckBox) findViewById(R.id.category_check_case);
        earcheck = (CheckBox) findViewById(R.id.category_check_earring);
        bracecheck = (CheckBox) findViewById(R.id.category_check_bracelet);
        etccheck = (CheckBox) findViewById(R.id.category_check_etc);

        //스피너 설정
        ArrayAdapter Adapter1 = ArrayAdapter.createFromResource(this, R.array.keyring, android.R.layout.simple_spinner_dropdown_item);
        keyspinner.setAdapter(Adapter1);
        ArrayAdapter Adapter2 = ArrayAdapter.createFromResource(this, R.array.phone_case, android.R.layout.simple_spinner_dropdown_item);
        casespinner.setAdapter(Adapter2);
        ArrayAdapter Adapter3 = ArrayAdapter.createFromResource(this, R.array.earring, android.R.layout.simple_spinner_dropdown_item);
        earspinner.setAdapter(Adapter3);
        ArrayAdapter Adapter4 = ArrayAdapter.createFromResource(this, R.array.bracelet, android.R.layout.simple_spinner_dropdown_item);
        bracespinner.setAdapter(Adapter4);
        ArrayAdapter Adapter5 = ArrayAdapter.createFromResource(this, R.array.etc, android.R.layout.simple_spinner_dropdown_item);
        etcspinner.setAdapter(Adapter5);

        //툴바 뒤로가기 버튼 설정
        Toolbar tb = findViewById(R.id.image_upload_toolbar) ;
        setSupportActionBar(tb) ;
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);

        //리사이클러뷰 레이아웃 매니저 설정
        preview_recycler_view = findViewById(R.id.image_recyclerview);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setOrientation(RecyclerView.HORIZONTAL);
        preview_recycler_view.setHasFixedSize(true);
        preview_recycler_view.setLayoutManager(layoutManager);
        preview_recycler_view.setAdapter(preview_image_adapter);

        Preview_Image_Info item = new Preview_Image_Info();
        preview_image_infos.add(item);
        preview_image_adapter.notifyDataSetChanged();

        //스피너 눌렸을 때 아이템 값 받아오기 - 키링
        keyspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(final AdapterView<?> parent, View view, final int position, long id) {
                keyspin = parent.getItemAtPosition(position).toString();
                Log.d("키링 스피너", keyspin+" 이다");
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //스피너 눌렸을 때 아이템 값 받아오기 - 폰케이스
        casespinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                casespin = parent.getItemAtPosition(position).toString();
                Log.d("폰케 스피너", casespin+" 이다");
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //스피너 눌렸을 때 아이템 값 받아오기 - 귀걸이
        earspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                earspin = parent.getItemAtPosition(position).toString();
                Log.d("귀걸이 스피너", earspin+" 이다");
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //스피너 눌렸을 때 아이템 값 받아오기 - 팔찌
        bracespinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                bracespin = parent.getItemAtPosition(position).toString();
                Log.d("팔찌 스피너", bracespin+" 이다");
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //스피너 눌렸을 때 아이템 값 받아오기 - 기타
        etcspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                etcspin = parent.getItemAtPosition(position).toString();
                Log.d("기타 스피너", etcspin+" 이다");
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // 안내 팝업 버튼
        ImageButton.OnClickListener light_btnListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.light_button:

                        Intent intent = new Intent(ImageUploadActivity.this, PopupActivity.class);
                        startActivityForResult(intent, 1);

                        break;

                }
            }
        };

        light_button = (ImageButton) findViewById(R.id.light_button);
        light_button.setOnClickListener(light_btnListener);

        checkedTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!checkedTextView.isChecked()) {
                    checkedTextView.setChecked(true);
                    checkedTextView_side.setChecked(false);
                }
                else {
                    checkedTextView.setChecked(false);
                }
            }
        });
        checkedTextView_side.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!checkedTextView_side.isChecked()) {
                    checkedTextView_side.setChecked(true);
                    checkedTextView.setChecked(false);
                }
                else {
                    checkedTextView_side.setChecked(false);
                }
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Log.d("TAG", "권한 설정 완료");
                //이미지 선택 버튼 누르면 사진 찍기 or 갤러리에서 선택 다이얼로그 실행

                preview_image_adapter.setOnItemClickListener(new Preview_Image_Adapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(View v, int position) {
                        onSelectImageClick();
                    }
                });

                //업로드 버튼 누르면 파이어베이스에 업로드 실행
                upload_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(data_arr.size() == 0) {
                            Toast.makeText(ImageUploadActivity.this, "상품 대표 사진이 필요합니다.", Toast.LENGTH_SHORT).show();
                        }
                        else if(rm_data_arr.size() == 0) {
                            Toast.makeText(ImageUploadActivity.this, "상품 배경이 지워진 사진이 필요합니다.", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            UploadFile();
                        }
                    }
                });
            } else {
                Log.d("TAG", "권한 설정 요청");
                ActivityCompat.requestPermissions(ImageUploadActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }
    }

    // 권한 요청
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults[0] == PackageManager.PERMISSION_GRANTED || grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            Log.d("TAG", "Permission: " + permissions[0] + " was " + grantResults[0]);
            Log.d("TAG", "Permission: " + permissions[1] + " was " + grantResults[1]);

            preview_image_adapter.setOnItemClickListener(new Preview_Image_Adapter.OnItemClickListener() {
                @Override
                public void onItemClick(View v, int position) {
                    onSelectImageClick();
                }
            });

            upload_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(data_arr.size() == 0) {
                        Toast.makeText(ImageUploadActivity.this, "상품 대표 사진이 필요합니다.", Toast.LENGTH_SHORT).show();
                    }
                    if(rm_data_arr.size() == 0) {
                        Toast.makeText(ImageUploadActivity.this, "배경이 지워진 사진이 필요합니다.", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        UploadFile();
                    }
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(!(resultCode == RESULT_OK))
        {
            return;
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            //((ImageView) findViewById(R.id.quick_start_cropped_image)).setImageURI(result.getUri());

            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            try {
                // 이미지 표시
                //사진의 주소를 가져와 EXIF에서 회전 값을 읽어와 회전된 상태만큼 다시 회전시켜 원상복구 시킨다.
                //* EXIF : 사진의 크기, 화소, 회전, 노출정도 등의 메타데이터.
                //imageView = (ImageView) findViewById(R.id.preview);
                imagePath = getRealPathFromURI(result.getUri());
                int degree = getExifOrientation(imagePath);
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), result.getUri());
                bitmap = getRotatedBitmap(bitmap, degree);
                Log.d("넓이",bitmap.getWidth()+"");
                Log.d("높이",bitmap.getHeight()+"");
                //여기까지 회전된 이미지 복구
                Bitmap temp = bitmap;
                //사이즈 500:500 만듬

                float new_width = 0.0f;
                float new_ratio = 0.0f;
                float new_height = 0.0f;
                if(checkedTextView.isChecked() || checkedTextView_side.isChecked()) {
                    if (bitmap.getWidth() >= bitmap.getHeight() && bitmap.getWidth() > 600) {
                        new_width = 600.0f; // 축소시킬 너비
                        new_ratio = (float) bitmap.getWidth() / 600.0f;
                        new_height = (float) bitmap.getHeight() / new_ratio; // 축소시킬 높이
                        bitmap = Bitmap.createScaledBitmap(bitmap, (int) new_width, (int) new_height, true);
                    } else if (bitmap.getWidth() < bitmap.getHeight() && bitmap.getHeight() > 600) {
                        new_height = 600.0f; // 축소시킬
                        new_ratio = (float) bitmap.getHeight() / 600.0f;
                        new_width = (float) bitmap.getWidth() / new_ratio; // 축소시킬 높이
                        bitmap = Bitmap.createScaledBitmap(bitmap, (int) new_width, (int) new_height, true);
                    }
                    Log.d("넓이",new_width+"");
                    Log.d("높이",new_height+"");
                    bitmap = removeBackground(bitmap);
                    //여기서 500대 500이됨
                    baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                    Preview_Image_Info item;
                    if(checkedTextView.isChecked()){
                        rm_data_arr.add(baos.toByteArray());
                        item = new Preview_Image_Info(bitmap,rm_data_arr.get(rm_data_arr.size() - 1));
                    }
                    //측면
                    else{
                        rm_side_data_arr.add(baos.toByteArray());
                        item = new Preview_Image_Info(bitmap,rm_side_data_arr.get(rm_side_data_arr.size() - 1));
                    }

                    if( preview_image_infos.size() == 1)
                        preview_image_infos.add(0,item);
                    else
                        preview_image_infos.add(preview_image_infos.size() - 1, item);
                    Log.d("ddd", preview_image_infos.size()+"");
                    checkedTextView.setChecked(false);
                    checkedTextView_side.setChecked(false);
                }
                else{ //여기가 기본사진
                    baos = new ByteArrayOutputStream();
                    Log.d("보자",temp.getWidth()+"");
                    Log.d("보자33",temp.getHeight()+"");
                    temp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    data_arr.add(baos.toByteArray());
                    Preview_Image_Info item;
                    item = new Preview_Image_Info(temp,data_arr.get(data_arr.size() - 1));
                    if( preview_image_infos.size() == 1)
                        preview_image_infos.add(0,item);
                    else
                        preview_image_infos.add(preview_image_infos.size() - 1, item);
                    Log.d("ddd", preview_image_infos.size()+"");

                }
                preview_image_adapter.notifyDataSetChanged();
/*
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "Title", null);

    */
            }
            catch (Exception e) {
                e.printStackTrace();

            }
            Toast.makeText(this, "성공!", Toast.LENGTH_LONG).show();
        }
        else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            Toast.makeText(this, "Cropping failed: " + result.getError(), Toast.LENGTH_LONG).show();
        }
          //UploadFile() 함수에서 실행하면 시간차로 이미지 파일명이 '0'이 됨
        //alert.cancel(); 잠시 안씀 사진불러오기 할 때 씀
    }

    //파이어베이스에 이미지 업로드
    private void UploadFile(){

        firebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = firebaseAuth.getCurrentUser();

        name = mname.getText().toString();
        price = mprice.getText().toString();
        width = mwidth.getText().toString();
        height = mheight.getText().toString();
        depth = mdepth.getText().toString();
        stock = mstock.getText().toString();
        keyword = mkeyword.getText().toString();

        //카테고리 가져오기
        String[] category_check = new String[5];
        category_check[0] = getSelectedCategory(keycheck, keyspin);
        category_check[1] = getSelectedCategory(casecheck, casespin);
        category_check[2] = getSelectedCategory(earcheck, earspin);
        category_check[3] = getSelectedCategory(bracecheck, bracespin);
        category_check[4] = getSelectedCategory(etccheck, etcspin);

        for(int k=0; k<5; k++){
            if(!TextUtils.isEmpty(category_check[k])){
                if(TextUtils.isEmpty(category)) category = category_check[k];
                else category = category + "#" + category_check[k];
            }
        }

        //판매자 가게 이름 가져오기
        String seller_id = mFirebaseUser.getEmail();
        seller_id = seller_id.substring(0, seller_id.indexOf("@"));
        myRef2.orderByChild(seller_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    String u = mFirebaseUser.getEmail();
                    if(u.equals(ds.child("email").getValue().toString())) {
                        storename = ds.child("storename").getValue().toString();
                        Material material = new Material(name, price, width, height, depth, stock, keyword, storename, category);
                        myRef.child(count + "").setValue(material);
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //부자재 정보를 판매자 디비에 고유 아이디 값만 업데이트함
        UpdateSellerMaterialinfo();

        final int target = (int) count;
        mFirebaseUser = firebaseAuth.getCurrentUser();
        StringTokenizer st = new StringTokenizer(mFirebaseUser.getEmail(), "@");
        final String id = st.nextToken();
        for(int i = 0; i < data_arr.size(); i++) {
            final int fi = i;
            Log.d("ddd" + data_arr.size(), i + "번");
            Log.d("1번", count + "");

            Log.d("ddd"+data_arr.size(),i+"번");

            mountainImagesRef = storageRef.child( id + "-" + target + "-"+ i +"");
            uploadTask = mountainImagesRef.putBytes(data_arr.get(i));
            // Handle unsuccessful uploads
            uploadTask.addOnFailureListener( new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Log.d("----ddd----","업로드 실패");
                    Toast.makeText(ImageUploadActivity.this, "업로드를 실패하였습니다.", Toast.LENGTH_SHORT).show();
                    // 실패!
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    // Alternatively way to get download URL
                    Log.d("1번",count+"");
                    //Url을 다운받기

                    mountainImagesRef = storageRef.child( id + "-" + target + "-"+ fi +"");
                    mountainImagesRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Add_URL_Info(uri, target, false, false, (int) (target + fi));
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "다운로드 실패", Toast.LENGTH_SHORT).show();
                        }
                    });
                    // 성공!
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                }
            });
        }

        for(int i = 0; i < rm_data_arr.size(); i++){
            final int fi = i;
            Log.d("ddd","1번");

            mountainImagesRef2 = storageRef.child(id + "-" + target + "-"+ (data_arr.size() + fi) +"");
            uploadTask = mountainImagesRef2.putBytes(rm_data_arr.get(i));
            // Handle unsuccessful uploads
            uploadTask.addOnFailureListener( new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Log.d("----ddd----","업로드 실패");
                    Toast.makeText(ImageUploadActivity.this, "업로드를 실패하였습니다.", Toast.LENGTH_SHORT).show();
                    // 실패!
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.d("2번",count+"");
                    // Alternatively way to get download URL
                    //Url을 다운받기
                    mountainImagesRef2 = storageRef.child(id + "-" + target + "-"+ (data_arr.size()+ fi) +"");
                    mountainImagesRef2.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            Add_URL_Info(uri, target, true, false, (int) (target + data_arr.size()  + fi));

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "다운로드 실패", Toast.LENGTH_SHORT).show();
                        }
                    });

                    // 성공!
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                }
            });
        }
        //측면 배경 제거 사진 업로드
        for(int i = 0; i < rm_side_data_arr.size(); i++){
            final int fi = i;
            Log.d("ddd","측면");

            mountainImagesRef2 = storageRef.child(id + "-" + target + "-"+ (data_arr.size() + rm_data_arr.size() + fi) +"");
            uploadTask = mountainImagesRef2.putBytes(rm_side_data_arr.get(i));
            // Handle unsuccessful uploads
            uploadTask.addOnFailureListener( new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Log.d("----ddd----","업로드 실패");
                    Toast.makeText(ImageUploadActivity.this, "업로드를 실패하였습니다.", Toast.LENGTH_SHORT).show();
                    // 실패!
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.d("2번",count+"");
                    // Alternatively way to get download URL
                    //Url을 다운받기
                    mountainImagesRef2 = storageRef.child(id + "-" + target + "-"+ (data_arr.size()+ rm_data_arr.size() + fi) +"");
                    mountainImagesRef2.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            Add_URL_Info(uri, target, false, true, (int) (target + data_arr.size() + rm_data_arr.size()  + fi));

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "다운로드 실패", Toast.LENGTH_SHORT).show();
                        }
                    });

                    // 성공!
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                }
            });
        }
        checkUploadstate();
    }

    // 마지막 부자재의 고유 번호 가져오기
    private void getMyFinalMaterialNumber(){
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                count = 0;
                for(DataSnapshot ds : dataSnapshot.getChildren())
                    count = Integer.parseInt(Objects.requireNonNull(ds.getKey())) + 1;
                Log.i("카운트", count+"");
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //부자재 정보가 디비에 저장될때 부자재의 아이디 값만 판매자 디비에 추가
    private void UpdateSellerMaterialinfo(){
        firebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = firebaseAuth.getCurrentUser();
        myRef2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    String u = mFirebaseUser.getEmail();
                    if(Objects.equals(u, String.valueOf(ds.child("email").getValue()))){
                        String tmp = String.valueOf(ds.child("material").getValue());
                        if(TextUtils.isEmpty(tmp)) myRef2.child(ds.getKey()).child("material").setValue(count);
                        else myRef2.child(ds.getKey()).child("material").setValue(tmp+"#"+count);
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //선택한 카테고리 스트링으로 반환
    private String getSelectedCategory(CheckBox check, String spinner){
        if(check.isChecked() && !spinner.equals("선택안함")){
            String s_category = check.getText().toString();
            if(s_category.equals("팔찌") || s_category.equals("귀걸이")){
                s_category = "액세서리>" + s_category + ">" +  spinner;
            }
            else s_category = s_category + ">" +  spinner;
            return s_category;
        }
        else return null;
    }

    //디비에 부자재 URL 넣기
    private void Add_URL_Info(final Uri uri, final int target, final boolean check, final boolean side_check, final int number) {
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for( DataSnapshot ds : dataSnapshot.getChildren()){
                    if(ds.getKey().equals(target+"")){
                        if(check)
                            myRef.child(target+"").child("image_RB_url").child(number+"").setValue(uri+"");
                        else if(side_check)
                            myRef.child(target+"").child("image_RB_SIDE_url").child(number+"").setValue(uri+"");
                        else
                            myRef.child(target+"").child("image_url").child(number+"").setValue(uri+"");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    //이미지 회전하기
    private Bitmap getRotatedBitmap(Bitmap bitmap, int degree) {
        if (degree != 0 && bitmap != null) {
            Matrix matrix = new Matrix();
            matrix.setRotate(degree, (float) bitmap.getWidth() / 2, (float) bitmap.getHeight() / 2);

            try {
                Bitmap tmpBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

                if (bitmap != tmpBitmap) {
                    bitmap.recycle();
                    bitmap = tmpBitmap;
                }
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
            }
        }

        return bitmap;
    }

    //회전 각도 구하기
    private int getExifOrientation(String filePath) {
        ExifInterface exif = null;

        try {
            exif = new ExifInterface(filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (exif != null) {
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);

            if (orientation != -1) {
                switch (orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        return 90;

                    case ExifInterface.ORIENTATION_ROTATE_180:
                        return 180;

                    case ExifInterface.ORIENTATION_ROTATE_270:
                        return 270;
                }
            }
        }

        return 0;
    }

    private String getRealPathFromURI(Uri contentURI){
        String result;

        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        }
        else{
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }

        return result;
    }

    public Bitmap removeBackground(Bitmap bitmap) {
        //GrabCut part
        Mat img = new Mat();
        Utils.bitmapToMat(bitmap, img);

        int r = img.rows();
        int c = img.cols();
        Point p1 = new Point(c / 100, r / 100);
        Point p2 = new Point(c - c / 100, r - r / 100);
        Rect rect = new Rect(p1, p2);

        Mat mask = new Mat();
        Mat fgdModel = new Mat();
        Mat bgdModel = new Mat();

        Mat imgC3 = new Mat();
        Imgproc.cvtColor(img, imgC3, Imgproc.COLOR_RGBA2RGB);

        Imgproc.grabCut(imgC3, mask, rect, bgdModel, fgdModel, 6, Imgproc.
                GC_INIT_WITH_RECT);

        Mat source = new Mat(1, 1, CvType.CV_8U, new Scalar(3.0));
        Core.compare(mask, source/* GC_PR_FGD */, mask, Core.CMP_EQ);

        //This is important. You must use Scalar(255,255, 255,255), not Scalar(255,255,255)
        Mat foreground = new Mat(img.size(), CvType.CV_8UC3, new Scalar(255,
                255, 255,255));
        img.copyTo(foreground, mask);

        // convert matrix to output bitmap
        bitmap = Bitmap.createBitmap((int) foreground.size().width,
                (int) foreground.size().height,
                Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(foreground, bitmap);
        return bitmap;
    }

    /** Start pick image activity with chooser. */
    public void onSelectImageClick() {
        CropImage.activity(null).setGuidelines(CropImageView.Guidelines.ON).start(ImageUploadActivity.this);
        Log.d("언제이","ㅇㅇㅇ");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{ //toolbar의 back키 눌렀을 때 동작
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    // 프로그레스 다이얼로그 보이기
    public void showProgress() {
        if( pd == null ) { // 객체를 1회만 생성한다
            pd = new ProgressDialog(ImageUploadActivity.this, R.style.NewDialog); // 생성한다.
            pd.setCancelable(false); // 백키로 닫는 기능을 제거한다.
        }
        pd.show(); // 화면에 띠워라//
    }
    public void hideProgress() {
        if (pd != null && pd.isShowing()) {
            pd.dismiss();
        }
    }
    private void checkUploadstate(){

        showProgress();

        myRef.child(count+"").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int a  = (int) dataSnapshot.child("image_RB_url").getChildrenCount();
                int b  = (int) dataSnapshot.child("image_url").getChildrenCount();
                int c  = (int) dataSnapshot.child("image_RB_SIDE_url").getChildrenCount();

                if( a + b + c  == rm_data_arr.size() + data_arr.size() + rm_side_data_arr.size()) {
                    hideProgress();
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
