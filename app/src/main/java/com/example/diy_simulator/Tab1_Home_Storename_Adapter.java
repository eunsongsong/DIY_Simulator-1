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

    public interface OnItemClickListener {
        void onItemClick(View v, int position, String item_name);
    }

    // 리스너 객체 참조를 저장하는 변수
    private OnItemClickListener mListener = null ;

    // OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener ;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.tab1_home_storename_item, null);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final Tab1_Home_StorenameInfo item = items.get(position);

        holder.storename.setText(item.getStorename());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //가게 검색 결과가 존재할 때 가게 이름을 누르면 해당 가게 목록 페이지로 이동
                if(!item.getStorename().equals("결과가 없습니다.")) {
                    if (mListener != null) mListener.onItemClick(v, position, item.getStorename());
                }
            }
        });


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
