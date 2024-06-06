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

        // Glide를 사용하여 이미지 로드
        Glide.with(holder.itemView.getContext())
                .load(event.getImgSrc())
                .into(holder.imageView);

        holder.itemView.setOnClickListener(v -> {
            incrementHitCount(event);
            Intent intent = new Intent(holder.itemView.getContext(), EventDetailActivity.class);
            intent.putExtra(EventDetailActivity.EXTRA_EVENT, event);
            holder.itemView.getContext().startActivity(intent);
        });

        initializeButton(event, holder.favButton);
        setDdayStatus(holder.ddayStatus, event.getSubPeriod());
        holder.hitCount.setText("조회수: " + event.getHits()); // 조회수 설정
    }

    @Override
    public int getItemCount() {
        return events.size();
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
            title = view.findViewById(R.id.event_title);
            organization = view.findViewById(R.id.event_organization);
            ddayStatus = view.findViewById(R.id.event_dday_status); // 추가된 부분
            hitCount = view.findViewById(R.id.event_hit_count); // 추가된 부분
            imageView = view.findViewById(R.id.event_image);
            favButton = view.findViewById(R.id.action_button);
        }
    }

    private void incrementHitCount(Event event) {
        Query query = db.collection("contests").whereEqualTo("title", event.getTitle());
        query.get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (!queryDocumentSnapshots.isEmpty()) {
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    DocumentReference docRef = document.getReference();
                    long hits = document.getLong("hits") != null ? document.getLong("hits") : 0;
                    hits++;
                    docRef.update("hits", hits);
                    event.setHits((int) hits); // 업데이트된 조회수 설정
                    break; // 제목은 유니크하다고 가정하고 첫 번째 매치에서 종료
                }
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
                long daysLeft = (diff / (1000 * 60 * 60 * 24)) + 1;
                ddayStatusView.setText("D-" + daysLeft);
            } else if (currentDate.after(startDate) && currentDate.before(endDate)) {
                long diff = endDate.getTime() - currentDate.getTime();
                long daysLeft = (diff / (1000 * 60 * 60 * 24)) + 1;
                ddayStatusView.setText("마감일까지 " + daysLeft + "일");
            } else {
                ddayStatusView.setText("마감됨");
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void initializeButton(Event event, ImageButton button) {
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

            button.setOnClickListener(v -> toggleFavorite(event, button));
        });
    }

    private void toggleFavorite(Event event, ImageButton button) {
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
                    favoriteData.put("contest_field", event.getContestField());

                    db.collection("users").document(userId).collection("favorites_event")
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
