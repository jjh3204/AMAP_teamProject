package com.example.amap_teamproject.Notice;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.amap_teamproject.R;
import com.example.amap_teamproject.databinding.ActivityAddNoticeBinding;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddNoticeActivity extends AppCompatActivity {
    private ActivityAddNoticeBinding binding;
    private EditText noticeTitleEditText;
    private EditText noticeContentEditText;
    private Button postNoticeButton;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddNoticeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            binding.toolbar.getNavigationIcon().setTint(getResources().getColor(android.R.color.black));
        }

        noticeTitleEditText = binding.noticeTitleEditText;
        noticeContentEditText = binding.noticeContentEditText;
        postNoticeButton = binding.postNoticeButton;
        db = FirebaseFirestore.getInstance();

        postNoticeButton.setOnClickListener(v -> postNotice());
    }

    private void postNotice() {
        String title = noticeTitleEditText.getText().toString().trim();
        String content = noticeContentEditText.getText().toString().trim();

        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(content)) {
            Toast.makeText(this, "제목과 내용을 모두 입력하세요", Toast.LENGTH_SHORT).show();
            return;
        }

        DocumentReference newNoticeRef = db.collection("notices").document();
        String documentId = newNoticeRef.getId();

        Map<String, Object> notice = new HashMap<>();
        notice.put("title", title);
        notice.put("content", content);
        notice.put("timestamp", System.currentTimeMillis());
        notice.put("documentId", documentId);

        newNoticeRef.set(notice)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "공지사항이 등록되었습니다", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "공지사항 등록에 실패했습니다", Toast.LENGTH_SHORT).show());
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