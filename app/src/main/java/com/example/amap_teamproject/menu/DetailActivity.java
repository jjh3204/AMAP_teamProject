package com.example.amap_teamproject.menu;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.amap_teamproject.R;

public class DetailActivity extends AppCompatActivity {

    public static final String EXTRA_EVENT = "event";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Event event = getIntent().getParcelableExtra(EXTRA_EVENT);

        if (event != null) {
            ((TextView) findViewById(R.id.event_title)).setText(event.getTitle());
            ((TextView) findViewById(R.id.event_organization)).setText(event.getOrganization());
            ((TextView) findViewById(R.id.event_deadline)).setText(event.getDeadline());
            ((TextView) findViewById(R.id.event_description)).setText(event.getDescription());

            // 이미지 리소스 로드를 Glide로 변경
            ImageView imageView = findViewById(R.id.event_image);
            Glide.with(this)
                    .load(event.getPosterUrl())
                    .into(imageView);
        }
    }
}

