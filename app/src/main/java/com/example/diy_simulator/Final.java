package com.example.diy_simulator;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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

public class Final extends AppCompatActivity {

    private ImageView imageView;
    private AlertDialog alert;
    static int PICK_IMAGE = 11;
    byte[] data_arr;
    private String imagePath;

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

        Imgproc.grabCut(imgC3, mask, rect, bgdModel, fgdModel, 8, Imgproc.
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!OpenCVLoader.initDebug()) {
            // Handle initialization error
            finish();
        }
        imageView = ( ImageView) findViewById(R.id.quick_start_cropped_image);
        photoDialogRadio();
        // drawable 타입을 bitmap으로 변경;
    }

    /** Start pick image activity with chooser. */
    public void onSelectImageClick(View view) {
        CropImage.activity(null).setGuidelines(CropImageView.Guidelines.ON).start(this);
        Log.d("언제이","ㅇㅇㅇ");
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
                    //sendTakePhotoIntent();

                } else if (item == 1) { //갤러리에서 가져오기
                    takePhotoFromGallery();
                }

            }
        });
        alert = alt_bld.create();
        alert.show();
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
        /*
        if (requestCode == PICK_IMAGE && data.getData() != null) {
            //이미지뷰에 세팅
            try {
                // 이미지 표시
                //사진의 주소를 가져와 EXIF에서 회전 값을 읽어와 회전된 상태만큼 다시 회전시켜 원상복구 시킨다.
                //* EXIF : 사진의 크기, 화소, 회전, 노출정도 등의 메타데이터.
                imagePath = getRealPathFromURI(data.getData());
                int degree = getExifOrientation(imagePath);
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                bitmap = getRotatedBitmap(bitmap, degree);
                bitmap = removeBackground(bitmap);

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

         */
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                //((ImageView) findViewById(R.id.quick_start_cropped_image)).setImageURI(result.getUri());

                try {
                    // 이미지 표시
                    //사진의 주소를 가져와 EXIF에서 회전 값을 읽어와 회전된 상태만큼 다시 회전시켜 원상복구 시킨다.
                    //* EXIF : 사진의 크기, 화소, 회전, 노출정도 등의 메타데이터.
                    imageView = (ImageView) findViewById(R.id.quick_start_cropped_image);
                    imagePath = getRealPathFromURI(result.getUri());
                    int degree = getExifOrientation(imagePath);
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), result.getUri());
                    bitmap = getRotatedBitmap(bitmap, degree);
                    bitmap = removeBackground(bitmap);

                    imageView.setImageBitmap(bitmap);
                    imageView.setDrawingCacheEnabled(true);
                    imageView.buildDrawingCache();

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    data_arr = baos.toByteArray();

                } catch (Exception e) {
                    e.printStackTrace();

                }
                Toast.makeText(
                        this, "Cropping successful, Sample: " + result.getSampleSize(), Toast.LENGTH_LONG)
                        .show();
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(this, "Cropping failed: " + result.getError(), Toast.LENGTH_LONG).show();
            }
        }
       // getMyChildrenCount();  //UploadFile() 함수에서 실행하면 시간차로 이미지 파일명이 '0'이 됨
      //  alert.cancel();
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

}
