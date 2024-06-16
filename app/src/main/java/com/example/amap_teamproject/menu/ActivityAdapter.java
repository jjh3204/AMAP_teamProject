package com.example.amap_teamproject.menu;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.amap_teamproject.R;
import com.example.amap_teamproject.TeamPage.TeamPageActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
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

        Glide.with(holder.itemView.getContext())
                .load(activity.getPosterUrl())
                .into(holder.imageView);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), ActivityDetailActivity.class);
            intent.putExtra(ActivityDetailActivity.EXTRA_ACTIVITY, activity);
            holder.itemView.getContext().startActivity(intent);
        });

        initializeButton(activity, holder.favButton, holder.likeCount);
        setDdayStatus(holder.ddayStatus, activity.getSubPeriod());
        holder.hitCount.setText("조회수: " + activity.getHits());
        holder.likeCount.setText("좋아요: " + activity.getLikes());

        holder.teamRecruitButton.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), TeamPageActivity.class);
            intent.putExtra("type", "activities");
            intent.putExtra("DOCUMENT_ID", activity.getTitle());
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
        TextView ddayStatus;
        TextView hitCount;
        TextView likeCount;
        ImageView imageView;
        ImageButton favButton;
        Button teamRecruitButton;

        ViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.activity_title);
            organization = view.findViewById(R.id.activity_organization);
            ddayStatus = view.findViewById(R.id.activity_dday_status);
            hitCount = view.findViewById(R.id.activity_hit_count);
            likeCount = view.findViewById(R.id.activity_like_count);
            imageView = view.findViewById(R.id.activity_image);
            favButton = view.findViewById(R.id.action_button);
            teamRecruitButton = view.findViewById(R.id.team_recruit_button);
        }
    }

    private void setDdayStatus(TextView ddayStatusView, String subPeriod) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yy.MM.dd", Locale.getDefault());
        String[] dates = subPeriod.split(" ~ ");
        if (dates.length < 2) return;

        try {
            Date startDate = dateFormat.parse(dates[0]);
            Date endDate = dateFormat.parse(dates[1]);
            Date currentDate = new Date();

            if (currentDate.before(startDate)) {
                long diff = startDate.getTime() - currentDate.getTime();
                long daysLeft = (diff / (1000 * 60 * 60 * 24)) + 1;
                ddayStatusView.setText("접수시작까지 " + daysLeft + "일");
            } else if (currentDate.after(startDate) && currentDate.before(endDate)) {
                long diff = endDate.getTime() - currentDate.getTime();
                long daysLeft = (diff / (1000 * 60 * 60 * 24)) + 1;
                ddayStatusView.setText("마감까지 " + daysLeft + "일");
            } else {
                ddayStatusView.setText("마감됨");
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void initializeButton(Activity activity, ImageButton button, TextView likeCount) {
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
                    button.setImageResource(R.drawable.empty_heart);
                } else {
                    button.setImageResource(R.drawable.full_heart);
                }
            }

            button.setOnClickListener(v -> toggleFavorite(activity, button, likeCount));
        });

        Query activityQuery = db.collection("activities").whereEqualTo("title", activity.getTitle());
        activityQuery.get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (!queryDocumentSnapshots.isEmpty()) {
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    long likes = document.getLong("likes") != null ? document.getLong("likes") : 0;
                    likeCount.setText("좋아요: " + likes);
                    activity.setLikes((int) likes);
                    break;
                }
            }
        });
    }

    private void toggleFavorite(Activity activity, ImageButton button, TextView likeCount) {
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
                    Map<String, Object> favoriteData = new HashMap<>();
                    favoriteData.put("title", activity.getTitle());
                    favoriteData.put("time", activity.getTimestamp());

                    db.collection("users").document(userId).collection("favorites_activity")
                            .add(favoriteData)
                            .addOnSuccessListener(documentReference -> {
                                button.setImageResource(R.drawable.full_heart);
                                updateLikeCount(activity, 1, likeCount);
                            })
                            .addOnFailureListener(e -> {
                            });
                } else {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        DocumentReference docRef = document.getReference();
                        docRef.delete()
                                .addOnSuccessListener(aVoid -> {
                                    button.setImageResource(R.drawable.empty_heart);
                                    updateLikeCount(activity, -1, likeCount);
                                })
                                .addOnFailureListener(e -> {
                                });
                    }
                }
            } else {
            }
        });
    }

    private void updateLikeCount(Activity activity, int delta, TextView likeCount) {
        Query query = db.collection("activities").whereEqualTo("title", activity.getTitle());
        query.get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (!queryDocumentSnapshots.isEmpty()) {
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    DocumentReference docRef = document.getReference();
                    long likes = document.getLong("likes") != null ? document.getLong("likes") : 0;
                    likes += delta;
                    docRef.update("likes", likes);
                    activity.setLikes((int) likes);
                    likeCount.setText("좋아요: " + likes);
                    break;
                }
            }
        });
    }
}
