package com.example.amap_teamproject.Career;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.amap_teamproject.R;

import java.util.List;
public class CareerItemAdapter extends RecyclerView.Adapter<CareerItemAdapter.ViewHolder> {
    private List<CareerItem> mData;
    private Context mContext;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(CareerItem data);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView titleTextView;

        public ViewHolder(View view){
            super(view);
            titleTextView = view.findViewById(R.id.titleTextView);
        }
    }

    public CareerItemAdapter(List<CareerItem> data, Context context, OnItemClickListener listener){
        mData = data;
        mContext = context;
        onItemClickListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_mycareer_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position){
        CareerItem data = mData.get(position);
        holder.titleTextView.setText(data.getTitle());
        holder.itemView.setOnClickListener(v -> onItemClickListener.onItemClick(data));
    }

    @Override
    public int getItemCount(){
        return mData.size();
    }
}
