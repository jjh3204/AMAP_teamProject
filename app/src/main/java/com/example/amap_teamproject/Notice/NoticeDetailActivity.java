package com.example.amap_teamproject.Notice;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.amap_teamproject.R;
import com.example.amap_teamproject.databinding.ActivityNoticeDetailBinding;
import com.example.amap_teamproject.utils.DateUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class NoticeDetailActivity extends AppCompatActivity {
    private ActivityNoticeDetailBinding binding;
    private TextView noticeDetailTitle;
    private TextView noticeDetailTimestamp;
    private TextView noticeDetailContent;
    private FirebaseFirestore db;
    private String currentUserId;
    private String documentId;
    private boolean isAdmin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNoticeDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            binding.toolbar.getNavigationIcon().setTint(getResources().getColor(android.R.color.black));
        }

        db = FirebaseFirestore.getInstance();
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        noticeDetailTitle = binding.noticeTitle;
        noticeDetailContent = binding.noticeContent;
        noticeDetailTimestamp = binding.noticeTimestamp;

        String title = getIntent().getStringExtra("title");
        String content = getIntent().getStringExtra("content");
        long timestamp = getIntent().getLongExtra("timestamp", 0);
        documentId = getIntent().getStringExtra("documentId");

        noticeDetailTitle.setText(title);
        noticeDetailContent.setText(content);
        noticeDetailTimestamp.setText(DateUtils.formatTimestamp(timestamp));

        checkIfUserIsAdmin();
    }

    private void checkIfUserIsAdmin() {
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
                        invalidateOptionsMenu();
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_add_career, menu);

        if (!isAdmin) {
            menu.findItem(R.id.action_delete).setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else if (item.getItemId() == R.id.action_delete) {
            showDeleteConfirmationDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDeleteConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("알림")
                .setMessage("이 항목을 삭제하시겠습니까?")
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteNotice();
                    }
                })
                .setNegativeButton("취소", null)
                .show();
    }

    private void deleteNotice() {
        if (documentId != null && !documentId.isEmpty()) {
            db.collection("notices").document(documentId)
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "공지사항이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "공지사항 삭제에 실패했습니다.", Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(this, "잘못된 공지사항 정보입니다.", Toast.LENGTH_SHORT).show();
        }
    }
}