package com.example.amap_teamproject.TeamPage;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.app.AlertDialog;
import android.text.InputType;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.amap_teamproject.R;
import com.example.amap_teamproject.utils.DateUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private List<Comment> comments;
    private String currentUserId;
    private static final int TYPE_COMMENT = 0;
    private static final int TYPE_REPLY = 1;
    private String documentId;
    private RecyclerView recyclerView;

    public CommentAdapter(List<Comment> comments, String currentUserId, String documentId, RecyclerView recyclerView){
        this.comments = comments;
        this.currentUserId = currentUserId;
        this.documentId = documentId;
        this.recyclerView = recyclerView;
    }

    @Override
    public int getItemViewType(int position){
        if(comments.get(position).getParentCommentId() == null)
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
        public final TextView authorName;
        public final Button deleteButton;
        public final RecyclerView repliesRecyclerView;
        public final Button openReplyButton;

        public CommentViewHolder(View view) {
            super(view);
            contentTextView = view.findViewById(R.id.comment_content);
            timestampTextView = view.findViewById(R.id.comment_timestamp);
            repliesRecyclerView = view.findViewById(R.id.replies_recycler_view);
            authorLabel = view.findViewById(R.id.comment_author_label);
            authorName = view.findViewById(R.id.comment_author);
            deleteButton = view.findViewById(R.id.delete_button);
            openReplyButton = view.findViewById(R.id.open_reply_button);
        }

        public void bind(Comment comment) {
            contentTextView.setText(comment.getContent());
            timestampTextView.setText(DateUtils.formatTimestamp(comment.getTimestamp()));
            authorName.setText(comment.getAuthorName());
            authorLabel.setVisibility(comment.isAuthor() ? View.VISIBLE : View.GONE);
            deleteButton.setVisibility(comment.getAuthorId().equals(currentUserId) ? View.VISIBLE : View.GONE);

            if (comment.getReplies() != null) {
                repliesRecyclerView.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
                repliesRecyclerView.setAdapter(new CommentAdapter(comment.getReplies(), currentUserId, documentId, repliesRecyclerView));
            }

            openReplyButton.setOnClickListener(v -> showReplyDialog(comment));
            deleteButton.setOnClickListener(v -> showDeleteConfirmationDialog(comment));
        }

        private void showReplyDialog(Comment parentComment) {
            AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext());

            final EditText input = new EditText(itemView.getContext());
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            input.setLines(7);  // 세로 크기를 5줄로 설정
            input.setPadding(30, 50, 30, 30);
            input.setHint("댓글을 입력하세요.");
            input.setBackground(null);
            input.setSingleLine(false);
            input.setGravity(Gravity.TOP | Gravity.START);
            builder.setView(input);

            builder.setPositiveButton("작성", (dialog, which) -> {
                String replyContent = input.getText().toString().trim();
                if (!replyContent.isEmpty()) {
                    postReply(parentComment, replyContent);
                }
            });
            builder.setNegativeButton("취소", (dialog, which) -> dialog.cancel());

            AlertDialog dialog = builder.create();

            dialog.setOnShowListener(dialogInterface -> {
                input.requestFocus();
                input.postDelayed(() -> {
                    InputMethodManager imm = (InputMethodManager) itemView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.showSoftInput(input, InputMethodManager.SHOW_IMPLICIT);
                    }
                }, 100);
            });

            dialog.show();
        }

        private void showDeleteConfirmationDialog(Comment comment) {
            AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext());
            builder.setMessage("댓글을 삭제하시겠습니까?");
            builder.setPositiveButton("삭제", (dialog, which) -> deleteComment(comment));
            builder.setNegativeButton("취소", (dialog, which) -> dialog.dismiss());
            builder.show();
        }

        private void postReply(Comment parentComment, String content) {
            String authorId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            long timestamp = System.currentTimeMillis();
            boolean isAuthor = authorId.equals(parentComment.getPostAuthorId());

            FirebaseFirestore db = FirebaseFirestore.getInstance();

            db.collection("users").document(currentUserId).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String authorName = documentSnapshot.getString("name");
                            DocumentReference newCommentRef = db.collection(parentComment.getType()).document(documentId)
                                    .collection("posts").document(parentComment.getPostId())
                                    .collection("comments").document(parentComment.getCommentId())
                                    .collection("replies").document();

                            String replyId = newCommentRef.getId();

                            Comment reply = new Comment(parentComment.getType(), content, timestamp, authorId, isAuthor,
                                    parentComment.getPostId(), parentComment.getCommentId(), replyId, authorName, parentComment.getPostAuthorId());

                            newCommentRef.set(reply)
                                    .addOnSuccessListener(aVoid -> {
                                        if (parentComment.getReplies() == null) {
                                            parentComment.setReplies(new ArrayList<>());
                                        }
                                        parentComment.getReplies().add(reply);
                                        notifyDataSetChanged();
                                    });
                        }
                    });
        }

        private void deleteComment(Comment comment) {
            new FirestoreUtils().deleteCommentWithReplies(comment.getType(), documentId, comment.getPostId(), comment.getCommentId())
                    .addOnSuccessListener(aVoid -> {
                        int position = getAdapterPosition();
                        comments.remove(position);
                        notifyItemRemoved(position);
                    });
        }
    }

    public class ReplyViewHolder extends RecyclerView.ViewHolder {
        public final TextView contentTextView;
        public final TextView timestampTextView;
        public final TextView authorLabel;
        public final TextView authorName;
        public final ImageView replyArrow;
        public final Button deleteButton;

        public ReplyViewHolder(View view) {
            super(view);
            contentTextView = view.findViewById(R.id.reply_content);
            timestampTextView = view.findViewById(R.id.reply_timestamp);
            authorName = view.findViewById(R.id.comment_author);
            authorLabel = view.findViewById(R.id.comment_author_label);
            replyArrow = view.findViewById(R.id.reply_arrow);
            deleteButton = view.findViewById(R.id.delete_button);
        }

        public void bind(Comment reply) {
            contentTextView.setText(reply.getContent());
            timestampTextView.setText(DateUtils.formatTimestamp(reply.getTimestamp()));
            authorName.setText(reply.getAuthorName());
            authorLabel.setVisibility(reply.isAuthor() ? View.VISIBLE : View.GONE);
            replyArrow.setVisibility(View.VISIBLE);
            deleteButton.setVisibility(reply.getAuthorId().equals(currentUserId) ? View.VISIBLE : View.GONE);
            deleteButton.setOnClickListener(v -> showDeleteConfirmationDialog(reply));
        }

        private void showDeleteConfirmationDialog(Comment comment) {
            AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext());
            builder.setMessage("댓글을 삭제하시겠습니까?");
            builder.setPositiveButton("삭제", (dialog, which) -> deleteComment(comment));
            builder.setNegativeButton("취소", (dialog, which) -> dialog.dismiss());
            builder.show();
        }

        private void deleteComment(Comment comment) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection(comment.getType()).document(documentId).collection("posts")
                    .document(comment.getPostId()).collection("comments")
                    .document(comment.getParentCommentId()).collection("replies")
                    .document(comment.getCommentId())
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        int position = getAdapterPosition();
                        comments.remove(position);
                        notifyItemRemoved(position);
                    });
        }
    }
}
