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
import com.example.amap_teamproject.TeamPage.TeamPageActivity; // 추가
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
            Intent intent = new Intent(holder.itemView.getContext(), ActivityDetailActivity.class);
            intent.putExtra(ActivityDetailActivity.EXTRA_ACTIVITY, activity);
            holder.itemView.getContext().startActivity(intent);
        });

        initializeButton(activity, holder.favButton, holder.likeCount);
        setDdayStatus(holder.ddayStatus, activity.getSubPeriod());
        holder.hitCount.setText("조회수: " + activity.getHits()); // 조회수 설정
        holder.likeCount.setText("찜: " + activity.getLikes()); // 좋아요 수 설정

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
        TextView ddayStatus; // 추가된 D-day 상태를 위한 TextView
        TextView hitCount; // 조회수 표시를 위한 TextView 추가
        TextView likeCount; // 좋아요 수를 표시하기 위한 TextView 추가
        ImageView imageView;
        ImageButton favButton;
        Button teamRecruitButton; // 팀원 모집 버튼 추가

        ViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.activity_title);
            organization = view.findViewById(R.id.activity_organization);
            ddayStatus = view.findViewById(R.id.activity_dday_status); // 추가된 부분
            hitCount = view.findViewById(R.id.activity_hit_count); // 추가된 부분
            likeCount = view.findViewById(R.id.activity_like_count); // 추가된 부분
            imageView = view.findViewById(R.id.activity_image);
            favButton = view.findViewById(R.id.action_button);
            teamRecruitButton = view.findViewById(R.id.team_recruit_button); // 팀원 모집 버튼 추가
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

        // 좋아요 수를 설정
        Query activityQuery = db.collection("activities").whereEqualTo("title", activity.getTitle());
        activityQuery.get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (!queryDocumentSnapshots.isEmpty()) {
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    long likes = document.getLong("likes") != null ? document.getLong("likes") : 0;
                    likeCount.setText("찜: " + likes);
                    activity.setLikes((int) likes);
                    break; // 제목은 유니크하다고 가정하고 첫 번째 매치에서 종료
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
                    // 데이터가 존재하지 않으면 추가
                    Map<String, Object> favoriteData = new HashMap<>();
                    favoriteData.put("title", activity.getTitle());
                    favoriteData.put("time", activity.getTimestamp());

                    db.collection("users").document(userId).collection("favorites_activity")
                            .add(favoriteData)
                            .addOnSuccessListener(documentReference -> {
                                button.setImageResource(R.drawable.full_heart);
                                updateLikeCount(activity, 1, likeCount); // 좋아요 수 증가
                            })
                            .addOnFailureListener(e -> {
                                // 실패 시 처리
                            });
                } else {
                    // 데이터가 존재하면 삭제
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        DocumentReference docRef = document.getReference();
                        docRef.delete()
                                .addOnSuccessListener(aVoid -> {
                                    button.setImageResource(R.drawable.empty_heart);
                                    updateLikeCount(activity, -1, likeCount); // 좋아요 수 감소
                                })
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

    private void updateLikeCount(Activity activity, int delta, TextView likeCount) {
        Query query = db.collection("activities").whereEqualTo("title", activity.getTitle());
        query.get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (!queryDocumentSnapshots.isEmpty()) {
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    DocumentReference docRef = document.getReference();
                    long likes = document.getLong("likes") != null ? document.getLong("likes") : 0;
                    likes += delta;
                    docRef.update("likes", likes);
                    activity.setLikes((int) likes); // 업데이트된 좋아요 수 설정
                    likeCount.setText("찜: " + likes); // 좋아요 수 업데이트
                    break; // 제목은 유니크하다고 가정하고 첫 번째 매치에서 종료
                }
            }
        });
    }
}
