package com.example.amap_teamproject.menu;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.amap_teamproject.R;

public class DetailActivity extends AppCompatActivity {

    public static final String EXTRA_EVENT = "event";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Event event = getIntent().getParcelableExtra(EXTRA_EVENT);

        if (event != null) {
            ((TextView) findViewById(R.id.event_title)).setText(event.getTitle());
            ((TextView) findViewById(R.id.event_organization)).setText(event.getOrganization());
            ((TextView) findViewById(R.id.event_deadline)).setText(event.getDeadline());
            ((TextView) findViewById(R.id.event_description)).setText(event.getDescription());
            ((ImageView) findViewById(R.id.event_image)).setImageResource(event.getImageResourceId());
        }
    }
}
