package com.example.amap_teamproject.TeamPage;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.amap_teamproject.databinding.ActivityPostItemBinding;
import com.example.amap_teamproject.R;

import com.example.amap_teamproject.utils.DateUtils;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class PostItemActivity extends AppCompatActivity {

    private TextView postTitle;
    private TextView postContent;
    private TextView postTimestamp;
    private RecyclerView commentRecyclerView;
    private EditText commentEditText;
    private Button postCommentButton;
    private CommentAdapter commentAdapter;
    private List<Comment> commentList;
    private FirebaseFirestore db;
    private String postId;
    private String documentId;
    private String currentUserId;
    private String postAuthorId;
    private ActivityPostItemBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityPostItemBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        postTitle = binding.postTitle;
        postTimestamp = binding.postTimestamp;
        postContent = binding.postContent;
        commentRecyclerView = binding.commentRecyclerview;

        db = FirebaseFirestore.getInstance();
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        postId = getIntent().getStringExtra("POST_ID");
        documentId = getIntent().getStringExtra("DOCUMENT_ID");

        commentList = new ArrayList<>();
        commentAdapter = new CommentAdapter(commentList, currentUserId, documentId);
        commentRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        commentRecyclerView.setAdapter(commentAdapter);

        loadPostDetails();
        loadComments();

        postCommentButton.setOnClickListener(v -> postComment());
    }

    private void loadPostDetails() {
        db.collection("activities").document(documentId).collection("posts").document(postId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    Post post = documentSnapshot.toObject(Post.class);
                    if (post != null) {
                        postTitle.setText(post.getTitle());
                        postContent.setText(post.getContent());
                        postTimestamp.setText(DateUtils.formatTimestamp(post.getTimestamp()));
                        postAuthorId = post.getAuthorId();

                        // Show delete button if the current user is the post author
                        if (currentUserId.equals(postAuthorId)) {
                            // Code to show delete button
                        }
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "정보를 불러오는데 실패했습니다", Toast.LENGTH_SHORT).show());;
    }

    private void loadComments() {
        db.collection("activities").document(documentId).collection("posts").document(postId).collection("comments")
                .orderBy("timestamp")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot snapshot = task.getResult();
                        commentList.clear();
                        for (DocumentSnapshot doc : snapshot.getDocuments()) {
                            Comment comment = doc.toObject(Comment.class);
                            if (comment.isSecret()) {
                                if (comment.getAuthorId().equals(currentUserId) || postAuthorId.equals(currentUserId)) {
                                    commentList.add(comment);
                                }
                                else{
                                    comment.setContent("비밀 댓글입니다.");
                                    commentList.add(comment);
                                }
                            } else {
                                commentList.add(comment);
                            }
                        }
                        commentAdapter.notifyDataSetChanged();
                    }
                });
    }

    // 댓글 작성
    private void postComment() {
        String content = commentEditText.getText().toString().trim();
        if (!content.isEmpty()) {
            long timestamp = System.currentTimeMillis();
            boolean isAuthor = currentUserId.equals(postAuthorId);

            Comment comment = new Comment(content, timestamp, currentUserId, false, isAuthor, postId, null);
            db.collection("activities").document(documentId).collection("posts").document(postId).collection("comments")
                    .add(comment)
                    .addOnSuccessListener(documentReference -> {
                        commentEditText.setText("");
                        loadComments();
                    });
        }
    }
}