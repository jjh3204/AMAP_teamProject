package com.example.amap_teamproject.menu;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.amap_teamproject.R;

public class DetailActivity extends AppCompatActivity {

    public static final String EXTRA_EVENT = "event";
    public static final String EXTRA_ACTIVITY = "activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // Event 객체를 처리
        if (getIntent().hasExtra(EXTRA_EVENT)) {
            Event event = getIntent().getParcelableExtra(EXTRA_EVENT);
            if (event != null) {
                ((TextView) findViewById(R.id.event_title)).setText(event.getTitle());
                ((TextView) findViewById(R.id.event_organization)).setText(event.getOrganization());
                ((TextView) findViewById(R.id.event_deadline)).setText(event.getDeadline());
                ((TextView) findViewById(R.id.event_description)).setText(event.getDescription());

                ImageView imageView = findViewById(R.id.event_image);
                Glide.with(this)
                        .load(event.getPosterUrl())
                        .into(imageView);
            }
        }
        // Activity 객체를 처리
        else if (getIntent().hasExtra(EXTRA_ACTIVITY)) {
            Activity activity = getIntent().getParcelableExtra(EXTRA_ACTIVITY);
            if (activity != null) {
                ((TextView) findViewById(R.id.event_title)).setText(activity.getTitle());
                ((TextView) findViewById(R.id.event_organization)).setText(activity.getOrganization());
                ((TextView) findViewById(R.id.event_deadline)).setText(activity.getActPeriod());
                ((TextView) findViewById(R.id.event_description)).setText(activity.getDetail());

                ImageView imageView = findViewById(R.id.event_image);
                Glide.with(this)
                        .load(activity.getPosterUrl())
                        .into(imageView);
            }
        }
    }
}



