package com.example.amap_teamproject.SearchPage;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.amap_teamproject.R;

import java.util.Arrays;

public class ContestDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contest_detail);

        TextView titleTextView = findViewById(R.id.detail_title);
        TextView organizationTextView = findViewById(R.id.detail_organization);
        TextView awardScaleTextView = findViewById(R.id.detail_award_scale);
        TextView contestFieldTextView = findViewById(R.id.detail_contest_field);
        TextView detailTextView = findViewById(R.id.detail_detail);
        TextView homepageTextView = findViewById(R.id.detail_homepage);
        ImageView imgView = findViewById(R.id.detail_img_src);
        TextView noticeUrlTextView = findViewById(R.id.detail_notice_url);
        TextView participantsTextView = findViewById(R.id.detail_participants);
        TextView subPeriodTextView = findViewById(R.id.detail_sub_period);
        TextView timestampTextView = findViewById(R.id.detail_timestamp);

        Intent intent = getIntent();

        titleTextView.setText(intent.getStringExtra("title"));
        organizationTextView.setText(intent.getStringExtra("organization"));
        awardScaleTextView.setText(intent.getStringExtra("awardScale"));
        contestFieldTextView.setText(intent.getStringExtra("contestField"));
        detailTextView.setText(intent.getStringExtra("detail"));

        // homepage는 배열 형태이므로, 이를 문자열로 변환하여 표시
        String[] homepageArray = intent.getStringArrayExtra("homepage");
        if (homepageArray != null) {
            homepageTextView.setText(Arrays.toString(homepageArray));
        } else {
            homepageTextView.setText("");
        }

        noticeUrlTextView.setText(intent.getStringExtra("noticeUrl"));
        participantsTextView.setText(intent.getStringExtra("participants"));
        subPeriodTextView.setText(intent.getStringExtra("subPeriod"));
        timestampTextView.setText(intent.getStringExtra("timestamp"));

        String imgSrc = intent.getStringExtra("imgSrc");
        loadImageFromUrl(imgView, imgSrc);
    }

    // Helper method to load image from URL (you can use libraries like Glide or Picasso)
    private void loadImageFromUrl(ImageView imageView, String url) {
        // Assuming you are using Glide
        Glide.with(this)
                .load(url)
                .into(imageView);
    }
}