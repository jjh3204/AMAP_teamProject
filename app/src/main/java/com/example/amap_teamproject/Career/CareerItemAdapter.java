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
    private List<CareerItem> careerList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(CareerItem careerItem);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView careerTitle;

        public ViewHolder(View view){
            super(view);
            careerTitle = view.findViewById(R.id.career_title);
        }
    }

    public CareerItemAdapter(List<CareerItem> careerList, OnItemClickListener listener){
        this.careerList = careerList;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_mycareer_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position){
        CareerItem careerItem = careerList.get(position);
        holder.careerTitle.setText(careerItem.getTitle());
        holder.itemView.setOnClickListener(v -> listener.onItemClick(careerItem));
    }

    @Override
    public int getItemCount(){
        return careerList.size();
    }
}
