package com.example.amap_teamproject.SearchPage;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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

public class ContestAdapter extends RecyclerView.Adapter<ContestAdapter.ContestViewHolder> {

    private final List<Contest> contestList;
    private final Context context;

    public ContestAdapter(List<Contest> contestList, Context context) {
        this.contestList = contestList;
        this.context = context;
    }

    @NonNull
    @Override
    public ContestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contest_item, parent, false);
        return new ContestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContestViewHolder holder, int position) {
        Contest contest = contestList.get(position);

        // Set the text for each TextView in the ViewHolder
        holder.titleTextView.setText(contest.getTitle());
        holder.organizationTextView.setText(contest.getOrganization());

        holder.itemView.setOnClickListener(v -> {
            Intent detailIntent = new Intent(context, ContestDetailActivity.class);
            detailIntent.putExtra("title", contest.getTitle());
            detailIntent.putExtra("organization", contest.getOrganization());
            detailIntent.putExtra("link", contest.getLink());
            detailIntent.putExtra("awardScale", contest.getAwardScale());
            detailIntent.putExtra("contestField", contest.getContestField());
            detailIntent.putExtra("detail", contest.getDetail());
            detailIntent.putExtra("homepage", contest.getHomepage().toArray(new String[0]));
            detailIntent.putExtra("imgSrc", contest.getImgSrc());
            detailIntent.putExtra("noticeUrl", contest.getNoticeUrl());
            detailIntent.putExtra("participants", contest.getParticipants());
            detailIntent.putExtra("subPeriod", contest.getSubPeriod());
            detailIntent.putExtra("timestamp", contest.getTimestamp().toDate().toString());
            context.startActivity(detailIntent);
        });

        // Set the OnClickListener for the action button to toggle favorite
        holder.actionButton.setOnClickListener(v -> toggleFavorite(contest));
    }

    @Override
    public int getItemCount() {
        return contestList.size();
    }

    static class ContestViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView organizationTextView;
        Button actionButton;

        ContestViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.contest_title);
            organizationTextView = itemView.findViewById(R.id.contest_organization);
            actionButton = itemView.findViewById(R.id.action_button);
        }
    }

    private void toggleFavorite(Contest contest) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(context, "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = user.getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Query query = db.collection("users").document(userId).collection("favorites").whereEqualTo("title", contest.getTitle());

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult().isEmpty()) {
                    // 데이터가 존재하지 않으면 추가
                    Map<String, Object> favoriteData = new HashMap<>();
                    favoriteData.put("title", contest.getTitle());
                    favoriteData.put("link", contest.getLink());
                    favoriteData.put("organization", contest.getOrganization());
                    favoriteData.put("awardScale", contest.getAwardScale());
                    favoriteData.put("contestField", contest.getContestField());
                    favoriteData.put("detail", contest.getDetail());
                    favoriteData.put("homepage", contest.getHomepage());
                    favoriteData.put("imgSrc", contest.getImgSrc());
                    favoriteData.put("noticeUrl", contest.getNoticeUrl());
                    favoriteData.put("participants", contest.getParticipants());
                    favoriteData.put("subPeriod", contest.getSubPeriod());
                    favoriteData.put("timestamp", contest.getTimestamp());

                    db.collection("users").document(userId).collection("favorites")
                            .add(favoriteData)
                            .addOnSuccessListener(documentReference -> {
                                Toast.makeText(context, "Added to favorites", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(context, "Failed to add to favorites", Toast.LENGTH_SHORT).show();
                            });
                } else {
                    // 데이터가 존재하면 삭제
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        DocumentReference docRef = document.getReference();
                        docRef.delete()
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(context, "Removed from favorites", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(context, "Failed to remove from favorites", Toast.LENGTH_SHORT).show();
                                });
                    }
                }
            } else {
                Toast.makeText(context, "Error checking favorites", Toast.LENGTH_SHORT).show();
            }
        });
    }
}