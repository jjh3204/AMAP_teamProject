package com.example.amap_teamproject.TeamPage;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.amap_teamproject.databinding.ActivityPostItemBinding;
import com.example.amap_teamproject.R;

import com.example.amap_teamproject.utils.DateUtils;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import java.util.ArrayList;
import java.util.List;

public class PostItemActivity extends AppCompatActivity {

    private TextView postTitle;
    private TextView postContent;
    private TextView postTimestamp;
    private RecyclerView commentRecyclerView;
    private EditText commentEditText;
    private Button postCommentButton;
    private Button deleteButton;
    private CommentAdapter commentAdapter;
    private List<Comment> commentList;
    private FirebaseFirestore db;
    private String type;
    private String postId;
    private String documentId;
    private String currentUserId;
    private String postAuthorId;
    private ActivityPostItemBinding binding;
    private NestedScrollView scrollView;

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
        commentEditText = binding.commentEditText;
        postCommentButton = binding.postCommentButton;
        deleteButton = binding.deleteButton;
        scrollView = binding.scrollview;

        db = FirebaseFirestore.getInstance();
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        type = getIntent().getStringExtra("type");
        postId = getIntent().getStringExtra("POST_ID");
        documentId = getIntent().getStringExtra("DOCUMENT_ID");
        if (type == null || type.isEmpty()) {
            Toast.makeText(this, "Invalid type", Toast.LENGTH_SHORT).show();
            finish(); // 종료 액티비티
            return;
        }

        commentList = new ArrayList<>();
        commentAdapter = new CommentAdapter(commentList, currentUserId, documentId, commentRecyclerView);
        commentRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        commentRecyclerView.setAdapter(commentAdapter);

        loadPostDetails();
        loadComments();

        deleteButton.setOnClickListener(v -> postDelete());
        postCommentButton.setOnClickListener(v -> postComment());
    }

    private void loadPostDetails() {
        db.collection(type).document(documentId).collection("posts").document(postId)
                .get()
                .addOnSuccessListener(postSnapshot -> {
                    Post post = postSnapshot.toObject(Post.class);
                    if (post != null) {
                        postTitle.setText(post.getTitle());
                        postContent.setText(post.getContent());
                        postTimestamp.setText(DateUtils.formatTimestamp(post.getTimestamp()));
                        postAuthorId = post.getAuthorId();

                        db.collection("users").document(postAuthorId).get()
                                .addOnSuccessListener(documentSnapshot -> {
                                            if (documentSnapshot.exists()) {
                                                String authorName = documentSnapshot.getString("name");
                                                binding.postAuthor.setText(authorName);
                                            }
                                        });

                        // 현재 유저와 글 작성자가 같으면 삭제버튼이 보이게 함
                        if (currentUserId.equals(postAuthorId)) {
                            deleteButton.setVisibility(View.VISIBLE);
                        }
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "정보를 불러오는데 실패했습니다", Toast.LENGTH_SHORT).show());
        ;
    }

    private void loadComments() {
        db.collection(type).document(documentId).collection("posts").document(postId).collection("comments")
                .orderBy("timestamp")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot snapshot = task.getResult();
                        commentList.clear();
                        List<Task<QuerySnapshot>> replyTasks = new ArrayList<>();

                        for (DocumentSnapshot doc : snapshot.getDocuments()) {
                            Comment comment = doc.toObject(Comment.class);
                            if (comment != null) {
                                commentList.add(comment);
                                Task<QuerySnapshot> replyTask = db.collection(type).document(documentId)
                                        .collection("posts").document(postId)
                                        .collection("comments").document(comment.getCommentId())
                                        .collection("replies").orderBy("timestamp").get();
                                replyTasks.add(replyTask);
                            }
                        }
                        Tasks.whenAllComplete(replyTasks).addOnCompleteListener(replyTask -> {
                            for (int i = 0; i < replyTasks.size(); i++) {
                                Task<QuerySnapshot> taskSnapshot = replyTasks.get(i);
                                if (taskSnapshot.isSuccessful()) {
                                    List<Comment> replies = new ArrayList<>();
                                    for (DocumentSnapshot replyDoc : taskSnapshot.getResult().getDocuments()) {
                                        Comment reply = replyDoc.toObject(Comment.class);
                                        if (reply != null) {
                                            replies.add(reply);
                                        }
                                    }
                                    commentList.get(i).setReplies(replies);
                                }
                            }
                            commentAdapter.notifyDataSetChanged();
                            scrollView.scrollTo(0, 0);
                        });
                    }
                });
    }

    // 댓글 작성
    private void postComment() {
        String content = commentEditText.getText().toString().trim();
        if (!content.isEmpty()) {
            long timestamp = System.currentTimeMillis();
            boolean isAuthor = currentUserId.equals(postAuthorId);
            String Type = type;

            db.collection("users").document(currentUserId).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String authorName = documentSnapshot.getString("name");

                            DocumentReference newCommentRef = db.collection(type).document(documentId)
                                    .collection("posts").document(postId)
                                    .collection("comments").document();

                            String commentId = newCommentRef.getId();
                            Comment comment = new Comment(Type, content, timestamp, currentUserId, false, isAuthor,
                                    postId, null, commentId, authorName);

                            newCommentRef.set(comment)
                                    .addOnSuccessListener(aVoid -> {
                                        commentEditText.setText("");
                                        loadComments();
                                        hideKeyboard();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(this, "댓글을 작성하는데 실패했습니다", Toast.LENGTH_SHORT).show();
                                    });
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "사용자를 인식하지 못했습니다", Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void postDelete() {
        if (postId != null && !postId.isEmpty() && type != null && documentId != null) {
            db.collection(type).document(documentId).collection("posts").document(postId)
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "게시글이 삭제되었습니다", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "게시글 삭제에 실패했습니다", Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(this, "잘못된 게시글 정보입니다", Toast.LENGTH_SHORT).show();
        }
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(commentEditText.getWindowToken(), 0);
        }
    }
}