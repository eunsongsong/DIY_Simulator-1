package com.example.diy_simulator;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class HomeSearch_Category_Adapter extends RecyclerView.Adapter<HomeSearch_Category_Adapter.ViewHolder> {
    Context context;
    List<HomeSearch_Category_Info> items;
    int item_layout;

    public HomeSearch_Category_Adapter(Context context, List<HomeSearch_Category_Info> items, int item_layout) {
        this.context = context;
        this.items = items;
        this.item_layout = item_layout;
    }

    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }

    // 리스너 객체 참조를 저장하는 변수
    private HomeSearch_Category_Adapter.OnItemClickListener mListener = null ;

    // OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드
    public void setOnItemClickListener(HomeSearch_Category_Adapter.OnItemClickListener listener) {
        this.mListener = listener ;
    }

    @NonNull
    @Override
    public HomeSearch_Category_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_category_item, null);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeSearch_Category_Adapter.ViewHolder holder, final int position) {
        final HomeSearch_Category_Info item = items.get(position);

        //제품 이름, 가격, 가게 이름 텍스트 나타내기
        holder.name.setText(item.getName());
        holder.price.setText(item.getPrice());
        holder.store_name.setText(item.getStorename());
        if (!TextUtils.isEmpty(item.getPreview_img_url())) {
            //제품 이미지 url로 나타내기
            Glide.with(holder.itemView.getContext())
                    .load(item.getPreview_img_url())
                    .into(holder.img);
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
        TextView store_name;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.search_category_product_name);
            price = itemView.findViewById(R.id.search_category_product_price);
            img = itemView.findViewById(R.id.search_category_product_img);
            store_name = itemView.findViewById(R.id.search_category_store_name);
        }
    }
}
