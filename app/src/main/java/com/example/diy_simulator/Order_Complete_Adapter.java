package com.example.diy_simulator;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import static android.content.Context.CLIPBOARD_SERVICE;

public class Order_Complete_Adapter extends RecyclerView.Adapter<Order_Complete_Adapter.ViewHolder> {

    Context context;
    List<Order_Complete_Info> items;
    int item_layout;

    public Order_Complete_Adapter(Context context, List<Order_Complete_Info> items, int item_layout) {
        this.context = context;
        this.items = items;
        this.item_layout = item_layout;
    }

    @NonNull
    @Override
    public Order_Complete_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_complete_item, null);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final Order_Complete_Adapter.ViewHolder holder, int position) {
        Order_Complete_Info item = items.get(position);

        holder.storename.setText("가게이름 : "+ item.getStorename());
        holder.seller_name.setText("판매자 이름 : " + item.getSeller_name());
        holder.seller_phone.setText("판매자 전화번호 : "+ item.getSeller_phone());
        holder.price.setText("금액 : "+ item.getPrice());
        holder.bank.setText("은행 : "+ item.getBank_name());
        holder.account.setText(item.getAccount_number());

        holder.copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("Account_Number", holder.account.getText()); //클립보드에 ID라는 이름표로 id 값을 복사하여 저장
                clipboardManager.setPrimaryClip(clipData);
                holder.copy.setBackground(holder.copy.getResources().getDrawable(R.drawable.check_mint));
            }
        });

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView storename;
        TextView seller_name;
        TextView seller_phone;
        TextView price;
        TextView bank;
        TextView account;
        ImageButton copy;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            storename = itemView.findViewById(R.id.order_complete_storename);
            seller_name = itemView.findViewById(R.id.order_complete_seller_name);
            seller_phone = itemView.findViewById(R.id.order_complete_seller_phone);
            price = itemView.findViewById(R.id.order_complete_price);
            bank = itemView.findViewById(R.id.order_complete_bank_name);
            account = itemView.findViewById(R.id.order_complete_account_number);
            copy = itemView.findViewById(R.id.copy_btn);
        }
    }
}
