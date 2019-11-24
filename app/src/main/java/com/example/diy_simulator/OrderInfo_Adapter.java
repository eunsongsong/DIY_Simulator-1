package com.example.diy_simulator;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class OrderInfo_Adapter extends  RecyclerView.Adapter<com.example.diy_simulator.OrderInfo_Adapter.ViewHolder> {

    Context context;
    List<Tab3_Cart_Info> items;
    int item_layout;

    public OrderInfo_Adapter(Context context, List<Tab3_Cart_Info> items, int item_layout) {
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
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_item, null);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final Tab3_Cart_Info item = items.get(position);
        final OrderInfo_In_Adapter orderInfo_in_adapter = new OrderInfo_In_Adapter(context, item.getIn_items(), R.layout.order_in_item);
        //상점 이름, 배송비 텍스트 나타내기
        holder.store_name.setText(item.getStorename());
        String str = "배송비 " + item.getDelivery_fee() + " 원";
        holder.delivery_fee.setText(str);

        //리사이클러뷰 세팅
        holder.in_item_recyclerView.setHasFixedSize(true);
        holder.in_item_recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL,false));
        holder.in_item_recyclerView.setAdapter(orderInfo_in_adapter);
        orderInfo_in_adapter.notifyDataSetChanged();
       // notifyDataSetChanged();
        int sum = 0 ;
        for(int i = 0 ; i < item.getIn_items().size(); i++)
        {
            sum += (Integer.parseInt(item.getIn_items().get(i).getPrice()) * item.getIn_items().get(i).getAmount());
        }
        holder.order_order_money.setText("주문금액 "+ sum+"");
        holder.order_sum_of_money.setText(Integer.parseInt(item.getDelivery_fee()) + sum+"");
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView store_name;
        private TextView delivery_fee;
        private TextView order_order_money;
        private TextView order_sum_of_money;
        private RecyclerView in_item_recyclerView;
        private Button order_btn;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            store_name = itemView.findViewById(R.id.order_item_store_name);
            delivery_fee = itemView.findViewById(R.id.order_delivery_money_txt);
            order_order_money = itemView.findViewById(R.id.order_order_money_txt);
            order_sum_of_money = itemView.findViewById(R.id.order_sum_of_money_txt);
            in_item_recyclerView = itemView.findViewById(R.id.order_in_recyclerView);
            order_btn = itemView.findViewById(R.id.order_proceed_btn);
        }
    }

}
