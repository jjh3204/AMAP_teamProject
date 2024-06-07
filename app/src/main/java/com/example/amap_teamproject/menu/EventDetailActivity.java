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

public class EventDetailActivity extends AppCompatActivity {

    public static final String EXTRA_EVENT = "event";
    private static final String TAG = "EventDetailActivity";

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
        TextView ddayStatus = findViewById(R.id.detail_dday); // D-day 텍스트뷰
        TextView hitCount = findViewById(R.id.detail_hit_count); // 조회수 텍스트뷰 추가
        TextView likeCount = findViewById(R.id.detail_like_count); // 좋아요 텍스트뷰 추가

        if (getIntent().hasExtra(EXTRA_EVENT)) {
            Event event = getIntent().getParcelableExtra(EXTRA_EVENT);
            if (event != null) {
                title.setText(event.getTitle());

                setBoldText(organization, "주최기관: ", event.getOrganization());
                setBoldText(period, "지원 기간: ", event.getSubPeriod());
                setBoldText(participants, "참가대상: ", event.getParticipants());
                setBoldText(awardScale, "시상 내역: ", event.getAwardScale());

                if (event.getContestField() != null) {
                    setBoldText(contestField, "공모 분야: ", String.join(", ", event.getContestField()));
                } else {
                    setBoldText(contestField, "공모 분야: ", "N/A");
                }

                if (event.getHomepage() != null && !event.getHomepage().isEmpty()) {
                    setBoldText(homepage, "홈페이지: ", event.getHomepage().get(0));
                } else {
                    setBoldText(homepage, "홈페이지: ", "N/A");
                }

                // Replace \n with actual newlines
                setBoldText(detail, "상세정보:\n", event.getDetail().replaceAll("\\\\n", "\n"));

                Log.d(TAG, "Loading image from URL: " + event.getImgSrc());
                if (event.getImgSrc() != null && !event.getImgSrc().isEmpty()) {
                    Glide.with(this)
                            .load(event.getImgSrc())
                            .apply(new RequestOptions().error(R.drawable.default_image).diskCacheStrategy(DiskCacheStrategy.ALL))
                            .into(image);
                } else {
                    image.setImageResource(R.drawable.default_image); // 기본 이미지 설정
                }

                // D-day 계산 및 설정
                setDdayStatus(ddayStatus, event.getSubPeriod());

                // 조회수 표시
                hitCount.setText("조회수: " + event.getHits());

                // 좋아요 수 표시
                likeCount.setText("찜: " + event.getLikes());

                // 조회수 증가
                incrementHitCount(event, hitCount);
            }
        }
    }

    private void setBoldText(TextView textView, String boldText, String normalText) {
        SpannableString spannableString = new SpannableString(boldText + normalText);
        spannableString.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, boldText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(spannableString);
    }

    private void setDdayStatus(TextView ddayStatusView, String subPeriod) {
        if (subPeriod == null) return;

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
                ddayStatusView.setText("접수시작까지 " + daysLeft + "일");
            } else if (currentDate.after(startDate) && currentDate.before(endDate)) {
                long diff = endDate.getTime() - currentDate.getTime();
                long daysLeft = (diff / (1000 * 60 * 60 * 24)) + 1;
                ddayStatusView.setText("마감까지 " + daysLeft + "일");
            } else {
                ddayStatusView.setText("마감됨");
            }
            ddayStatusView.setTextColor(Color.GRAY); // D-day 텍스트 색상 회색으로 설정
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void incrementHitCount(Event event, TextView hitCountView) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Query query = db.collection("contests").whereEqualTo("title", event.getTitle());

        query.get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (!queryDocumentSnapshots.isEmpty()) {
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    DocumentReference docRef = document.getReference();
                    long hits = document.getLong("hits") != null ? document.getLong("hits") : 0;
                    hits++;
                    docRef.update("hits", hits);
                    hitCountView.setText("조회수: " + hits);
                    event.setHits((int) hits); // 업데이트된 조회수 설정
                    break; // 제목은 유니크하다고 가정하고 첫 번째 매치에서 종료
                }
            }
        });
    }
}

