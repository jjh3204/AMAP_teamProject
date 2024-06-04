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

public class ActivityAdapter extends RecyclerView.Adapter<ActivityAdapter.ViewHolder> {

    private final List<Activity> activities;
    private final FirebaseFirestore db;

    public ActivityAdapter(List<Activity> activities) {
        this.activities = activities;
        this.db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_item_activity, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Activity activity = activities.get(position);
        holder.title.setText(activity.getTitle());
        holder.organization.setText(activity.getOrganization());
        holder.subPeriod.setText(activity.getActPeriod());

        // Glide를 사용하여 이미지 로드
        Glide.with(holder.itemView.getContext())
                .load(activity.getPosterUrl())
                .into(holder.imageView);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), ActivityDetailActivity.class);
            intent.putExtra(ActivityDetailActivity.EXTRA_ACTIVITY, activity);
            holder.itemView.getContext().startActivity(intent);
        });

        holder.favButton.setOnClickListener(v -> toggleFavorite(activity));
    }

    @Override
    public int getItemCount() {
        return activities.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView organization;
        TextView subPeriod;
        ImageView imageView;
        Button favButton;

        ViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.activity_title);
            organization = view.findViewById(R.id.activity_organization);
            subPeriod = view.findViewById(R.id.activity_sub_period);
            imageView = view.findViewById(R.id.activity_image);
            favButton = view.findViewById(R.id.action_button);
        }
    }

    private void toggleFavorite(Activity activity) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            return;
        }

        String userId = user.getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Query query = db.collection("users").document(userId).collection("favorites_activity").whereEqualTo("title", activity.getTitle());

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult().isEmpty()) {
                    // 데이터가 존재하지 않으면 추가
                    Map<String, Object> favoriteData = new HashMap<>();
                    favoriteData.put("title", activity.getTitle());
                    favoriteData.put("organization", activity.getOrganization());
                    favoriteData.put("actPeriod", activity.getActPeriod());
                    favoriteData.put("subPeriod", activity.getSubPeriod());
                    favoriteData.put("detail", activity.getDetail());
                    favoriteData.put("posterurl", activity.getPosterUrl());
                    favoriteData.put("participants", activity.getParticipants());
                    favoriteData.put("homepage", activity.getHomepage());

                    db.collection("users").document(userId).collection("favorites_activity")
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
