package com.example.amap_teamproject.menu;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.amap_teamproject.R;

public class ActivityDetailActivity extends AppCompatActivity {

    public static final String EXTRA_ACTIVITY = "activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_detail);

        TextView title = findViewById(R.id.detail_title);
        ImageView image = findViewById(R.id.detail_image);
        TextView organization = findViewById(R.id.detail_organization);
        TextView period = findViewById(R.id.detail_period);
        TextView participants = findViewById(R.id.detail_participants);
        TextView homepage = findViewById(R.id.detail_homepage);
        TextView detail = findViewById(R.id.detail_detail);

        if (getIntent().hasExtra(EXTRA_ACTIVITY)) {
            Activity activity = getIntent().getParcelableExtra(EXTRA_ACTIVITY);
            if (activity != null) {
                title.setText(activity.getTitle());
                organization.setText(activity.getOrganization());
                period.setText(activity.getActPeriod());
                participants.setText(activity.getParticipants());
                homepage.setText(activity.getHomepage().toString());
                detail.setText(activity.getDetail());

                Glide.with(this)
                        .load(activity.getPosterUrl())
                        .into(image);
            }
        }
    }
}
