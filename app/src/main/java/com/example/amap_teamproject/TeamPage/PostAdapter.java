package com.example.amap_teamproject.TeamPage;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.amap_teamproject.R;
import com.example.amap_teamproject.utils.DateUtils;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder>{
    private static List<Post> postList;
    private static PostAdapter.OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Post post);
    }

    public PostAdapter(List<Post> postList, PostAdapter.OnItemClickListener listener) {
        this.postList = postList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        ViewHolder holder = new ViewHolder(view);

        return holder;
    }

    @Override   // 실제 추가될 때
    public void onBindViewHolder(@NonNull PostAdapter.ViewHolder holder, int position) {
        Post post = postList.get(position);
        holder.post_title.setText(post.getTitle());
        holder.post_timestamp.setText(DateUtils.formatTimestamp(post.getTimestamp()));
        holder.commentCountTextView.setText(post.getCommentCount() + "개");
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        protected TextView post_title;
        protected TextView post_timestamp;
        private TextView commentCountTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            post_title = itemView.findViewById(R.id.post_title);
            post_timestamp = itemView.findViewById(R.id.post_timestamp);
            commentCountTextView = itemView.findViewById(R.id.post_comment_count);

            itemView.setOnClickListener(v ->{
                int position = getAdapterPosition();
                if(position != RecyclerView.NO_POSITION){
                    listener.onItemClick(postList.get(position));
                }
            });
        }
    }
}
