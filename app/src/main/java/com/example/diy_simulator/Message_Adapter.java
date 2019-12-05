package com.example.diy_simulator;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class Message_Adapter extends  RecyclerView.Adapter<com.example.diy_simulator.Message_Adapter.ViewHolder> {

    Context context;
    List<Message_Info> items;
    int item_layout;

    public Message_Adapter(Context context, List<Message_Info> items, int item_layout) {
        this.context = context;
        this.items = items;
        this.item_layout = item_layout;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.msg_recycle_item, null);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final Message_Info item = items.get(position);

        holder.is_my_tv.setText(item.getWho());
        if(item.getWho().equals("받은 쪽지"))
            holder.is_my_tv.setTextColor(Color.parseColor("#3DC1AB"));
        else
            holder.is_my_tv.setTextColor(Color.parseColor("#FFC81E"));

        holder.msg_content.setText(item.getMsg_content());
        holder.msg_time.setText(item.getTime());

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView msg_content;
        TextView is_my_tv;
        TextView msg_time;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            msg_content = (TextView) itemView.findViewById(R.id.msg_content_tv);
            is_my_tv = (TextView) itemView.findViewById(R.id.is_my_tv);
            msg_time = (TextView) itemView.findViewById(R.id.msg_time);
        }
    }

}
