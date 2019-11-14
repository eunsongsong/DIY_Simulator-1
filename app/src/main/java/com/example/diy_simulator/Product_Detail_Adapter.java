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

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

public class Product_Detail_Adapter extends RecyclerView.Adapter<Product_Detail_Adapter.ViewHolder>{
    Context context;
    List<Product_Detail_Info> items;
    int item_layout;

    public Product_Detail_Adapter(Context context, List<Product_Detail_Info> items, int item_layout) {
        this.context = context;
        this.items = items;
        this.item_layout = item_layout;
    }

    @NonNull
    @Override
    public Product_Detail_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_detail_item, null);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Product_Detail_Adapter.ViewHolder holder, int position) {
        final Product_Detail_Info item = items.get(position);


        RequestOptions requestOptions = new RequestOptions();
        requestOptions = requestOptions.placeholder(R.drawable.mungmung);

        //제품 이미지 url로 나타내기
        if (!TextUtils.isEmpty(item.getImg_url())) {
            Glide.with(holder.itemView.getContext())
                    .setDefaultRequestOptions(requestOptions)
                    .load(item.getImg_url())
                    .into(holder.image);
        }

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.detail_images);
        }
    }
}
