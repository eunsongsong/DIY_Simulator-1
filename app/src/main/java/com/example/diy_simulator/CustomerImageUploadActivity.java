package com.example.diy_simulator;

import android.Manifest;
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
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

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
import java.util.Objects;
import java.util.StringTokenizer;

public class CustomerImageUploadActivity extends AppCompatActivity {

    private ProgressDialog pd;
    FirebaseStorage storage = FirebaseStorage.getInstance("gs://diy-simulator-607c9.appspot.com");
    StorageReference storageRef = storage.getReference();
    StorageReference mountainImagesRef;
    byte[] rm_data;
    UploadTask uploadTask;
    ByteArrayOutputStream baos;
    FirebaseAuth firebaseAuth;
    FirebaseUser mFirebaseUser;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = database.getReference("구매자");

    ImageButton add_btn;
    ImageButton light_button;
    String imagePath;
    private EditText mname, mwidth, mheight;
    private String name, width, height;
    int count;
    Button upload_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_image_upload);

        firebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = firebaseAuth.getCurrentUser();
        if (!OpenCVLoader.initDebug()) {
            // Handle initialization error
            finish();
        }
        showProgress();
        getMyFinalMaterialNumber();
        add_btn = findViewById(R.id.my_image);
        mname = findViewById(R.id.my_material_name);
        mwidth = findViewById(R.id.my_size_width);
        mheight = findViewById(R.id.my_size_height);
        upload_btn = findViewById(R.id.my_upload_btn);

        //툴바 뒤로가기 버튼 설정
        Toolbar tb = findViewById(R.id.my_image_upload_toolbar) ;
        setSupportActionBar(tb) ;
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);

        // 안내 팝업 버튼
        ImageButton.OnClickListener light_btnListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.my_light_button:
                        Intent intent = new Intent(CustomerImageUploadActivity.this, PopupActivity.class);
                        startActivityForResult(intent, 1);
                        break;

                }
            }
        };
        light_button = findViewById(R.id.my_light_button);
        light_button.setOnClickListener(light_btnListener);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Log.d("TAG", "권한 설정 완료");
                //이미지 선택 버튼 누르면 사진 찍기 or 갤러리에서 선택 다이얼로그 실행

                add_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onSelectImageClick();
                    }
                });
                //업로드 버튼 누르면 파이어베이스에 업로드 실행
                upload_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        UploadFile();
                    }
                });
            } else {
                Log.d("TAG", "권한 설정 요청");
                ActivityCompat.requestPermissions(CustomerImageUploadActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
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
            add_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onSelectImageClick();
                }
            });
            //업로드 버튼 누르면 파이어베이스에 업로드 실행
            upload_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        UploadFile();
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
                //사이즈 500:500 만듬

                float new_width = 0.0f;
                float new_ratio = 0.0f;
                float new_height = 0.0f;
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
                    bitmap = removeBackground(bitmap);
                    //여기서 500대 500이됨
                    baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                    rm_data = baos.toByteArray();
                    add_btn.setImageBitmap(bitmap);
                    add_btn.setDrawingCacheEnabled(true);
                    add_btn.buildDrawingCache();
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
        showProgress();
        name = mname.getText().toString();
        width = mwidth.getText().toString();
        height = mheight.getText().toString();

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (ds.child("email").getValue().toString().equals(mFirebaseUser.getEmail())) {
                        MyMaterial myMaterial = new MyMaterial(name, width, height);
                        myRef.child(ds.getKey()).child("my_image_url").child(count+"").setValue(myMaterial);
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        final int target = count;
        StringTokenizer st = new StringTokenizer(mFirebaseUser.getEmail(), "@");
        final String id = st.nextToken();

            mountainImagesRef = storageRef.child( id + "-" + target + "-my");
            uploadTask = mountainImagesRef.putBytes(rm_data);
            // Handle unsuccessful uploads
            uploadTask.addOnFailureListener( new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Log.d("----ddd----","업로드 실패");
                    Toast.makeText(CustomerImageUploadActivity.this, "업로드를 실패하였습니다.", Toast.LENGTH_SHORT).show();
                    // 실패!
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    // Alternatively way to get download URL
                    //Url을 다운받기

                    mountainImagesRef = storageRef.child(id + "-" + target + "-my");
                    mountainImagesRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Add_URL_Info(uri, target);
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
                hideProgress();
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
    // 프로그레스 다이얼로그 보이기
    public void showProgress() {
        if( pd == null ) { // 객체를 1회만 생성한다
            pd = new ProgressDialog(CustomerImageUploadActivity.this, R.style.NewDialog); // 생성한다.
            pd.setCancelable(false); // 백키로 닫는 기능을 제거한다.
        }
        pd.show(); // 화면에 띠워라//
    }
    public void hideProgress() {
        if (pd != null && pd.isShowing()) {
            pd.dismiss();
        }
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
        CropImage.activity(null).setGuidelines(CropImageView.Guidelines.ON).start(CustomerImageUploadActivity.this);
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
    //디비에 부자재 URL 넣기
    private void Add_URL_Info(final Uri uri, final int target) {
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (ds.child("email").getValue().toString().equals(mFirebaseUser.getEmail())) {
                        myRef.child(ds.getKey()).child("my_image_url").child(target + "").child("image_url").setValue(uri + "");
                        hideProgress();
                        finish();
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