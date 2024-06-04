package com.example.amap_teamproject.menu;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.amap_teamproject.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {

    private final List<Event> events;
    private final FirebaseFirestore db;

    public EventAdapter(List<Event> events) {
        this.events = events;
        this.db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_item_event, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Event event = events.get(position);
        holder.title.setText(event.getTitle());
        holder.organization.setText(event.getOrganization());
        holder.subPeriod.setText(event.getSubPeriod());

        Glide.with(holder.itemView.getContext())
                .load(event.getImgSrc())
                .into(holder.imageView);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), EventDetailActivity.class);
            intent.putExtra(EventDetailActivity.EXTRA_EVENT, event);
            holder.itemView.getContext().startActivity(intent);
        });
        holder.favButton.setOnClickListener(v -> toggleFavorite(event));

    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView organization;
        TextView subPeriod;
        ImageView imageView;

        Button favButton;

        ViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.event_title);
            organization = view.findViewById(R.id.event_organization);
            subPeriod = view.findViewById(R.id.event_sub_period);
            imageView = view.findViewById(R.id.event_image);
            favButton = view.findViewById(R.id.action_button);
        }
    }

    private void toggleFavorite(Event event) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            return;
        }

        String userId = user.getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Query query = db.collection("users").document(userId).collection("favorites_event").whereEqualTo("title", event.getTitle());

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult().isEmpty()) {
                    // 데이터가 존재하지 않으면 추가
                    Map<String, Object> favoriteData = new HashMap<>();
                    favoriteData.put("title", event.getTitle());
                    favoriteData.put("organization", event.getOrganization());
                    favoriteData.put("subPeriod", event.getSubPeriod());
                    favoriteData.put("detail", event.getDetail());
                    favoriteData.put("awardScale", event.getAwardScale());
                    favoriteData.put("contestField", event.getContestField());
                    favoriteData.put("homepage", event.getHomepage());
                    favoriteData.put("imgSrc", event.getImgSrc());
                    favoriteData.put("noticeUrl", event.getNoticeUrl());
                    favoriteData.put("participants", event.getParticipants());

                    db.collection("users").document(userId).collection("favorites_event")
                            .add(favoriteData)
                            .addOnSuccessListener(documentReference -> {
                            })
                            .addOnFailureListener(e -> {
                            });
                } else {
                    // 데이터가 존재하면 삭제
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        DocumentReference docRef = document.getReference();
                        docRef.delete()
                                .addOnSuccessListener(aVoid -> {
                                })
                                .addOnFailureListener(e -> {
                                });
                    }
                }
            } else {
            }
        });
    }
}



