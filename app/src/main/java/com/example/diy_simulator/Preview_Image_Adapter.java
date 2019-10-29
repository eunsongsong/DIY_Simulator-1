package com.example.diy_simulator;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class Preview_Image_Adapter extends  RecyclerView.Adapter<com.example.diy_simulator.Preview_Image_Adapter.ViewHolder> {
    Context context;
    List<Preview_Image_Info> items;
    int item_layout;

    public Preview_Image_Adapter(Context context, List<Preview_Image_Info> items, int item_layout) {
        this.context = context;
        this.items = items;
        this.item_layout = item_layout;
    }

    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }

    // 리스너 객체 참조를 저장하는 변수
    private Preview_Image_Adapter.OnItemClickListener mListener = null ;

    // OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드
    public void setOnItemClickListener(Preview_Image_Adapter.OnItemClickListener listener) {
        this.mListener = listener ;
    }

    @NonNull
    @Override
    public Preview_Image_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_upload_item, null);
        return new Preview_Image_Adapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final Preview_Image_Adapter.ViewHolder holder, final int position) {
        final Preview_Image_Info item = items.get(position);
        //제품 이름, 가격 텍스트 나타내기

        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //가게 검색 결과가 존재할 때 가게 이름을 누르면 해당 가게 목록 페이지로 이동
                    if (mListener != null) mListener.onItemClick(v, position);
            }
        });

        if(position == getItemCount() - 1){
            holder.button.setVisibility(View.VISIBLE);
            holder.imageView.setVisibility(View.INVISIBLE);
        }
        else
        {
            //제품 이미지 url로 나타내기
            Log.d("이미지","ㅇㅇㅇ");
            holder.imageView.setVisibility(View.VISIBLE);
            holder.imageView.setImageBitmap(item.getBitmap());
            holder.imageView.setDrawingCacheEnabled(true);
            holder.imageView.buildDrawingCache();
            holder.button.setVisibility(View.INVISIBLE);
        }

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;
        private Button button;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.image_upload_preview);
            button = (Button) itemView.findViewById(R.id.invisible_btn);
        }
    }

}