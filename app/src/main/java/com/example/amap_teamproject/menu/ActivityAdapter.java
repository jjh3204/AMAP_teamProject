package com.example.amap_teamproject.menu;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.amap_teamproject.R;
import java.util.List;

public class ActivityAdapter extends RecyclerView.Adapter<ActivityAdapter.ViewHolder> {

    private final List<Activity> activities;

    public ActivityAdapter(List<Activity> activities) {
        this.activities = activities;
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
        Activity activity = activities.get(position);
        holder.title.setText(activity.getTitle());
        holder.organization.setText(activity.getOrganization());
        holder.actPeriod.setText(activity.getActPeriod());
        holder.detail.setText(activity.getDetail());

        // Glide를 사용하여 이미지 로드
        Glide.with(holder.itemView.getContext())
                .load(activity.getPosterUrl())
                .into(holder.imageView);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), DetailActivity.class);
            intent.putExtra(DetailActivity.EXTRA_ACTIVITY, activity);
            holder.itemView.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return activities.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView organization;
        TextView actPeriod;
        TextView detail;
        ImageView imageView;

        ViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.event_title);
            organization = view.findViewById(R.id.event_organization);
            actPeriod = view.findViewById(R.id.event_deadline);
            detail = view.findViewById(R.id.event_description);
            imageView = view.findViewById(R.id.event_image);
        }
    }
}



