package com.example.amap_teamproject.TeamPage;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.amap_teamproject.databinding.ActivityTeampageBinding;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
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
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityTeampageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            binding.toolbar.getNavigationIcon().setTint(getResources().getColor(android.R.color.black));
        }

        type = getIntent().getStringExtra("type");
        title = getIntent().getStringExtra("DOCUMENT_ID");

        postRecyclerView = binding.postRecycler;
        emptyMessageTextView = binding.emptyMessageTextView;
        swipeRefreshLayout = binding.swipeRefreshLayout;
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
        swipeRefreshLayout.setOnRefreshListener(this::loadPosts);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // 뒤로 가기 버튼을 누르면 현재 액티비티를 종료
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadPosts() {
        swipeRefreshLayout.setRefreshing(true);
        db.collection(type).document(documentId).collection("posts")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(postsTask -> {
                    if (postsTask.isSuccessful()) {
                        QuerySnapshot snapshot = postsTask.getResult();
                        postList.clear();
                        List<Task<Void>> commentCountTasks = new ArrayList<>();

                        for (DocumentSnapshot doc : snapshot.getDocuments()) {
                            Post post = doc.toObject(Post.class);
                            if (post != null) {
                                Task<Void> commentCountTask = calculateCommentCount(post).continueWith(task -> {
                                    postList.add(post);
                                    return null;
                                });
                                commentCountTasks.add(commentCountTask);
                            }
                        }

                        Tasks.whenAllComplete(commentCountTasks).addOnCompleteListener(task -> {
                            postList.sort((p1, p2) -> Long.compare(p2.getTimestamp(), p1.getTimestamp())); // Sorting by timestamp
                            postAdapter.notifyDataSetChanged();
                            emptyMessageTextView.setVisibility(postList.isEmpty() ? View.VISIBLE : View.GONE);
                            swipeRefreshLayout.setRefreshing(false);
                        });
                    } else {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
    }

    private Task<Void> calculateCommentCount(Post post) {
        return db.collection(type).document(documentId)
                .collection("posts").document(post.getId()).collection("comments")
                .get()
                .continueWithTask(commentTask -> {
                    if (!commentTask.isSuccessful() || commentTask.getResult() == null) {
                        return Tasks.forResult(null);
                    }

                    List<DocumentSnapshot> commentDocs = commentTask.getResult().getDocuments();
                    final int[] commentCount = {commentDocs.size()};
                    List<Task<QuerySnapshot>> replyTasks = new ArrayList<>();

                    for (DocumentSnapshot commentDoc : commentDocs) {
                        Task<QuerySnapshot> replyTask = db.collection(type).document(documentId)
                                .collection("posts").document(post.getId()).collection("comments")
                                .document(commentDoc.getId()).collection("replies")
                                .get()
                                .addOnCompleteListener(replySnapshotTask -> {
                                    if (replySnapshotTask.isSuccessful()) {
                                        commentCount[0] += replySnapshotTask.getResult().size();
                                    }
                                });
                        replyTasks.add(replyTask);
                    }

                    return Tasks.whenAllComplete(replyTasks).continueWith(replyTask -> {
                        post.setCommentCount(commentCount[0]);
                        return null;
                    });
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

                    if (snapshots != null) {
                        postList.clear();
                        List<Task<Void>> tasks = new ArrayList<>();

                        for (DocumentSnapshot doc : snapshots.getDocuments()) {
                            Post post = doc.toObject(Post.class);
                            if (post != null) {
                                Task<Void> task = calculateCommentCount(post).continueWith(t -> {
                                    postList.add(post);
                                    return null;
                                });
                                tasks.add(task);
                            }
                        }

                        Tasks.whenAllComplete(tasks).addOnCompleteListener(task -> {
                            postList.sort((p1, p2) -> Long.compare(p2.getTimestamp(), p1.getTimestamp())); // Sorting by timestamp
                            postAdapter.notifyDataSetChanged();
                            emptyMessageTextView.setVisibility(postList.isEmpty() ? View.VISIBLE : View.GONE);
                        });
                    } else {
                        postList.clear();
                        postAdapter.notifyDataSetChanged();
                        emptyMessageTextView.setVisibility(View.VISIBLE);
                    }
                });
    }
}
