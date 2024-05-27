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

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {

    private final List<Event> events;

    public EventAdapter(List<Event> events) {
        this.events = events;
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
        Event event = events.get(position);
        holder.title.setText(event.getTitle());
        holder.organization.setText(event.getOrganization());
        holder.deadline.setText(event.getDeadline());
        holder.description.setText(event.getDescription());

        // Glide를 사용하여 이미지 로드
        Glide.with(holder.itemView.getContext())
                .load(event.getPosterUrl())
                .into(holder.imageView);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), DetailActivity.class);
            intent.putExtra(DetailActivity.EXTRA_EVENT, event);
            holder.itemView.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView organization;
        TextView deadline;
        TextView description;
        ImageView imageView;

        ViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.event_title);
            organization = view.findViewById(R.id.event_organization);
            deadline = view.findViewById(R.id.event_deadline);
            description = view.findViewById(R.id.event_description);
            imageView = view.findViewById(R.id.event_image);
        }
    }
}
