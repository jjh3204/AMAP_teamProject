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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.example.amap_teamproject.TeamPage.TeamPageActivity;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
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

        Glide.with(holder.itemView.getContext())
                .load(event.getImgSrc())
                .into(holder.imageView);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), EventDetailActivity.class);
            intent.putExtra(EventDetailActivity.EXTRA_EVENT, event);
            holder.itemView.getContext().startActivity(intent);
        });

        initializeButton(event, holder.favButton, holder.likeCount);
        setDdayStatus(holder.ddayStatus, event.getSubPeriod());
        holder.hitCount.setText("조회수: " + event.getHits());
        holder.likeCount.setText("좋아요: " + event.getLikes());

        holder.teamRecruitButton.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), TeamPageActivity.class);
            intent.putExtra("DOCUMENT_ID", event.getTitle());
            intent.putExtra("type", "contests");
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
        TextView ddayStatus;
        TextView hitCount;
        TextView likeCount;
        ImageView imageView;
        ImageButton favButton;
        Button teamRecruitButton;

        ViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.event_title);
            organization = view.findViewById(R.id.event_organization);
            ddayStatus = view.findViewById(R.id.event_dday_status);
            hitCount = view.findViewById(R.id.event_hit_count);
            likeCount = view.findViewById(R.id.event_like_count);
            imageView = view.findViewById(R.id.event_image);
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

    private void initializeButton(Event event, ImageButton button, TextView likeCount) {
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
                    button.setImageResource(R.drawable.empty_heart);
                } else {
                    button.setImageResource(R.drawable.full_heart);
                }
            }

            button.setOnClickListener(v -> toggleFavorite(event, button, likeCount));
        });

        Query eventQuery = db.collection("contests").whereEqualTo("title", event.getTitle());
        eventQuery.get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (!queryDocumentSnapshots.isEmpty()) {
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    long likes = document.getLong("likes") != null ? document.getLong("likes") : 0;
                    likeCount.setText("좋아요: " + likes);
                    event.setLikes((int) likes);
                    break;
                }
            }
        });
    }

    private void toggleFavorite(Event event, ImageButton button, TextView likeCount) {
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
                    Map<String, Object> favoriteData = new HashMap<>();
                    favoriteData.put("title", event.getTitle());
                    favoriteData.put("time", event.getTimestamp());

                    db.collection("users").document(userId).collection("favorites_event")
                            .add(favoriteData)
                            .addOnSuccessListener(documentReference -> {
                                button.setImageResource(R.drawable.full_heart);
                                updateLikeCount(event, 1, likeCount);
                            })
                            .addOnFailureListener(e -> {
                            });
                } else {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        DocumentReference docRef = document.getReference();
                        docRef.delete()
                                .addOnSuccessListener(aVoid -> {
                                    button.setImageResource(R.drawable.empty_heart);
                                    updateLikeCount(event, -1, likeCount);
                                })
                                .addOnFailureListener(e -> {
                                });
                    }
                }
            } else {
            }
        });
    }

    private void updateLikeCount(Event event, int delta, TextView likeCount) {
        Query query = db.collection("contests").whereEqualTo("title", event.getTitle());
        query.get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (!queryDocumentSnapshots.isEmpty()) {
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    DocumentReference docRef = document.getReference();
                    long likes = document.getLong("likes") != null ? document.getLong("likes") : 0;
                    likes += delta;
                    docRef.update("likes", likes);
                    event.setLikes((int) likes);
                    likeCount.setText("좋아요: " + likes);
                    break;
                }
            }
        });
    }
}
