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

public class Tab2_MyPage_Order_Adapter extends RecyclerView.Adapter<Tab2_MyPage_Order_Adapter.ViewHolder>  {

    Context context;
    List<Order_Info> items;
    int item_layout;

    public Tab2_MyPage_Order_Adapter(Context context, List<Order_Info> items, int item_layout) {
        this.context = context;
        this.items = items;
        this.item_layout = item_layout;
    }

    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }

    // 리스너 객체 참조를 저장하는 변수
    private Tab2_MyPage_Order_Adapter.OnItemClickListener mListener = null ;

    // OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드
    public void setOnItemClickListener(Tab2_MyPage_Order_Adapter.OnItemClickListener listener) {
        this.mListener = listener ;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.mypage_orderlist_item, null);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        Order_Info item = items.get(position);

        holder.order_number.setText("주문 번호 " + item.getOrder_number());
        holder.storename.setText("주문 상점 : " + item.getStorename());
        String str = "주문 상품 : " +  item.getOrder_items().get(0).getProduct_name() + " 외";
        holder.order_items.setText(str);
        holder.price.setText("주문 금액 : " + item.getOrder_price());
        holder.delivery_fee.setText("배송비 : " + item.getDelivery_fee());
        holder.state.setText(item.getOrder_state());
        holder.state.setTextColor(Color.parseColor("#FF0000"));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onItemClick(v, position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView order_number;
        TextView storename;
        TextView order_items;
        TextView price;
        TextView delivery_fee;
        TextView state;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            order_number = itemView.findViewById(R.id.mypage_order_number);
            storename = itemView.findViewById(R.id.mypage_order_storename);
            order_items = itemView.findViewById(R.id.mypage_order_items);
            price = itemView.findViewById(R.id.mypage_order_price);
            delivery_fee = itemView.findViewById(R.id.mypage_order_delivery_fee);
            state = itemView.findViewById(R.id.mypage_order_state);
        }
    }
}
