package com.example.amap_teamproject.SearchPage;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.amap_teamproject.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.List;

public class ContestAdapter extends RecyclerView.Adapter<ContestAdapter.ContestViewHolder> {

    private List<Contest> contestList;
    private Context context;

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
        holder.titleTextView.setText(contest.getTitle());
        holder.organizationTextView.setText(contest.getOrganization());
        holder.linkTextView.setText(contest.getLink());

        holder.itemView.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(contest.getLink()));
            context.startActivity(browserIntent);
        });

        holder.actionButton.setOnClickListener(v -> toggleFavorite(contest));
    }

    @Override
    public int getItemCount() {
        return contestList.size();
    }

    static class ContestViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView organizationTextView;
        TextView linkTextView;
        Button actionButton;

        ContestViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.contest_title);
            organizationTextView = itemView.findViewById(R.id.contest_organization);
            linkTextView = itemView.findViewById(R.id.contest_link);
            actionButton = itemView.findViewById(R.id.action_button);
        }
    }

    private void toggleFavorite(Contest contest) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Query query = db.collection("favorites").whereEqualTo("title", contest.getTitle());

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult().isEmpty()) {
                    // 데이터가 존재하지 않으면 추가
                    db.collection("favorites")
                            .add(contest)
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