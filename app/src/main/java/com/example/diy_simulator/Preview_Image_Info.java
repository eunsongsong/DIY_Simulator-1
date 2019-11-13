package com.example.diy_simulator;

import android.graphics.Bitmap;

public class Preview_Image_Info {

    private Bitmap bitmap;
    private byte[] data_arr;

    public Preview_Image_Info(Bitmap bitmap, byte[] data_arr) {
        this.bitmap = bitmap;
        this.data_arr = data_arr;
    }

    public Preview_Image_Info() {
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public byte[] getData_arr() {
        return data_arr;
    }
}
