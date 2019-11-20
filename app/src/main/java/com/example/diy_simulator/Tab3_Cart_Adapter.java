package com.example.diy_simulator;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class Tab3_Cart_Adapter extends  RecyclerView.Adapter<Tab3_Cart_Adapter.ViewHolder> {

    Context context;
    List<Tab3_Cart_Info> all_store_items;
    int item_layout;
    Tab3_Cart tab3;

    public Tab3_Cart_Adapter(Context context, List<Tab3_Cart_Info> all_store_items, int item_layout, Tab3_Cart tab3) {
        this.context = context;
        this.all_store_items = all_store_items;
        this.item_layout = item_layout;
        this.tab3 = tab3;
    }

    public interface OnItemClickListener {
        void onItemClick(View v, int outer_position, int inner_position);
    }

    // 리스너 객체 참조를 저장하는 변수
    private Tab3_Cart_Adapter.OnItemClickListener mListener = null ;

    // OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드
    public void setOnItemClickListener(Tab3_Cart_Adapter.OnItemClickListener listener) {
        this.mListener = listener ;
    }

    @NonNull
    @Override
    public Tab3_Cart_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.tab3_cart_item, null);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Tab3_Cart_Adapter.ViewHolder holder, final int position) {
        final Tab3_Cart_Info item = all_store_items.get(position);
        final Tab3_Cart_In_Item_Adapter in_item_adapter = new Tab3_Cart_In_Item_Adapter(context, item.getIn_items(), R.layout.tab3_cart_item_in_item);

        //상점 이름, 배송비 텍스트 나타내기
        holder.store_name.setText(item.getStorename());
        String str = "배송비 " + item.getDelivery_fee() + " 원";
        holder.delivery_fee.setText(str);

        //리사이클러뷰 세팅
        holder.in_item_recyclerView.setHasFixedSize(true);
        holder.in_item_recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL,false));
        holder.in_item_recyclerView.setAdapter(in_item_adapter);

        in_item_adapter.setOnXItemClickListener(new Tab3_Cart_In_Item_Adapter.OnXItemClickListener() {
            @Override
            public void onXItemClick(View v) {
                if(all_store_items.get(position).getIn_items().size() == 0){
                    all_store_items.remove(position);
                    notifyDataSetChanged();
                }
                else{
                    Boolean allfalse = true;
                    for(int i=0; i<all_store_items.get(position).getIn_items().size(); i++){
                        if(all_store_items.get(position).getIn_items().get(i).getCheckBox()){
                            allfalse = false;
                        }
                    }
                    item.setAnySelected(!allfalse);
                }

                tab3.setSum_of_money();
                if(all_store_items.size() == 0 ) tab3.isEmptyCart();
            }
        });


        in_item_adapter.setOnCheckItemClickListener(new Tab3_Cart_In_Item_Adapter.OnCheckItemClickListener() {
            @Override
            public void onCheckItemClick(View v, int pos) {
                Boolean allfalse = true;
                for(int i=0; i<all_store_items.get(position).getIn_items().size(); i++){
                    if(all_store_items.get(position).getIn_items().get(i).getCheckBox()){
                        allfalse = false;
                    }
                }
                item.setAnySelected(!allfalse);
                tab3.setSum_of_money();

            }
        });

        in_item_adapter.setOnItemClickListener(new Tab3_Cart_In_Item_Adapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int pos) {
                tab3.setSum_of_money();
            }
        });

        in_item_adapter.setOnItemImageClickListener(new Tab3_Cart_In_Item_Adapter.OnItemImageClickListener() {
            @Override
            public void onItemImageClick(View v, int pos) {
                mListener.onItemClick(v, position, pos);
            }
        });

    }

    @Override
    public int getItemCount() {
        return all_store_items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView store_name;
        TextView delivery_fee;
        RecyclerView in_item_recyclerView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            store_name = itemView.findViewById(R.id.out_cart_store_name);
            delivery_fee = itemView.findViewById(R.id.out_cart_store_delivery_fee);
            in_item_recyclerView = itemView.findViewById(R.id.cart_in_item_recyclerView);
        }
    }
}
