package com.example.amap_teamproject.ui.items;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity; // 이 줄을 추가합니다
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.amap_teamproject.R;
import com.example.amap_teamproject.menu.Event;
import com.example.amap_teamproject.menu.EventAdapter;
import java.util.ArrayList;
import java.util.List;

public class ItemFragment extends Fragment {

    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;
    private List<Event> eventList;

    public ItemFragment() {
    }

    public static ItemFragment newInstance(int columnCount) {
        ItemFragment fragment = new ItemFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_list, container, false);

        // Load event data
        eventList = new ArrayList<>();
        loadSampleData();

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            recyclerView.setAdapter(new EventAdapter(eventList));
        }
        return view;
    }

    private void loadSampleData() {
        // 공모전 예시 데이터
        eventList.add(new Event("아동 양성평등 공모전", "동작이수사회복지관", "D-13", R.drawable.sample_image_1, "아동 양성평등 서포터즈는 ...", "공모전"));
        eventList.add(new Event("청소년 사진PG '인생내컷' 공모전", "광장종합사회복지관", "D-5", R.drawable.sample_image_2, "청소년 사진PG '인생내컷' 활동가는 ...", "공모전"));
        eventList.add(new Event("자원봉사 공모전", "초록우산 경북지역본부", "D-1", R.drawable.sample_image_3, "일일자원봉사자는 ...", "공모전"));
        eventList.add(new Event("중학생 꿈찾기 공모전", "인천장애인재활협회", "D-6", R.drawable.sample_image_4, "중학생 꿈찾기 및 학습멘토링은 ...", "공모전"));
        eventList.add(new Event("아동 양성평등 공모전2", "동작이수사회복지관", "D-13", R.drawable.sample_image_1, "아동 양성평등 서포터즈는 ...", "공모전"));
        eventList.add(new Event("청소년 사진PG '인생내컷' 공모전2", "광장종합사회복지관", "D-5", R.drawable.sample_image_2, "청소년 사진PG '인생내컷' 활동가는 ...", "공모전"));
        eventList.add(new Event("자원봉사 공모전2", "초록우산 경북지역본부", "D-1", R.drawable.sample_image_3, "일일자원봉사자는 ...", "공모전"));
        eventList.add(new Event("중학생 꿈찾기 공모전2", "인천장애인재활협회", "D-6", R.drawable.sample_image_4, "중학생 꿈찾기 및 학습멘토링은 ...", "공모전"));
        eventList.add(new Event("아동 양성평등 공모전3", "동작이수사회복지관", "D-13", R.drawable.sample_image_1, "아동 양성평등 서포터즈는 ...", "공모전"));
        eventList.add(new Event("청소년 사진PG '인생내컷' 공모전3", "광장종합사회복지관", "D-5", R.drawable.sample_image_2, "청소년 사진PG '인생내컷' 활동가는 ...", "공모전"));
        eventList.add(new Event("자원봉사 공모전3", "초록우산 경북지역본부", "D-1", R.drawable.sample_image_3, "일일자원봉사자는 ...", "공모전"));
        eventList.add(new Event("중학생 꿈찾기 공모전3", "인천장애인재활협회", "D-6", R.drawable.sample_image_4, "중학생 꿈찾기 및 학습멘토링은 ...", "공모전"));
        eventList.add(new Event("아동 양성평등 공모전4", "동작이수사회복지관", "D-13", R.drawable.sample_image_1, "아동 양성평등 서포터즈는 ...", "공모전"));
        eventList.add(new Event("청소년 사진PG '인생내컷' 공모전4", "광장종합사회복지관", "D-5", R.drawable.sample_image_2, "청소년 사진PG '인생내컷' 활동가는 ...", "공모전"));
        eventList.add(new Event("자원봉사 공모전4", "초록우산 경북지역본부", "D-1", R.drawable.sample_image_3, "일일자원봉사자는 ...", "공모전"));
        eventList.add(new Event("중학생 꿈찾기 공모전4", "인천장애인재활협회", "D-6", R.drawable.sample_image_4, "중학생 꿈찾기 및 학습멘토링은 ...", "공모전"));

        // 대외활동 예시 데이터
        eventList.add(new Event("아동 돌봄 대외활동", "사랑의복지관", "D-10", R.drawable.sample_image_1, "아동 돌봄 자원봉사는 ...", "대외활동"));
        eventList.add(new Event("청소년 리더십 캠프", "청소년리더십센터", "D-20", R.drawable.sample_image_2, "청소년 리더십 캠프는 ...", "대외활동"));
        eventList.add(new Event("해외 봉사단 모집", "글로벌봉사단", "D-30", R.drawable.sample_image_3, "해외 봉사단은 ...", "대외활동"));
        eventList.add(new Event("환경 보호 서포터즈", "환경재단", "D-15", R.drawable.sample_image_4, "환경 보호 서포터즈는 ...", "대외활동"));
        eventList.add(new Event("아동 돌봄 대외활동2", "사랑의복지관", "D-10", R.drawable.sample_image_1, "아동 돌봄 자원봉사는 ...", "대외활동"));
        eventList.add(new Event("청소년 리더십 캠프2", "청소년리더십센터", "D-20", R.drawable.sample_image_2, "청소년 리더십 캠프는 ...", "대외활동"));
        eventList.add(new Event("해외 봉사단 모집2", "글로벌봉사단", "D-30", R.drawable.sample_image_3, "해외 봉사단은 ...", "대외활동"));
        eventList.add(new Event("환경 보호 서포터즈2", "환경재단", "D-15", R.drawable.sample_image_4, "환경 보호 서포터즈는 ...", "대외활동"));
        eventList.add(new Event("아동 돌봄 대외활동3", "사랑의복지관", "D-10", R.drawable.sample_image_1, "아동 돌봄 자원봉사는 ...", "대외활동"));
        eventList.add(new Event("청소년 리더십 캠프3", "청소년리더십센터", "D-20", R.drawable.sample_image_2, "청소년 리더십 캠프는 ...", "대외활동"));
        eventList.add(new Event("해외 봉사단 모집3", "글로벌봉사단", "D-30", R.drawable.sample_image_3, "해외 봉사단은 ...", "대외활동"));
        eventList.add(new Event("환경 보호 서포터즈3", "환경재단", "D-15", R.drawable.sample_image_4, "환경 보호 서포터즈는 ...", "대외활동"));
        eventList.add(new Event("아동 돌봄 대외활동4", "사랑의복지관", "D-10", R.drawable.sample_image_1, "아동 돌봄 자원봉사는 ...", "대외활동"));
        eventList.add(new Event("청소년 리더십 캠프4", "청소년리더십센터", "D-20", R.drawable.sample_image_2, "청소년 리더십 캠프는 ...", "대외활동"));
        eventList.add(new Event("해외 봉사단 모집4", "글로벌봉사단", "D-30", R.drawable.sample_image_3, "해외 봉사단은 ...", "대외활동"));
        eventList.add(new Event("환경 보호 서포터즈4", "환경재단", "D-15", R.drawable.sample_image_4, "환경 보호 서포터즈는 ...", "대외활동"));
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("목록");
        }
    }
}


