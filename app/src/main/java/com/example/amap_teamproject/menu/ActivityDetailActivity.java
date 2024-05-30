package com.example.amap_teamproject.menu;

import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;
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

                setBoldText(organization, "주최기관 또는 주최자: ", activity.getOrganization());
                setBoldText(period, "기간: ", activity.getActPeriod());
                setBoldText(participants, "참가대상: ", activity.getParticipants());

                if (activity.getHomepage() != null && !activity.getHomepage().isEmpty()) {
                    setBoldText(homepage, "공지 URL: ", activity.getHomepage().get(0));
                } else {
                    setBoldText(homepage, "공지 URL: ", "N/A");
                }

                // Replace \n with actual newlines
                setBoldText(detail, "상세정보:\n", activity.getDetail().replaceAll("\\\\n", "\n"));

                if (activity.getPosterUrl() != null && !activity.getPosterUrl().isEmpty()) {
                    Glide.with(this)
                            .load(activity.getPosterUrl())
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
