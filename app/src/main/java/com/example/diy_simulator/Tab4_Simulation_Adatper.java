package com.example.diy_simulator;


import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
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

import java.util.ArrayList;
import java.util.List;

public class Tab4_Simulation_Adatper extends  RecyclerView.Adapter<com.example.diy_simulator.Tab4_Simulation_Adatper.ViewHolder>  implements Filterable {
    Context context;
    List<Tab4_Simulation_Item> unFilteredlist;
    List<Tab4_Simulation_Item> filteredList;
    int item_layout;

    FirebaseStorage storage = FirebaseStorage.getInstance("gs://diy-simulator-607c9.appspot.com");
    // Create a storage reference from our app
    StorageReference storageRef = storage.getReference();
    FirebaseDatabase database= FirebaseDatabase.getInstance();
    DatabaseReference myRef_customer = database.getReference("구매자");

    FirebaseAuth firebaseAuth;
    FirebaseUser mFirebaseUser;
    String customer_id;

    public Tab4_Simulation_Adatper(Context context, List<Tab4_Simulation_Item> items , int item_layout) {
        this.context = context;
        this.unFilteredlist = items;
        this.filteredList = items;
        this.item_layout = item_layout;
    }
    public List<Tab4_Simulation_Item> getFilteredList() {
        //for(int i = 0 ; i < filteredList.size(); i++)
          // Log.d("우람",filteredList.get(i).getPreview_url());
        return filteredList;
    }
    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }

    // 리스너 객체 참조를 저장하는 변수
    private Tab4_Simulation_Adatper.OnItemClickListener mListener = null ;

    public interface OnModifyItemClickListener {
        void onModifyItemClick(View v, int position);
    }

    // 리스너 객체 참조를 저장하는 변수
    private Tab4_Simulation_Adatper.OnModifyItemClickListener MListener = null ;

    // OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드
    public void setOnModifyItemClickListener(Tab4_Simulation_Adatper.OnModifyItemClickListener listener) {
        this.MListener = listener ;
    }

    // OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드
    public void setOnItemClickListener(Tab4_Simulation_Adatper.OnItemClickListener listener) {
        this.mListener = listener ;
    }
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String charString = constraint.toString();
                if (charString.isEmpty()) {
                    filteredList = unFilteredlist;
                } else {
                    ArrayList<Tab4_Simulation_Item> filteringList = new ArrayList<>() ;
                    for (Tab4_Simulation_Item item : unFilteredlist) {
                        if(!TextUtils.isEmpty(item.getCategory()))
                            if (item.getCategory().contains(charString)) {
                                filteringList.add(item);
                            }
                    }
                    filteredList = filteringList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredList = (List<Tab4_Simulation_Item>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    @NonNull
    @Override
    public com.example.diy_simulator.Tab4_Simulation_Adatper.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.simulation_menu_item, null);
        return new com.example.diy_simulator.Tab4_Simulation_Adatper.ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(@NonNull final com.example.diy_simulator.Tab4_Simulation_Adatper.ViewHolder holder, final int position) {
        final Tab4_Simulation_Item item = filteredList.get(position);
        //제품 이름, 가격 텍스트 나타내기
        holder.name.setText(item.getName());

        firebaseAuth= FirebaseAuth.getInstance();
        mFirebaseUser = firebaseAuth.getCurrentUser();
        if(!item.isMy()){
            holder.myitem_modify_btn.setVisibility(View.GONE);
            holder.myitem_delete_btn.setVisibility(View.GONE);
        }
        else
        {
            holder.myitem_modify_btn.setVisibility(View.VISIBLE);
            holder.myitem_delete_btn.setVisibility(View.VISIBLE);
        }
        //수정 버튼 누르면 부자재 정보 수정
        holder.myitem_modify_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MListener != null) MListener.onModifyItemClick(v, position);
            }
        });

        //삭제 버튼 누르면 아이템 삭제
        holder.myitem_delete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //판매자 아이디 저장
                customer_id = mFirebaseUser.getEmail().substring(0,mFirebaseUser.getEmail().indexOf("@"));
                // 다이얼로그 띄워서 삭제 의사 묻기
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("상품을 정말 삭제하시겠습니까?");
                // '네' 클릭시 아이템 삭제
                builder.setPositiveButton("네", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        /* 디비, 스토리지에서 해당 부자재 디비, 파일 삭제 */
                        myRef_customer.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for(DataSnapshot ds: dataSnapshot.getChildren()) {
                                    if(ds.child("email").getValue().toString().equals(mFirebaseUser.getEmail())) {
                                        StorageReference desertRef = storageRef.child(customer_id + "-" + item.getUnique_number() + "-my");
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
                                            }
                                        });
                                        myRef_customer.child(ds.getKey()).child("my_image_url").child(item.getUnique_number()).removeValue();
                                        break;
                                    }
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                        filteredList.remove(position);
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

        RequestOptions requestOptions = new RequestOptions();
        requestOptions = requestOptions.placeholder(R.drawable.mungmung);

        if (!TextUtils.isEmpty(item.getPreview_url())) {
            //제품 이미지 url로 나타내기

            Glide.with(holder.itemView.getContext())
                    .setDefaultRequestOptions(requestOptions)
                    .load(item.getPreview_url())
                    .into(holder.imageView);

        }
        holder.itemView.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) mListener.onItemClick(v, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView name;
        ImageButton myitem_modify_btn;
        ImageButton myitem_delete_btn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.simulation_menu_product_img);
            name = itemView.findViewById(R.id.simulation_menu_product_name);
            myitem_modify_btn = itemView.findViewById(R.id.myitem_modify_btn_btn);
            myitem_delete_btn = itemView.findViewById(R.id.myitem_delete_btn);
        }
    }
}