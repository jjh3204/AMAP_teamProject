package com.example.amap_teamproject.TeamPage;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import android.widget.EditText;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.amap_teamproject.databinding.ActivityAddPostBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AddPostActivity extends AppCompatActivity {

    private EditText titleEditText;
    private EditText contentEditText;
    private Button postButton;
    private FirebaseFirestore db;
    private String type;
    private String documentId;
    private String authorId;
    private ActivityAddPostBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAddPostBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        titleEditText = binding.postTitleEditText;
        contentEditText = binding.postContentEditText;
        postButton = binding.postButton;

        db = FirebaseFirestore.getInstance();
        authorId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        type = getIntent().getStringExtra("type");
        documentId = getIntent().getStringExtra("DOCUMENT_ID");

        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = titleEditText.getText().toString().trim();
                String content = contentEditText.getText().toString().trim();
                long timestamp = System.currentTimeMillis();

                if (!title.isEmpty() && !content.isEmpty()) {
                    createPost(title, content, timestamp);
                } else {
                    Toast.makeText(AddPostActivity.this, "제목과 내용을 작성해주세요", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void createPost(String title, String content, long timestamp) {
        Map<String, Object> newPost = new HashMap<>();

        newPost.put("title", title);
        newPost.put("content", content);
        newPost.put("timestamp", timestamp);
        newPost.put("authorId", authorId);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(type).document(documentId).collection("posts")
                .add(newPost)
                .addOnSuccessListener(documentReference -> {
                    String postId = documentReference.getId();
                    newPost.put("id", postId); // Post ID 추가

                    documentReference.update(newPost)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(AddPostActivity.this, "글을 작성하였습니다", Toast.LENGTH_SHORT).show();
                                finish(); // Post 생성 후 액티비티 종료
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(AddPostActivity.this, "오류가 발생하였습니다", Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(AddPostActivity.this, "오류가 발생하였습니다", Toast.LENGTH_SHORT).show();
                });
    }
}