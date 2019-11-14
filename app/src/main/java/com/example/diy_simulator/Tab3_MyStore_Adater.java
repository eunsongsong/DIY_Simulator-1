package com.example.diy_simulator;

import android.app.ProgressDialog;
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
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.DrawableImageViewTarget;

import java.util.List;

public class Tab3_MyStore_Adater extends  RecyclerView.Adapter<Tab3_MyStore_Adater.ViewHolder> {
    Context context;
    List<Material_Detail_Info> items;
    int item_layout;

    public Tab3_MyStore_Adater(Context context, List<Material_Detail_Info> items, int item_layout) {
        this.context = context;
        this.items = items;
        this.item_layout = item_layout;
    }

    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }

    // 리스너 객체 참조를 저장하는 변수
    private Tab3_MyStore_Adater.OnItemClickListener mListener = null ;

    // OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드
    public void setOnItemClickListener(Tab3_MyStore_Adater.OnItemClickListener listener) {
        this.mListener = listener ;
    }

    @NonNull
    @Override
    public Tab3_MyStore_Adater.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.tab3_my_store_item, null);
        return new com.example.diy_simulator.Tab3_MyStore_Adater.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final Tab3_MyStore_Adater.ViewHolder holder, final int position) {
        final Material_Detail_Info item = items.get(position);

        //제품 이름, 가격 텍스트 나타내기
        holder.name.setText(item.getName());
        holder.price.setText(item.getPrice());

        RequestOptions requestOptions = new RequestOptions();
        requestOptions = requestOptions.placeholder(R.drawable.mungmung);

        if (!TextUtils.isEmpty(item.getPreview_img_url())) {
            //제품 이미지 url로 나타내기

            Glide.with(holder.itemView.getContext())
                    .setDefaultRequestOptions(requestOptions)
                    .load(item.getPreview_img_url()).into(holder.img);
            /*
            Glide.with(holder.itemView.getContext())
                    .load(R.drawable.chu)
                    .into(holder.drawableImageViewTarget);

             */
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) mListener.onItemClick(v, position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView price;
        ImageView img;
        //DrawableImageViewTarget drawableImageViewTarget;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.mystore_product_name);
            price = itemView.findViewById(R.id.mystore_product_price);
            img = itemView.findViewById(R.id.mystore_product_img);
            //drawableImageViewTarget = new DrawableImageViewTarget(img);
        }
    }
}


