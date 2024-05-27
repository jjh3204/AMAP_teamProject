package com.example.amap_teamproject.ui.items;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.amap_teamproject.R;
import com.example.amap_teamproject.menu.Event;

import java.util.List;

public class MyItemRecyclerViewAdapter extends RecyclerView.Adapter<MyItemRecyclerViewAdapter.ViewHolder> {

    private final List<Event> mValues;

    public MyItemRecyclerViewAdapter(List<Event> items) {
        mValues = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Event event = mValues.get(position);
        holder.mItem = event;
        holder.mTitleView.setText(event.getTitle());
        holder.mOrganizationView.setText(event.getOrganization());
        holder.mDeadlineView.setText(event.getSubPeriod());
        holder.mDescriptionView.setText(event.getDetail());

        // Glide를 사용하여 이미지 로드
        Glide.with(holder.itemView.getContext())
                .load(event.getImgSrc())
                .into(holder.mImageView);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView mTitleView;
        public final TextView mOrganizationView;
        public final TextView mDeadlineView;
        public final TextView mDescriptionView;
        public final ImageView mImageView;
        public Event mItem;

        public ViewHolder(View view) {
            super(view);
            mTitleView = view.findViewById(R.id.event_title);
            mOrganizationView = view.findViewById(R.id.event_organization);
            mDeadlineView = view.findViewById(R.id.event_sub_period);
            mDescriptionView = view.findViewById(R.id.event_detail);
            mImageView = view.findViewById(R.id.event_image);
        }
    }
}
