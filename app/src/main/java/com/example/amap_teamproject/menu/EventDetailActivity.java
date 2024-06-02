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

public class EventDetailActivity extends AppCompatActivity {

    public static final String EXTRA_EVENT = "event";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        TextView title = findViewById(R.id.detail_title);
        ImageView image = findViewById(R.id.detail_image);
        TextView organization = findViewById(R.id.detail_organization);
        TextView period = findViewById(R.id.detail_period);
        TextView participants = findViewById(R.id.detail_participants);
        TextView homepage = findViewById(R.id.detail_homepage);
        TextView detail = findViewById(R.id.detail_detail);
        TextView awardScale = findViewById(R.id.detail_award_scale);
        TextView contestField = findViewById(R.id.detail_contest_field);
        // Removed duplicate noticeUrl TextView

        if (getIntent().hasExtra(EXTRA_EVENT)) {
            Event event = getIntent().getParcelableExtra(EXTRA_EVENT);
            if (event != null) {
                title.setText(event.getTitle());

                setBoldText(organization, "주최기관 또는 주최자: ", event.getOrganization());
                setBoldText(period, "기간: ", event.getSubPeriod());
                setBoldText(participants, "참가대상: ", event.getParticipants());

                if (event.getHomepage() != null && !event.getHomepage().isEmpty()) {
                    setBoldText(homepage, "홈페이지: ", event.getHomepage().get(0));
                } else {
                    setBoldText(homepage, "홈페이지: ", "N/A");
                }

                setBoldText(awardScale, "시상 내역: ", event.getAwardScale());

                if (event.getContestField() != null) {
                    setBoldText(contestField, "공모 분야: ", String.join(", ", event.getContestField()));
                } else {
                    setBoldText(contestField, "공모 분야: ", "N/A");
                }

                setBoldText(detail, "상세정보:\n", event.getDetail().replaceAll("\\\\n", "\n"));

                if (event.getImgSrc() != null && !event.getImgSrc().isEmpty()) {
                    Glide.with(this)
                            .load(event.getImgSrc())
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

