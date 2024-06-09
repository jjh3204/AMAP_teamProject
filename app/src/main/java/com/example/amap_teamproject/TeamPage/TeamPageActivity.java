package com.example.amap_teamproject.TeamPage;

import static android.service.controls.ControlsProviderService.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.amap_teamproject.databinding.ActivityTeampageBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class TeamPageActivity extends AppCompatActivity {
    private ActivityTeampageBinding binding;
    private RecyclerView postRecyclerView;
    private PostAdapter postAdapter;
    private List<Post> postList;
    private FirebaseFirestore db;
    private String title;
    private String documentId;
    private String type;
    private TextView emptyMessageTextView;
    private ListenerRegistration postListListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityTeampageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        type = getIntent().getStringExtra("type");
        title = getIntent().getStringExtra("DOCUMENT_ID");

        postRecyclerView = binding.postRecycler;
        emptyMessageTextView = binding.emptyMessageTextView;
        FloatingActionButton fab = binding.fab;

        postRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        postList = new ArrayList<>();
        postAdapter = new PostAdapter(postList, this::onPostClick);
        postRecyclerView.setAdapter(postAdapter);

        db = FirebaseFirestore.getInstance();

        Query query = db.collection(type).whereEqualTo("title", title);
        query.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (!task.getResult().isEmpty()) {
                            // 첫 번째 매칭된 문서를 가져옵니다.
                            DocumentSnapshot document = task.getResult().getDocuments().get(0);
                            documentId = document.getId();
                            loadPosts();
                        }
                    }
                });

        fab.setOnClickListener(v -> {
            Intent intent = new Intent(TeamPageActivity.this, AddPostActivity.class);
            intent.putExtra("type", type);
            intent.putExtra("DOCUMENT_ID", documentId);
            startActivity(intent);
        });
    }

    private void loadPosts() {
        db.collection(type).document(documentId).collection("posts")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(postsTask -> {
                    if (postsTask.isSuccessful()) {
                        QuerySnapshot snapshot = postsTask.getResult();
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
        intent.putExtra("type", type);
        intent.putExtra("DOCUMENT_ID", documentId);
        startActivity(intent);
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (type == null || documentId == null) {
            return;
        }
        attachPostListListener();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (postListListener != null) {
            postListListener.remove();
        }
    }

    private void attachPostListListener() {
        if (type == null || documentId == null) {
            return;
        }

        postListListener = db.collection(type).document(documentId).collection("posts")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        return;
                    }

                    if (snapshots != null && !snapshots.isEmpty()) {
                        postList.clear();
                        for (DocumentSnapshot doc : snapshots.getDocuments()) {
                            Post post = doc.toObject(Post.class);
                            if (post != null) {
                                postList.add(post);
                            }
                        }
                        postAdapter.notifyDataSetChanged();
                        emptyMessageTextView.setVisibility(postList.isEmpty() ? View.VISIBLE : View.GONE);
                    } else {
                        postList.clear();
                        postAdapter.notifyDataSetChanged();
                        emptyMessageTextView.setVisibility(View.VISIBLE);
                    }
                });
    }
}
