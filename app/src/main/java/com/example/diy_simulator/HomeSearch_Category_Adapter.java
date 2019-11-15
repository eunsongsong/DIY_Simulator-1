package com.example.diy_simulator;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

public class HomeSearch_Category_Adapter extends RecyclerView.Adapter<HomeSearch_Category_Adapter.ViewHolder> implements Filterable {
    Context context;
    List<Material_Detail_Info> unFilteredlist;
    List<Material_Detail_Info> filteredList;
    int item_layout;
    HomeSearch_Category tab_search;

    public HomeSearch_Category_Adapter(Context context, List<Material_Detail_Info> items, int item_layout, HomeSearch_Category tab_search) {
        this.context = context;
        this.unFilteredlist = items;
        this.filteredList = items;
        this.item_layout = item_layout;
        this.tab_search = tab_search;
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
                        if (item.getCategory().toUpperCase().contains(charString.toUpperCase())) {
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
                filteredList = (List<Material_Detail_Info>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public List<Material_Detail_Info> getFilteredList() {
        return filteredList;
    }

    @NonNull
    @Override
    public HomeSearch_Category_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_category_item, null);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeSearch_Category_Adapter.ViewHolder holder, final int position) {
        final Material_Detail_Info item = filteredList.get(position);

        //제품 이름, 가격, 가게 이름 텍스트 나타내기
        holder.name.setText(item.getName());
        holder.price.setText(item.getPrice());
        holder.store_name.setText(item.getStorename());

        RequestOptions requestOptions = new RequestOptions();
        requestOptions = requestOptions.placeholder(R.drawable.mungmung);

        if (!TextUtils.isEmpty(item.getPreview_img_url())) {
            //제품 이미지 url로 나타내기
            Glide.with(holder.itemView.getContext())
                    .setDefaultRequestOptions(requestOptions)
                    .load(item.getPreview_img_url())
                    .into(holder.img);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment tab1 = new Product_Detail_Fragment();

                //번들에 부자재 상세정보 담아서 가게 상세 페이지 프래그먼트로 보내기
                Bundle bundle = new Bundle();
                bundle.putString("name", item.getName());
                bundle.putString("price", item.getPrice());
                bundle.putStringArray("url", item.getImg_url());
                bundle.putString("width", item.getWidth());
                bundle.putString("height", item.getHeight());
                bundle.putString("depth", item.getDepth());
                bundle.putString("keyword", item.getKeyword());
                bundle.putString("stock", item.getStock());
                bundle.putString("storename", item.getStorename());
                bundle.putString("unique_number", item.getUnique_number());
                bundle.putString("category", item.getCategory());
                tab1.setArguments(bundle);

                filteredList.clear();
                unFilteredlist.clear();

                //프래그먼트 키워드 검색 -> 제품 상세 페이지로 교체
                FragmentManager fm = tab_search.getActivity().getSupportFragmentManager();
                fm.beginTransaction()
                        .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right)
                        .replace(R.id.main_tab_view, tab1)
                        .hide(tab_search)
                        .addToBackStack(null)
                        .commit();
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
            name = itemView.findViewById(R.id.search_category_product_name);
            price = itemView.findViewById(R.id.search_category_product_price);
            img = itemView.findViewById(R.id.search_category_product_img);
            store_name = itemView.findViewById(R.id.search_category_store_name);
        }
    }
}
