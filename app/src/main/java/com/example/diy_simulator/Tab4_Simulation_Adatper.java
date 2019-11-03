package com.example.diy_simulator;



import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class Tab4_Simulation_Adatper extends  RecyclerView.Adapter<com.example.diy_simulator.Tab4_Simulation_Adatper.ViewHolder> {
    Context context;
    List<Tab4_Simulation_Item> items;
    int item_layout;

    public Tab4_Simulation_Adatper(Context context, List<Tab4_Simulation_Item> items, int item_layout) {
        this.context = context;
        this.items = items;
        this.item_layout = item_layout;
    }

    @NonNull
    @Override
    public com.example.diy_simulator.Tab4_Simulation_Adatper.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.simulation_menu_item, null);
        return new com.example.diy_simulator.Tab4_Simulation_Adatper.ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(@NonNull final com.example.diy_simulator.Tab4_Simulation_Adatper.ViewHolder holder, final int position) {
        final Tab4_Simulation_Item item = items.get(position);
/*
        //제품 이름, 가격 텍스트 나타내기
        holder.name.setText(item.getName());
        holder.price.setText(item.getPrice());

 */
        if (!TextUtils.isEmpty(item.getUrl())) {
            //제품 이미지 url로 나타내기
            Glide.with(holder.itemView.getContext())
                    .load(item.getUrl())
                    .into(holder.imageView);

        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.simulation_menu_product_img);
        }
    }
}


