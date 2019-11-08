package com.example.diy_simulator;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class Tab3_Cart_Adapter extends  RecyclerView.Adapter<Tab3_Cart_Adapter.ViewHolder> {
    Context context;
    List<Material_Detail_Info> items;
    int item_layout;

    FirebaseDatabase database= FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("구매자");

    public Tab3_Cart_Adapter(Context context, List<Material_Detail_Info> items, int item_layout) {
        this.context = context;
        this.items = items;
        this.item_layout = item_layout;
    }

    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }

    // 리스너 객체 참조를 저장하는 변수
    private Tab3_Cart_Adapter.OnItemClickListener mListener = null ;

    // OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드
    public void setOnItemClickListener(Tab3_Cart_Adapter.OnItemClickListener listener) {
        this.mListener = listener ;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.tab3_cart_item, null);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final Material_Detail_Info item = items.get(position);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        final FirebaseUser mFirebaseUser = firebaseAuth.getCurrentUser();

        //제품 이름, 가격, 가게 이름 텍스트 나타내기
        holder.name.setText(item.getName());
        holder.price.setText(item.getPrice());
        holder.store_name.setText(item.getStorename());

        if (!TextUtils.isEmpty(item.getPreview_img_data())) {
            //제품 이미지 url로 나타내기
            byte[] decodedByteArray = Base64.decode(item.getPreview_img_data(), Base64.NO_WRAP);
            Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedByteArray, 0, decodedByteArray.length);
            holder.img.setImageBitmap(decodedBitmap);
            holder.img.setDrawingCacheEnabled(true);
            holder.img.buildDrawingCache();
        }

        // X 버튼 누르면 해당 아이템 삭제
        holder.del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 다이얼로그 띄워서 삭제 의사 묻기
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("선택한 상품을 삭제하시겠습니까?");
                // '네' 클릭시 아이템 삭제
                builder.setPositiveButton("네", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // 아이템 리스트에서 제거
                        items.remove(position);
                        notifyDataSetChanged();

                        // 구매자 DB - cart 에서 해당 번호 삭제
                        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for(DataSnapshot ds : dataSnapshot.getChildren()){
                                    assert mFirebaseUser != null;
                                    if(mFirebaseUser.getEmail().equals(ds.child("email").getValue().toString())){
                                        String cart = ds.child("cart").getValue().toString();
                                        //해당 부자재 번호 삭제
                                        cart = cart.replace(item.getUnique_number(), "");
                                        //가운데에서 삭제되었을 경우 삭제되고 남은 ## 를 #으로 변경
                                        cart = cart.replace("##","#");
                                        //맨 앞에서 삭제되었을 경우 맨 앞에 남은 # 지우기
                                        if(cart.startsWith("#")) cart = cart.substring(1);
                                        //맨 뒤에서 삭제되었을 경우 맨 뒤에 남은 # 지우기
                                        if(cart.endsWith("#")) cart = cart.substring(0, cart.length()-1);
                                        myRef.child(ds.getKey()).child("cart").setValue(cart);
                                        break;
                                    }
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }
                });

                // '아니오' 클릭시 동작 없음
                builder.setNegativeButton("아니오", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        // 이미지 클릭시 상품 정보 상세 페이지로 이동
        holder.img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) mListener.onItemClick(v, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox check;
        Button del;
        ImageView img;
        TextView store_name;
        TextView name;
        TextView price;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            check = itemView.findViewById(R.id.cart_select_checkbox);
            del = itemView.findViewById(R.id.cart_delete_btn);
            img = itemView.findViewById(R.id.cart_preview_img);
            store_name = itemView.findViewById(R.id.cart_product_store);
            name = itemView.findViewById(R.id.cart_product_name);
            price = itemView.findViewById(R.id.cart_product_price);
        }
    }
}
