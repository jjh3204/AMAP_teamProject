package com.example.amap_teamproject.menu;

import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.amap_teamproject.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
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

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.getNavigationIcon().setTint(getResources().getColor(android.R.color.black));
        }

        TextView title = findViewById(R.id.detail_title);
        ImageView image = findViewById(R.id.detail_image);
        TextView organization = findViewById(R.id.detail_organization);
        TextView period = findViewById(R.id.detail_period);
        TextView actPeriod = findViewById(R.id.detail_act_period);
        TextView participants = findViewById(R.id.detail_participants);
        TextView homepage = findViewById(R.id.detail_homepage);
        TextView detail = findViewById(R.id.detail_detail);
        TextView actField = findViewById(R.id.detail_act_field);
        TextView actRegion = findViewById(R.id.detail_act_region);
        TextView interestField = findViewById(R.id.detail_interest_field);
        TextView ddayStatus = findViewById(R.id.detail_dday);
        TextView hitCount = findViewById(R.id.detail_hit_count);
        TextView likeCount = findViewById(R.id.detail_like_count);

        Activity activity = getIntent().getParcelableExtra(EXTRA_ACTIVITY);
        if (activity == null) {
            Log.e(TAG, "Activity data is missing");
            finish();
            return;
        }

        title.setText(activity.getTitle());
        setBoldText(organization, "주최기관: ", activity.getOrganization());
        setBoldText(period, "지원 기간: ", activity.getSubPeriod());
        setBoldText(actPeriod, "활동 기간: ", activity.getActPeriod());
        setBoldText(participants, "참가대상: ", activity.getParticipants());

        setBoldText(actField, "활동 분야: ", activity.getActField() != null ? String.join(", ", activity.getActField()) : "N/A");
        setBoldText(actRegion, "활동 지역: ", activity.getActRegion() != null ? String.join(", ", activity.getActRegion()) : "N/A");
        setBoldText(interestField, "관심 분야: ", activity.getInterestField() != null ? String.join(", ", activity.getInterestField()) : "N/A");
        setBoldText(homepage, "홈페이지: ", activity.getHomepage() != null && !activity.getHomepage().isEmpty() ? activity.getHomepage().get(0) : "N/A");

        setBoldText(detail, "상세정보:\n", activity.getDetail().replaceAll("\\\\n", "\n"));

        Log.d(TAG, "Loading image from URL: " + activity.getPosterUrl());
        if (activity.getPosterUrl() != null && !activity.getPosterUrl().isEmpty()) {
            Glide.with(this)
                    .load(activity.getPosterUrl())
                    .apply(new RequestOptions().error(R.drawable.default_image).diskCacheStrategy(DiskCacheStrategy.ALL))
                    .into(image);
        } else {
            image.setImageResource(R.drawable.default_image);
        }

        setDdayStatus(ddayStatus, activity.getSubPeriod());
        incrementHitCount(activity, hitCount);
        updateLikeCount(activity, likeCount);
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
            ddayStatusView.setTextColor(Color.GRAY);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void incrementHitCount(Activity activity, TextView hitCountView) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("activities").whereEqualTo("title", activity.getTitle())
                .get().addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            DocumentReference docRef = document.getReference();
                            long hits = document.getLong("hits") != null ? document.getLong("hits") : 0;
                            hits++;
                            docRef.update("hits", hits);
                            hitCountView.setText("조회수: " + hits);
                            activity.setHits((int) hits);
                            break;
                        }
                    }
                });
    }

    private void updateLikeCount(Activity activity, TextView likeCountView) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("activities").whereEqualTo("title", activity.getTitle())
                .get().addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            long likes = document.getLong("likes") != null ? document.getLong("likes") : 0;
                            likeCountView.setText("찜: " + likes);
                            activity.setLikes((int) likes);
                            break;
                        }
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

