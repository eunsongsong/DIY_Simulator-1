package com.example.diy_simulator;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class Tab1_Home_Storename_Adapter extends  RecyclerView.Adapter<com.example.diy_simulator.Tab1_Home_Storename_Adapter.ViewHolder> {

    Context context;
    List<Tab1_Home_StorenameInfo> items;
    int item_layout;

    public Tab1_Home_Storename_Adapter(Context context, List<Tab1_Home_StorenameInfo> items, int item_layout) {
        this.context = context;
        this.items = items;
        this.item_layout = item_layout;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.tab1_home_storename_item, null);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Tab1_Home_StorenameInfo item = items.get(position);

        holder.storename.setText(item.getStorename());

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView storename;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            storename = (TextView) itemView.findViewById(R.id.categorized_storename);
        }
    }

}
