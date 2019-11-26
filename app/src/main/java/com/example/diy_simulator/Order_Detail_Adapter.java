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
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

public class Order_Detail_Adapter extends RecyclerView.Adapter<Order_Detail_Adapter.ViewHolder>  {

    Context context;
    List<Order_Product_Info> items;
    int item_layout;

    public Order_Detail_Adapter(Context context, List<Order_Product_Info> items, int item_layout) {
        this.context = context;
        this.items = items;
        this.item_layout = item_layout;
    }

    @NonNull
    @Override
    public Order_Detail_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_in_item, null);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Order_Detail_Adapter.ViewHolder holder, int position) {
        Order_Product_Info item = items.get(position);

        RequestOptions requestOptions = new RequestOptions();
        requestOptions = requestOptions.placeholder(R.drawable.mungmung);

        if(!TextUtils.isEmpty(item.getProduct_url())){
            Glide.with(holder.itemView.getContext())
                    .setDefaultRequestOptions(requestOptions)
                    .load(item.getProduct_url())
                    .into(holder.img);
        }

        holder.name.setText("상품이름 "+ item.getProduct_name()+"");
        holder.price.setText("가격 "+item.getProduct_price()+"");
        holder.amount.setText("수량 "+item.getProduct_amount()+"");

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView price;
        TextView amount;
        ImageView img;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.order_item_product_name);
            price = itemView.findViewById(R.id.order_item_price);
            amount = itemView.findViewById(R.id.order_item_amount);
            img = itemView.findViewById(R.id.order_item_img);

        }
    }
}
