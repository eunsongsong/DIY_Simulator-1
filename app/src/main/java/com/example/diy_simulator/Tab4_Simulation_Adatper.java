package com.example.diy_simulator;



import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

public class Tab4_Simulation_Adatper extends  RecyclerView.Adapter<com.example.diy_simulator.Tab4_Simulation_Adatper.ViewHolder>  implements Filterable {
    Context context;
    List<Tab4_Simulation_Item> unFilteredlist;
    List<Tab4_Simulation_Item> filteredList;
    int item_layout;



    public Tab4_Simulation_Adatper(Context context, List<Tab4_Simulation_Item> items , int item_layout) {
        this.context = context;
        this.unFilteredlist = items;
        this.filteredList = items;
        this.item_layout = item_layout;
    }
    public List<Tab4_Simulation_Item> getFilteredList() {
        for(int i = 0 ; i < filteredList.size(); i++)
            Log.d("우람",filteredList.get(i).getUrl());
        return filteredList;
    }
    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }

    // 리스너 객체 참조를 저장하는 변수
    private Tab4_Simulation_Adatper.OnItemClickListener mListener = null ;

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
/*
        //제품 이름, 가격 텍스트 나타내기
        holder.name.setText(item.getName());
        holder.price.setText(item.getPrice());

 */
        RequestOptions requestOptions = new RequestOptions();
        requestOptions = requestOptions.placeholder(R.drawable.mungmung);

        if (!TextUtils.isEmpty(item.getUrl())) {
            //제품 이미지 url로 나타내기

            Glide.with(holder.itemView.getContext())
                    .setDefaultRequestOptions(requestOptions)
                    .load(item.getUrl())
                    /*
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                          //  holder.progressBar.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                          //  holder.progressBar.setVisibility(View.GONE);
                            holder.imageView2.setVisibility(View.GONE);
                            return false;
                        }
                    })

                     */
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
        ImageView imageView2;
       // ProgressBar progressBar;
         //DrawableImageViewTarget gifImage;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.simulation_menu_product_img);
          //  imageView2 = imageView.findViewById(R.id.chuchu);
           //progressBar = itemView.findViewById(R.id.progress_circular);
           // gifImage = new DrawableImageViewTarget(imageView2);
        }
    }
}


