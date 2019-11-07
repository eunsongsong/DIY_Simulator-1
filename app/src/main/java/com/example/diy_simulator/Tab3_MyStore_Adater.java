package com.example.diy_simulator;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class Tab3_MyStore_Adater extends  RecyclerView.Adapter<com.example.diy_simulator.Tab3_MyStore_Adater.ViewHolder> {
    Context context;
    List<Tab3_MyStore_Info> items;
    int item_layout;

    public Tab3_MyStore_Adater(Context context, List<Tab3_MyStore_Info> items, int item_layout) {
        this.context = context;
        this.items = items;
        this.item_layout = item_layout;
    }

    @NonNull
    @Override
    public com.example.diy_simulator.Tab3_MyStore_Adater.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.tab3_my_store_item, null);
        return new com.example.diy_simulator.Tab3_MyStore_Adater.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final com.example.diy_simulator.Tab3_MyStore_Adater.ViewHolder holder, final int position) {
        final Tab3_MyStore_Info item = items.get(position);

        //제품 이름, 가격 텍스트 나타내기
        holder.name.setText(item.getName());
        holder.price.setText(item.getPrice());
        if (!TextUtils.isEmpty(item.getImg_data())) {
            //제품 이미지 url로 나타내기

            byte[] decodedByteArray = Base64.decode(item.getImg_data(), Base64.NO_WRAP);
            Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedByteArray, 0, decodedByteArray.length);
            holder.img.setImageBitmap(decodedBitmap);
            holder.img.setDrawingCacheEnabled(true);
            holder.img.buildDrawingCache();

        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView price;
        ImageView img;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.mystore_product_name);
            price = itemView.findViewById(R.id.mystore_product_price);
            img = itemView.findViewById(R.id.mystore_product_img);
        }
    }
}


