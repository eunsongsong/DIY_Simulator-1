package com.example.diy_simulator;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Comment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;

public class ImageUploadActivity extends AppCompatActivity {

    FirebaseStorage storage = FirebaseStorage.getInstance("gs://diy-simulator-607c9.appspot.com");
    // Create a storage reference from our app
    StorageReference storageRef = storage.getReference();
    // Create a reference to 'images/mountains.jpg'

    //이 레퍼런스 child() 매개변수를 수정 하면 끝!
    StorageReference mountainImagesRef;

    FirebaseAuth firebaseAuth;
    FirebaseUser mFirebaseUser;

    private ImageView imageView;
    private Button choose_btn;
    private Button upload_btn;
    private EditText mname, mprice, mwidth, mheight, mdepth, mstock, mkeyword;
    private String name, price, width, height, depth, stock, keyword;
    private CheckBox keycheck, casecheck, earcheck, bracecheck, etccheck;

    private Spinner keyspinner, casespinner, earspinner, bracespinner, etcspinner;
    private AlertDialog alert;

    static int PICK_IMAGE = 11;
    static int CAPTURE_IMAGE = 12;
    private String imagePath;

    byte[] data_arr;
    long count;  //DB의 부자재 개수
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = database.getReference("부자재");
    private DatabaseReference myRef2 = database.getReference("판매자");
    private DatabaseReference myRef3 = database.getReference("카테고리");

