package com.example.amap_teamproject;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
        holder.titleTextView.setText(contest.getTitle());
        holder.organizationTextView.setText(contest.getOrganization());
        holder.linkTextView.setText(contest.getLink());

        holder.itemView.setOnClickListener(v -> {
            Context context = v.getContext();
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(contest.getLink()));
            context.startActivity(browserIntent);
        });
    }

    @Override
    public int getItemCount() {
        return contestList.size();
    }

    static class ContestViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView organizationTextView;
        TextView linkTextView;

        ContestViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.contest_title);
            organizationTextView = itemView.findViewById(R.id.contest_organization);
            linkTextView = itemView.findViewById(R.id.contest_link);
        }
    }
}