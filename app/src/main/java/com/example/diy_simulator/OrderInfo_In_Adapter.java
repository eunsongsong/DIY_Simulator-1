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

public class OrderInfo_In_Adapter extends  RecyclerView.Adapter<com.example.diy_simulator.OrderInfo_In_Adapter.ViewHolder> {

    Context context;
    List<Tab3_Cart_In_Item_Info> items;
    int item_layout;

    public OrderInfo_In_Adapter(Context context, List<Tab3_Cart_In_Item_Info> items, int item_layout) {
        this.context = context;
        this.items = items;
        this.item_layout = item_layout;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_in_item, null);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final Tab3_Cart_In_Item_Info item = items.get(position);

        RequestOptions requestOptions = new RequestOptions();
        requestOptions = requestOptions.placeholder(R.drawable.mungmung);

        if(!TextUtils.isEmpty(item.getPreview_img_url())){
            Glide.with(holder.itemView.getContext())
                    .setDefaultRequestOptions(requestOptions)
                    .load(item.getPreview_img_url())
                    .into(holder.order_img);
        }

        holder.material_name.setText("상품이름 "+ item.getName()+"");
        holder.price.setText("가격 "+item.getPrice()+"");
        holder.amount.setText("수량 "+item.getAmount()+"");

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView order_img;
        private TextView material_name;
        private TextView price;
        private TextView amount;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            order_img = itemView.findViewById(R.id.order_item_img);
            material_name = itemView.findViewById(R.id.order_item_product_name);
            price = itemView.findViewById(R.id.order_item_price);
            amount = itemView.findViewById(R.id.order_item_amount);
        }
    }

}
