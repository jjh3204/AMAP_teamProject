package com.example.amap_teamproject.Notice;


import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.amap_teamproject.databinding.ActivityNoticeBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class NoticeActivity extends AppCompatActivity {
    private boolean isAdmin = false;
    private ActivityNoticeBinding binding;
    private RecyclerView recyclerView;
    private NoticeAdapter noticeAdapter;
    private List<Notice> noticeList;
    private FirebaseFirestore db;
    private String currentUserId;
    private ListenerRegistration noticeListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityNoticeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            binding.toolbar.getNavigationIcon().setTint(getResources().getColor(android.R.color.black));
        }

        recyclerView = binding.noticeRecyclerview;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        noticeList = new ArrayList<>();
        noticeAdapter = new NoticeAdapter(noticeList);
        recyclerView.setAdapter(noticeAdapter);

        db = FirebaseFirestore.getInstance();
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        loadNotices();
        checkAdminStatus();
    }

    @Override
    protected void onResume() {
        super.onResume();
        attachNoticeListener();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (noticeListener != null) {
            noticeListener.remove();
        }
    }

    private void checkAdminStatus() {
        db.collection("users").document(currentUserId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String userName = documentSnapshot.getString("name");
                        if ("관리자".equals(userName)) {
                            isAdmin = true;
                        } else {
                            isAdmin = false;
                        }
                        configureUI(isAdmin);
                    }
                })
                .addOnFailureListener(e -> {
                    finish();
                });
    }

    private void configureUI(boolean isAdmin) {
        FloatingActionButton fab = binding.fab;
        if (isAdmin) {
            fab.setVisibility(View.VISIBLE);
            fab.setOnClickListener(v -> {
                Intent intent = new Intent(NoticeActivity.this, AddNoticeActivity.class);
                startActivity(intent);
            });
        } else {
            fab.setVisibility(View.GONE);
        }
    }

    private void loadNotices() {
        db.collection("notices")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    noticeList.clear();
                    noticeList.addAll(queryDocumentSnapshots.toObjects(Notice.class));
                    noticeAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "공지사항을 불러오지 못했습니다", Toast.LENGTH_SHORT).show();
                });
    }

    private void attachNoticeListener() {
        noticeListener = db.collection("notices")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            return;
                        }
                        if (snapshots != null) {
                            noticeList.clear();
                            for (DocumentSnapshot doc : snapshots.getDocuments()) {
                                Notice notice = doc.toObject(Notice.class);
                                if (notice != null) {
                                    noticeList.add(notice);
                                }
                            }
                            noticeAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}