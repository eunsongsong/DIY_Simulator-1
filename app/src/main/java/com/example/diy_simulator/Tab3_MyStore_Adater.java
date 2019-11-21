package com.example.diy_simulator;

import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Arrays;
import java.util.List;

public class Tab3_MyStore_Adater extends  RecyclerView.Adapter<Tab3_MyStore_Adater.ViewHolder> {
    Context context;
    List<Material_Detail_Info> items;
    int item_layout;

    FirebaseDatabase database= FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("부자재");
    DatabaseReference myRef_seller = database.getReference("판매자");
    DatabaseReference myRef_customer = database.getReference("구매자");

    FirebaseStorage storage = FirebaseStorage.getInstance("gs://diy-simulator-607c9.appspot.com");
    // Create a storage reference from our app
    StorageReference storageRef = storage.getReference();

    String seller_id = "";
    int img_num = 0;  //부자재 사진 파일 개수

    FirebaseAuth firebaseAuth;
    FirebaseUser mFirebaseUser;

    public Tab3_MyStore_Adater(Context context, List<Material_Detail_Info> items, int item_layout) {
        this.context = context;
        this.items = items;
        this.item_layout = item_layout;
    }

    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }

    // 리스너 객체 참조를 저장하는 변수
    private Tab3_MyStore_Adater.OnItemClickListener mListener = null ;

    // OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드
    public void setOnItemClickListener(Tab3_MyStore_Adater.OnItemClickListener listener) {
        this.mListener = listener ;
    }

    public interface OnModifyItemClickListener {
        void onModifyItemClick(View v, int position);
    }

    // 리스너 객체 참조를 저장하는 변수
    private Tab3_MyStore_Adater.OnModifyItemClickListener MListener = null ;

    // OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드
    public void setOnModifyItemClickListener(Tab3_MyStore_Adater.OnModifyItemClickListener listener) {
        this.MListener = listener ;
    }
    @NonNull
    @Override
    public Tab3_MyStore_Adater.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.tab3_my_store_item, null);
        return new com.example.diy_simulator.Tab3_MyStore_Adater.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final Tab3_MyStore_Adater.ViewHolder holder, final int position) {
        final Material_Detail_Info item = items.get(position);

        firebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = firebaseAuth.getCurrentUser();

        //제품 이름, 가격 텍스트 나타내기
        holder.name.setText(item.getName());
        holder.price.setText(item.getPrice()+" 원");

        RequestOptions requestOptions = new RequestOptions();
        requestOptions = requestOptions.placeholder(R.drawable.mungmung);

        if (!TextUtils.isEmpty(item.getPreview_img_url())) {
            //제품 이미지 url로 나타내기

            Glide.with(holder.itemView.getContext())
                    .setDefaultRequestOptions(requestOptions)
                    .load(item.getPreview_img_url()).into(holder.img);
            /*
            Glide.with(holder.itemView.getContext())
                    .load(R.drawable.chu)
                    .into(holder.drawableImageViewTarget);
             */
        }

        holder.item_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) mListener.onItemClick(v, position);
            }
        });

        //삭제 버튼 누르면 아이템 삭제
        holder.del_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //판매자 아이디 저장
                seller_id = mFirebaseUser.getEmail().substring(0,mFirebaseUser.getEmail().indexOf("@"));
                // 다이얼로그 띄워서 삭제 의사 묻기
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("상품을 정말 삭제하시겠습니까?");
                // '네' 클릭시 아이템 삭제
                builder.setPositiveButton("네", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        /* 디비, 스토리지에서 해당 부자재 디비, 파일 삭제 */
                        myRef.child(item.getUnique_number()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                img_num = (int)dataSnapshot.child("image_url").getChildrenCount();
                                img_num = img_num + (int)dataSnapshot.child("image_RB_url").getChildrenCount();
                                Log.i("개수", img_num + "");

                                for(int i=0; i<img_num ;i++){
                                    //스토리지에서 해당 부자재 이미지 파일 삭제
                                    // Create a reference to the file to delete
                                    StorageReference desertRef = storageRef.child(seller_id+"-"+item.getUnique_number()+"-"+i);
                                    // Delete the file
                                    desertRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            // File deleted successfully
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception exception) {
                                            // Uh-oh, an error occurred!
                                            Log.i("실패","페이렁");
                                        }
                                    });
                                }

                                //해당 부자재 디비 삭제
                                myRef.child(item.getUnique_number()).removeValue();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                        /* 판매자 material 디비에서 삭제 */
                        myRef_seller.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for(DataSnapshot ds : dataSnapshot.getChildren()){
                                    if(ds.child("email").getValue().toString().equals(mFirebaseUser.getEmail())){
                                        //판매자의 부자재 가져오기
                                        String material = ds.child("material").getValue().toString();
                                        material = material.replaceFirst(item.getUnique_number(), "");
                                        //가운데에서 삭제되었을 경우 삭제되고 남은 ## 를 #으로 변경
                                        material = material.replace("##","#");
                                        //맨 앞에서 삭제되었을 경우 맨 앞에 남은 # 지우기
                                        if(material.startsWith("#")) material = material.substring(1);
                                        //맨 뒤에서 삭제되었을 경우 맨 뒤에 남은 # 지우기
                                        if(material.endsWith("#")) material = material.substring(0, material.length()-1);
                                        myRef_seller.child(ds.getKey()).child("material").setValue(material);
                                        break;
                                    }
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                        /* 구매자 cart 디비에서 삭제 */
                        myRef_customer.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                    // 장바구니 값 가져오기
                                    String cart = ds.child("cart").getValue().toString();
                                    // 장바구니가 비어있지 않은 경우
                                    if(!TextUtils.isEmpty(cart)){
                                        String[] cart_arr = cart.split("#");
                                        boolean exist= Arrays.asList(cart_arr).contains(item.getUnique_number());
                                        // 장바구니에 해당 아이템이 존재할 경우 삭제 실행
                                        if(exist){
                                            cart = cart.replaceFirst(item.getUnique_number(), "");
                                            //가운데에서 삭제되었을 경우 삭제되고 남은 ## 를 #으로 변경
                                            cart = cart.replace("##","#");
                                            //맨 앞에서 삭제되었을 경우 맨 앞에 남은 # 지우기
                                            if(cart.startsWith("#")) cart = cart.substring(1);
                                            //맨 뒤에서 삭제되었을 경우 맨 뒤에 남은 # 지우기
                                            if(cart.endsWith("#")) cart = cart.substring(0, cart.length()-1);
                                            myRef_customer.child(ds.getKey()).child("cart").setValue(cart);
                                        }
                                    }
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                        items.remove(position);
                        notifyDataSetChanged();
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

        //수정 버튼 누르면 부자재 정보 수정
        holder.modi_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MListener != null) MListener.onModifyItemClick(v, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView price;
        ImageView img;
        RelativeLayout item_view;
        ImageButton modi_btn;
        ImageButton del_btn;
        //DrawableImageViewTarget drawableImageViewTarget;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.mystore_product_name);
            price = itemView.findViewById(R.id.mystore_product_price);
            img = itemView.findViewById(R.id.mystore_product_img);
            item_view = itemView.findViewById(R.id.mystore_prouduct_whole);
            del_btn = itemView.findViewById(R.id.mystore_item_delete_btn);
            modi_btn = itemView.findViewById(R.id.mystore_item_modify_btn);
            //drawableImageViewTarget = new DrawableImageViewTarget(img);
        }
    }
}