    @SuppressLint("WrongThread")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_upload);
        //사진 찍어서 or 갤러리에서 가져온 사진 나타내는 이미지뷰
        imageView = (ImageView) findViewById(R.id.preview);
        choose_btn = (Button) findViewById(R.id.choose_btn); //사진 선택 버튼
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Log.d("TAG", "권한 설정 완료");
                choose_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        photoDialogRadio();
                    }
                });
                upload_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getMyChildrenCount();
                        UploadFile();
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
            choose_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    photoDialogRadio();
                }
            });
            upload_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getMyChildrenCount();
                    UploadFile();
                }
            });
        }
    }

    //사진찍기 or 앨범에서 가져오기 선택 다이얼로그
    private void photoDialogRadio() {
        final CharSequence[] PhotoModels = {"찍어서 가져오기","갤러리에서 가져오기"};
        AlertDialog.Builder alt_bld = new AlertDialog.Builder(this);
        alt_bld.setIcon(R.drawable.minticon);
        alt_bld.setTitle("부자재 사진 ");
        alt_bld.setSingleChoiceItems(PhotoModels, -1, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                if (item == 0) { //찍어서 가져오기
                    sendTakePhotoIntent();

                } else if (item == 1) { //갤러리에서 가져오기
                    takePhotoFromGallery();
                }

            }
        });
        alert = alt_bld.create();
        alert.show();
    }


    //찍어서 가져오기
    private void sendTakePhotoIntent() {
        // Camera Application을 실행한다.
        Intent cameraApp = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // 찍은 사진을 보관할 파일 객체를 만들어서 보낸다.
        File picture = savePictureFile();
        if (picture != null) {
            cameraApp.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(getApplicationContext(),"com.example.diy_simulator.fileprovider" ,picture));
            startActivityForResult(cameraApp, CAPTURE_IMAGE);
        }
    }

    /**
     * 카메라에서 찍은 사진을 외부 저장소에 저장한다.
     * @return
     */
    private  File savePictureFile(){

        //사진 파일의 이름을 만든다.
        //Date는 java.util 을 Import 한다.
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss") .format(new Date());
        String fileName = "IMG_" + timestamp;
        /** * 사진파일이 저장될 장소를 구한다.
         *  * 외장메모리에서 사진을 저장하는 폴더를 찾아서
         *  * 그곳에 MYAPP 이라는 폴더를 만든다
         *  . */
        File pictureStorage = new File(
                Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES), "MYAPP/");
        // 만약 장소가 존재하지 않는다면 폴더를 새롭게 만든다.
         if (!pictureStorage.exists()) {
        // /** * mkdir은 폴더를 하나만 만들고,
        // * mkdirs는 경로상에 존재하는 모든 폴더를 만들어준다.
        pictureStorage.mkdirs();
        }

         try{

             File file = File.createTempFile(fileName, ".jpg", pictureStorage);
             // ImageView에 보여주기위해 사진파일의 절대 경로를 얻어온다.

             imagePath = file.getAbsolutePath();

             // 찍힌 사진을 "갤러리" 앱에 추가한다.
             Intent mediaScanIntent =
                     new Intent( Intent.ACTION_MEDIA_SCANNER_SCAN_FILE );

             File f = new File( imagePath );
             Uri contentUri = Uri.fromFile( f );
             mediaScanIntent.setData( contentUri );
             this.sendBroadcast( mediaScanIntent );

             return file;
         } catch (IOException e) {
             e.printStackTrace();
         }
         return null;
    }

    //갤러리에서 사진 불러오기
    private void takePhotoFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE);
    }
    //갤러리 사진가져온거 결과(비트맵으로) 저장

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(!(resultCode == RESULT_OK))
        {
            return;
        }
      //  if (resultCode == RESULT_OK && data.getData() != null) {
            if (requestCode == PICK_IMAGE && data.getData() != null) {
                //이미지뷰에 세팅
                try {
                    // 이미지 표시
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                    imageView.setImageBitmap(bitmap);
                    imageView.setDrawingCacheEnabled(true);
                    imageView.buildDrawingCache();

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    data_arr = baos.toByteArray();

                } catch (Exception e) {
                    e.printStackTrace();

                }
            }
            else if (requestCode == CAPTURE_IMAGE){
                // 사진을 ImageView에 보여준다.
                BitmapFactory.Options factory = new BitmapFactory.Options();
                factory.inJustDecodeBounds = false;
                factory.inPurgeable = true;

                Bitmap bitmap = BitmapFactory.decodeFile(imagePath, factory);
                imageView.setImageBitmap(bitmap);
                imageView.setDrawingCacheEnabled(true);
                imageView.buildDrawingCache();

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                data_arr = baos.toByteArray();
            }
        alert.cancel();
    }

    //파이어베이스에 이미지 업로드
    private void UploadFile(){
        mountainImagesRef = storageRef.child(count+"");
        UploadTask uploadTask = mountainImagesRef.putBytes(data_arr);
        // Handle unsuccessful uploads
        uploadTask.addOnFailureListener( new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.d("----ddd----","업로드 실패");
                Toast.makeText(ImageUploadActivity.this, "이미지 업로드를 실패하였습니다.", Toast.LENGTH_SHORT).show();
                // 실패!
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                name = mname.getText().toString();
                price = mprice.getText().toString();
                width = mwidth.getText().toString();
                height = mheight.getText().toString();
                depth = mdepth.getText().toString();
                stock = mstock.getText().toString();
                keyword = mkeyword.getText().toString();
                Material material = new Material(name, price, width, height, depth, stock, keyword);
                myRef.child(count+"").setValue(material);

                //부자재 정보를 판매자, 카테고리 디비에 고유 아이디 값만 업데이트함
                UpdateSellerMaterialinfo();

                Log.d("----ddd----","업로드 성공");
                Toast.makeText(ImageUploadActivity.this, "이미지 업로드를 완료하였습니다.", Toast.LENGTH_SHORT).show();
                // 성공!
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
            }
        });
        alert.cancel();
    }

    //부자재의 child 개수 가져오기
    private void getMyChildrenCount(){
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                 count = dataSnapshot.getChildrenCount();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //부자재 정보가 디비에 저장될때 부자재의 아이디 값만 판매자 디비에 추가
    private void UpdateSellerMaterialinfo(){
        getMyChildrenCount();
        firebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = firebaseAuth.getCurrentUser();
        myRef2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    String u = mFirebaseUser.getEmail();
                    if(u.equals(ds.child("email").getValue().toString())){
                        String tmp = ds.child("material").getValue().toString();
                        myRef2.child(ds.getKey()).child("material").setValue(tmp+"#"+count);
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}





