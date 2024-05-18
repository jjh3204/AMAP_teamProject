package com.example.amap_teamproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ContestAdapter extends RecyclerView.Adapter<ContestAdapter.ContestViewHolder> {

    private List<Contest> contestList;

    public ContestAdapter(List<Contest> contestList) {
        this.contestList = contestList;
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
        holder.titleTextView.setText(contest.title);
        holder.descriptionTextView.setText(contest.description);
        holder.dateTextView.setText(contest.date);
    }

    @Override
    public int getItemCount() {
        return contestList.size();
    }

    public static class ContestViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView descriptionTextView;
        TextView dateTextView;

        public ContestViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.title_text_view);
            descriptionTextView = itemView.findViewById(R.id.description_text_view);
            dateTextView = itemView.findViewById(R.id.date_text_view);
        }
    }
}