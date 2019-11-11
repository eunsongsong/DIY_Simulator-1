package com.example.diy_simulator;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class HomeSearch_Keyword_Adapter extends RecyclerView.Adapter<HomeSearch_Keyword_Adapter.ViewHolder> implements Filterable {
    Context context;
    List<Material_Detail_Info> unFilteredlist;
    List<Material_Detail_Info> filteredList;
    int item_layout;
    HomeSearch_Keyword search_keyword;

    public HomeSearch_Keyword_Adapter(Context context, List<Material_Detail_Info> items, int item_layout, HomeSearch_Keyword search_keyword) {
        this.context = context;
        this.unFilteredlist = items;
        this.filteredList = items;
        this.item_layout = item_layout;
        this.search_keyword = search_keyword;
    }

    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }

    // 리스너 객체 참조를 저장하는 변수
    private HomeSearch_Keyword_Adapter.OnItemClickListener mListener = null ;

    // OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드
    public void setOnItemClickListener(HomeSearch_Keyword_Adapter.OnItemClickListener listener) {
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
                    ArrayList<Material_Detail_Info> filteringList = new ArrayList<>() ;
                    for (Material_Detail_Info item : unFilteredlist) {
                        if (item.getName().toUpperCase().contains(charString.toUpperCase()) ||
                                item.getKeyword().toUpperCase().contains(charString.toUpperCase())) {
                            filteringList.add(item);
                        }
                    }
                    filteredList = filteringList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredList;
                //if(getItemCount() == 0) search_keyword.non_result.setVisibility(View.VISIBLE);
                //else search_keyword.non_result.setVisibility(View.GONE);
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredList = (List<Material_Detail_Info>) results.values;
                if(filteredList == null)
                notifyDataSetChanged();
            }
        };
    }

    @NonNull
    @Override
    public HomeSearch_Keyword_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_keyword_item, null);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeSearch_Keyword_Adapter.ViewHolder holder, final int position) {
        final Material_Detail_Info item = filteredList.get(position);

        holder.name.setText(filteredList.get(position).getName());
        holder.price.setText(filteredList.get(position).getPrice());
        holder.store_name.setText(filteredList.get(position).getStorename());

        if (!TextUtils.isEmpty(item.getPreview_img_data())) {
            byte[] decodedByteArray = Base64.decode(item.getPreview_img_data(), Base64.NO_WRAP);
            Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedByteArray, 0, decodedByteArray.length);
            holder.img.setImageBitmap(decodedBitmap);
            holder.img.setDrawingCacheEnabled(true);
            holder.img.buildDrawingCache();
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
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
        TextView name;
        TextView price;
        ImageView img;
        TextView store_name;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.search_keyword_product_name);
            price = itemView.findViewById(R.id.search_keyword_product_price);
            img = itemView.findViewById(R.id.search_keyword_product_img);
            store_name = itemView.findViewById(R.id.search_keyword_store_name);
        }
    }
}
