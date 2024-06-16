package com.example.amap_teamproject.Notice;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.amap_teamproject.R;
import com.example.amap_teamproject.utils.DateUtils;
import java.util.List;

public class NoticeAdapter extends RecyclerView.Adapter<NoticeAdapter.NoticeViewHolder> {
    private final List<Notice> noticeList;

    public NoticeAdapter(List<Notice> noticeList) {
        this.noticeList = noticeList;
    }

    @NonNull
    @Override
    public NoticeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notice, parent, false);
        return new NoticeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoticeViewHolder holder, int position) {
        Notice notice = noticeList.get(position);
        holder.titleTextView.setText(notice.getTitle());
        holder.timestampTextView.setText(DateUtils.formatTimestamp(notice.getTimestamp()));

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), NoticeDetailActivity.class);
            intent.putExtra("title", notice.getTitle());
            intent.putExtra("timestamp", notice.getTimestamp());
            intent.putExtra("content", notice.getContent());
            intent.putExtra("documentId", notice.getDocumentId());
            holder.itemView.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return noticeList.size();
    }

    static class NoticeViewHolder extends RecyclerView.ViewHolder {

        TextView titleTextView;
        TextView timestampTextView;

        NoticeViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.notice_title);
            timestampTextView = itemView.findViewById(R.id.notice_timestamp);
        }
    }
}