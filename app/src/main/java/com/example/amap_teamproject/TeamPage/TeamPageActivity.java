package com.example.amap_teamproject.TeamPage;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.amap_teamproject.databinding.ActivityTeampageBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class TeamPageActivity extends AppCompatActivity {
    private ActivityTeampageBinding binding;
    private RecyclerView postRecyclerView;
    private PostAdapter postAdapter;
    private List<Post> postList;
    private FirebaseFirestore db;
    private String documentId;
    private TextView emptyMessageTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityTeampageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        documentId = getIntent().getStringExtra("DOCUMENT_ID");

        postRecyclerView = binding.postRecycler;
        emptyMessageTextView = binding.emptyMessageTextView;
        FloatingActionButton fab = binding.fab;

        postRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        postList = new ArrayList<>();
        postAdapter = new PostAdapter(postList, this::onPostClick);
        postRecyclerView.setAdapter(postAdapter);

        db = FirebaseFirestore.getInstance();
        loadPosts();

        fab.setOnClickListener(v -> {
            Intent intent = new Intent(TeamPageActivity.this, AddPostActivity.class);
            intent.putExtra("DOCUMENT_ID", documentId);
            startActivity(intent);
        });
    }

    private void loadPosts() {
        db.collection("activities").document(documentId).collection("posts")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot snapshot = task.getResult();
                        postList.clear();
                        postList.addAll(snapshot.toObjects(Post.class));
                        postAdapter.notifyDataSetChanged();
                        emptyMessageTextView.setVisibility(postList.isEmpty() ? View.VISIBLE : View.GONE);
                    }
                });
    }

    private void onPostClick(Post post) {
        Intent intent = new Intent(TeamPageActivity.this, PostItemActivity.class);
        intent.putExtra("POST_ID", post.getId());
        intent.putExtra("DOCUMENT_ID", documentId);
        startActivity(intent);
    }

    @Override
    protected void onResume(){
        super.onResume();

    }
}
