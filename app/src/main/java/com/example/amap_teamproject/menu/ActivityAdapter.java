package com.example.amap_teamproject.menu;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

        // Glide를 사용하여 이미지 로드
        Glide.with(holder.itemView.getContext())
                .load(activity.getPosterUrl())
                .into(holder.imageView);

        holder.itemView.setOnClickListener(v -> {
            incrementHitCount(activity);
            Intent intent = new Intent(holder.itemView.getContext(), ActivityDetailActivity.class);
            intent.putExtra(ActivityDetailActivity.EXTRA_ACTIVITY, activity);
            holder.itemView.getContext().startActivity(intent);
        });

        initializeButton(activity, holder.favButton);
        setDdayStatus(holder.ddayStatus, activity.getSubPeriod());
        holder.hitCount.setText("조회수: " + activity.getHits()); // 조회수 설정
    }

    @Override
    public int getItemCount() {
        return activities.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView organization;
        TextView ddayStatus; // 추가된 D-day 상태를 위한 TextView
        TextView hitCount; // 조회수 표시를 위한 TextView 추가
        ImageView imageView;
        ImageButton favButton;

        ViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.activity_title);
            organization = view.findViewById(R.id.activity_organization);
            ddayStatus = view.findViewById(R.id.activity_dday_status); // 추가된 부분
            hitCount = view.findViewById(R.id.activity_hit_count); // 추가된 부분
            imageView = view.findViewById(R.id.activity_image);
            favButton = view.findViewById(R.id.action_button);
        }
    }

    private void incrementHitCount(Activity activity) {
        DocumentReference docRef = db.collection("activities").document(activity.getTitle());
        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                long hits = documentSnapshot.getLong("hits") != null ? documentSnapshot.getLong("hits") : 0;
                hits++;
                docRef.update("hits", hits);
                activity.setHits((int) hits); // 업데이트된 조회수 설정
            }
        });
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
                long daysLeft = (diff / (1000 * 60 * 60 * 24))+1;
                ddayStatusView.setText("D-" + daysLeft);
            } else if (currentDate.after(startDate) && currentDate.before(endDate)) {
                long diff = endDate.getTime() - currentDate.getTime();
                long daysLeft = (diff / (1000 * 60 * 60 * 24))+1;
                ddayStatusView.setText("마감일까지 " + daysLeft + "일");
            } else {
                ddayStatusView.setText("마감됨");
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void initializeButton(Activity activity, ImageButton button) {
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

            button.setOnClickListener(v -> toggleFavorite(activity, button));
        });
    }

    private void toggleFavorite(Activity activity, ImageButton button) {
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
                    favoriteData.put("interest_field", activity.getInterestField());

                    db.collection("users").document(userId).collection("favorites_activity")
                            .add(favoriteData)
                            .addOnSuccessListener(documentReference -> button.setImageResource(R.drawable.full_heart))
                            .addOnFailureListener(e -> {
                                // 실패 시 처리
                            });
                } else {
                    // 데이터가 존재하면 삭제
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        DocumentReference docRef = document.getReference();
                        docRef.delete()
                                .addOnSuccessListener(aVoid -> button.setImageResource(R.drawable.empty_heart))
                                .addOnFailureListener(e -> {
                                    // 실패 시 처리
                                });
                    }
                }
            } else {
                // 쿼리 실패 시 처리
            }
        });
    }
}
