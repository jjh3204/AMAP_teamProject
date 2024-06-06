package com.example.amap_teamproject.menu;

import android.graphics.Color;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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
        TextView actPeriod = findViewById(R.id.detail_act_period); // 활동 기간을 표시할 TextView 추가
        TextView participants = findViewById(R.id.detail_participants);
        TextView homepage = findViewById(R.id.detail_homepage);
        TextView detail = findViewById(R.id.detail_detail);
        TextView actField = findViewById(R.id.detail_act_field);
        TextView actRegion = findViewById(R.id.detail_act_region);
        TextView interestField = findViewById(R.id.detail_interest_field);
        TextView ddayStatus = findViewById(R.id.detail_dday); // D-day 텍스트뷰 추가
        TextView hitCount = findViewById(R.id.detail_hit_count); // 조회수 텍스트뷰 추가

        if (getIntent().hasExtra(EXTRA_ACTIVITY)) {
            Activity activity = getIntent().getParcelableExtra(EXTRA_ACTIVITY);
            if (activity != null) {
                title.setText(activity.getTitle());

                setBoldText(organization, "주최기관: ", activity.getOrganization());
                setBoldText(period, "지원 기간: ", activity.getSubPeriod());
                setBoldText(actPeriod, "활동 기간: ", activity.getActPeriod()); // 활동 기간 설정
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

                // D-day 계산 및 설정
                setDdayStatus(ddayStatus, activity.getSubPeriod());

                // 조회수 표시 및 증가
                incrementHitCount(activity, hitCount);
            }
        }
    }

    private void setBoldText(TextView textView, String boldText, String normalText) {
        SpannableString spannableString = new SpannableString(boldText + normalText);
        spannableString.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, boldText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(spannableString);
    }

    private void setDdayStatus(TextView ddayStatusView, String subPeriod) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yy.MM.dd", Locale.getDefault());
        String[] dates = subPeriod.split(" ~ ");
        if (dates.length < 2) return;

        try {
            Date startDate = dateFormat.parse(dates[0]);
            Date endDate = dateFormat.parse(dates[1]);
            Date currentDate = new Date();

            if (currentDate.before(startDate)) {
                long diff = startDate.getTime() - currentDate.getTime();
                long daysLeft = (diff / (1000 * 60 * 60 * 24)) + 1;
                ddayStatusView.setText("D-" + daysLeft);
            } else if (currentDate.after(startDate) && currentDate.before(endDate)) {
                long diff = endDate.getTime() - currentDate.getTime();
                long daysLeft = (diff / (1000 * 60 * 60 * 24)) + 1;
                ddayStatusView.setText("마감일까지 " + daysLeft + "일");
            } else {
                ddayStatusView.setText("마감됨");
            }
            ddayStatusView.setTextColor(Color.GRAY); // D-day 텍스트 색상 회색으로 설정
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void incrementHitCount(Activity activity, TextView hitCountView) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Query query = db.collection("activities").whereEqualTo("title", activity.getTitle());

        query.get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (!queryDocumentSnapshots.isEmpty()) {
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    DocumentReference docRef = document.getReference();
                    long hits = document.getLong("hits") != null ? document.getLong("hits") : 0;
                    hits++;
                    docRef.update("hits", hits);
                    hitCountView.setText("조회수: " + hits);
                    activity.setHits((int) hits); // 업데이트된 조회수 설정
                    break; // 제목은 유니크하다고 가정하고 첫 번째 매치에서 종료
                }
            }
        });
    }
}