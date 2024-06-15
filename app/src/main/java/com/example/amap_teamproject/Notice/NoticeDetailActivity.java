package com.example.amap_teamproject.Notice;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.amap_teamproject.databinding.ActivityNoticeDetailBinding;
import com.example.amap_teamproject.utils.DateUtils;

public class NoticeDetailActivity extends AppCompatActivity {
    private ActivityNoticeDetailBinding binding;
    private TextView noticeDetailTitle;
    private TextView noticeDetailTimestamp;
    private TextView noticeDetailContent;

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

        noticeDetailTitle = binding.noticeTitle;
        noticeDetailContent = binding.noticeContent;
        noticeDetailTimestamp = binding.noticeTimestamp;

        String title = getIntent().getStringExtra("title");
        String content = getIntent().getStringExtra("content");
        long timestamp = getIntent().getLongExtra("timestamp", 0);

        noticeDetailTitle.setText(title);
        noticeDetailContent.setText(content);
        noticeDetailTimestamp.setText(DateUtils.formatTimestamp(timestamp));
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