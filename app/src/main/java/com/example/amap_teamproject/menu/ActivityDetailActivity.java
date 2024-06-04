package com.example.amap_teamproject.menu;

import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.amap_teamproject.R;

public class ActivityDetailActivity extends AppCompatActivity {

    public static final String EXTRA_ACTIVITY = "activity";
    private static final String TAG = "ActivityDetailActivity";

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
        TextView actField = findViewById(R.id.detail_act_field);
        TextView actRegion = findViewById(R.id.detail_act_region);
        TextView interestField = findViewById(R.id.detail_interest_field);

        if (getIntent().hasExtra(EXTRA_ACTIVITY)) {
            Activity activity = getIntent().getParcelableExtra(EXTRA_ACTIVITY);
            if (activity != null) {
                title.setText(activity.getTitle());

                setBoldText(organization, "주최기관 또는 주최자: ", activity.getOrganization());
                setBoldText(period, "기간: ", activity.getSubPeriod());
                setBoldText(participants, "참가대상: ", activity.getParticipants());

                if (activity.getActField() != null) {
                    setBoldText(actField, "활동 분야: ", String.join(", ", activity.getActField()));
                } else {
                    setBoldText(actField, "활동 분야: ", "N/A");
                }

                if (activity.getActRegion() != null) {
                    setBoldText(actRegion, "활동 지역: ", String.join(", ", activity.getActRegion()));
                } else {
                    setBoldText(actRegion, "활동 지역: ", "N/A");
                }

                if (activity.getInterestField() != null) {
                    setBoldText(interestField, "관심 분야: ", String.join(", ", activity.getInterestField()));
                } else {
                    setBoldText(interestField, "관심 분야: ", "N/A");
                }

                if (activity.getHomepage() != null && !activity.getHomepage().isEmpty()) {
                    setBoldText(homepage, "홈페이지: ", activity.getHomepage().get(0));
                } else {
                    setBoldText(homepage, "홈페이지: ", "N/A");
                }

                // Replace \n with actual newlines
                setBoldText(detail, "상세정보:\n", activity.getDetail().replaceAll("\\\\n", "\n"));

                Log.d(TAG, "Loading image from URL: " + activity.getPosterUrl());
                if (activity.getPosterUrl() != null && !activity.getPosterUrl().isEmpty()) {
                    Glide.with(this)
                            .load(activity.getPosterUrl())
                            .apply(new RequestOptions().error(R.drawable.default_image).diskCacheStrategy(DiskCacheStrategy.ALL))
                            .into(image);
                } else {
                    image.setImageResource(R.drawable.default_image); // 기본 이미지 설정
                }
            }
        }
    }

    private void setBoldText(TextView textView, String boldText, String normalText) {
        SpannableString spannableString = new SpannableString(boldText + normalText);
        spannableString.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, boldText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(spannableString);
    }
}
