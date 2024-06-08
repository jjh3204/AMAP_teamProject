package com.example.amap_teamproject.TeamPage;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.amap_teamproject.R;
import com.example.amap_teamproject.utils.DateUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private List<Comment> comments;
    private String currentUserId;
    private static final int TYPE_COMMENT = 0;
    private static final int TYPE_REPLY = 1;
    private String documentId;

    public CommentAdapter(List<Comment> comments, String currentUserId, String documentId){
        this.comments = comments;
        this.currentUserId = currentUserId;
        this.documentId = documentId;
    }

    @Override
    public int getItemViewType(int position){
        if(comments.get(position).getReplies() == null)
            return TYPE_COMMENT;
        else
            return TYPE_REPLY;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == TYPE_COMMENT){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false);
            return new CommentViewHolder(view);
        }
        else{
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reply, parent, false);
            return new ReplyViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder.getItemViewType() == TYPE_COMMENT)
            ((CommentViewHolder)holder).bind(comments.get(position));
        else
            ((ReplyViewHolder)holder).bind(comments.get(position));
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public class CommentViewHolder extends RecyclerView.ViewHolder{
        public final TextView contentTextView;
        public final TextView timestampTextView;
        public final TextView authorLabel;
        public final Button replyButton;
        public final Button deleteButton;
        public final EditText replyEditText;
        public final RecyclerView repliesRecyclerView;

        public CommentViewHolder(View view) {
            super(view);
            contentTextView = view.findViewById(R.id.comment_content);
            timestampTextView = view.findViewById(R.id.comment_timestamp);
            replyButton = view.findViewById(R.id.reply_button);
            replyEditText = view.findViewById(R.id.reply_edit_text);
            repliesRecyclerView = view.findViewById(R.id.replies_recycler_view);
            authorLabel = view.findViewById(R.id.comment_author_label);
            deleteButton = view.findViewById(R.id.delete_button);

        }

        public void bind(Comment comment) {
            contentTextView.setText(comment.getContent());
            timestampTextView.setText(DateUtils.formatTimestamp(comment.getTimestamp()));
            authorLabel.setVisibility(comment.isAuthor() ? View.VISIBLE : View.GONE);

            deleteButton.setVisibility(comment.getAuthorId().equals(currentUserId) ? View.VISIBLE : View.GONE);

            if (comment.getReplies() != null) {
                repliesRecyclerView.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
                repliesRecyclerView.setAdapter(new CommentAdapter(comment.getReplies(), currentUserId, documentId));
            }

            replyButton.setOnClickListener(v -> {
                String replyContent = replyEditText.getText().toString().trim();
                if (!replyContent.isEmpty()) {
                    postReply(comment, replyContent);
                }
            });

            deleteButton.setOnClickListener(v -> deleteComment(comment));
        }

        private void postReply(Comment parentComment, String content) {
            String authorId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            long timestamp = System.currentTimeMillis();
            boolean isAuthor = authorId.equals(parentComment.getAuthorId());

            Comment reply = new Comment(content, timestamp, authorId, false, isAuthor, parentComment.getPostId(), parentComment.getId());

            FirebaseFirestore db = FirebaseFirestore.getInstance(); // 경로 수정예정
            db.collection("activities").document(documentId).collection("posts")
                    .document(parentComment.getPostId()).collection("comments")
                    .document(parentComment.getId()).collection("replies")
                    .add(reply)
                    .addOnSuccessListener(documentReference -> {
                        replyEditText.setText("");
                        parentComment.getReplies().add(reply);
                        notifyDataSetChanged();
                    });
        }

        private void deleteComment(Comment comment) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("activities").document(documentId).collection("posts")
                    .document(comment.getPostId()).collection("comments")
                    .document(comment.getId())
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        comments.remove(comment);
                        notifyDataSetChanged();
                    });
        }
    }

    public class ReplyViewHolder extends RecyclerView.ViewHolder {
        public final TextView contentTextView;
        public final TextView timestampTextView;
        public final TextView authorLabel;
        public final ImageView replyArrow;
        public final Button deleteButton;

        public ReplyViewHolder(View view) {
            super(view);
            contentTextView = view.findViewById(R.id.reply_content);
            timestampTextView = view.findViewById(R.id.reply_timestamp);
            authorLabel = view.findViewById(R.id.comment_author_label);
            replyArrow = view.findViewById(R.id.reply_arrow);
            deleteButton = view.findViewById(R.id.delete_button);
        }

        public void bind(Comment reply) {
            contentTextView.setText(reply.getContent());
            timestampTextView.setText(DateUtils.formatTimestamp(reply.getTimestamp()));
            authorLabel.setVisibility(reply.isAuthor() ? View.VISIBLE : View.GONE);
            replyArrow.setVisibility(View.VISIBLE);
            deleteButton.setVisibility(reply.getAuthorId().equals(currentUserId) ? View.VISIBLE : View.GONE);

            deleteButton.setOnClickListener(v -> deleteComment(reply));
        }

        private void deleteComment(Comment comment) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("activities").document(documentId).collection("posts")
                    .document(comment.getPostId()).collection("comments")
                    .document(comment.getParentCommentId()).collection("replies")
                    .document(comment.getId())
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        comments.remove(comment);
                        notifyDataSetChanged();
                    });
        }
    }
}
