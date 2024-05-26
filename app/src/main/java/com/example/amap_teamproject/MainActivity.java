package com.example.amap_teamproject;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.viewpager.widget.ViewPager;
import com.example.amap_teamproject.databinding.ActivityMainBinding;
import com.example.amap_teamproject.menu.Event;
import com.example.amap_teamproject.ui.items.ItemFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private FirebaseFirestore db; // Firestore 인스턴스 추가
    private List<Event> eventList = new ArrayList<>(); // 이벤트 리스트 추가

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        BottomNavigationView navView = findViewById(R.id.nav_view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_items, R.id.navigation_dashboard, R.id.navigation_my_page)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        // ViewPager 및 TabLayout 설정 추가
        ViewPager viewPager = findViewById(R.id.view_pager);
        setupViewPager(viewPager); // ViewPager 설정 함수 호출

        TabLayout tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager); // TabLayout과 ViewPager 연결

        // Firestore 초기화
        db = FirebaseFirestore.getInstance();

        // Firestore에서 데이터 가져오기
        fetchFirestoreData();
    }

    // Firestore에서 데이터 가져오는 함수
    private void fetchFirestoreData() {
        db.collection("activities")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Event> eventList = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Event event = document.toObject(Event.class);
                            eventList.add(event);
                        }
                        // ViewPager의 ItemFragment에 데이터를 전달
                        updateItemFragment(eventList);
                    } else {
                        // 오류 처리
                    }
                });
    }

    private void updateItemFragment(List<Event> events) {
        // ItemFragment 인스턴스를 가져와 데이터를 설정
        ViewPagerAdapter adapter = (ViewPagerAdapter) ((ViewPager) findViewById(R.id.view_pager)).getAdapter();
        if (adapter != null) {
            for (Fragment fragment : adapter.getFragments()) {
                if (fragment instanceof ItemFragment) {
                    ((ItemFragment) fragment).updateData(events);
                }
            }
        }
    }

    // ViewPager를 설정하는 함수
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(ItemFragment.newInstance(1), "Items"); // ItemFragment를 ViewPager에 추가
        viewPager.setAdapter(adapter); // ViewPager에 어댑터 설정
    }

    // ViewPager 어댑터 클래스
    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position); // 위치에 따른 Fragment 반환
        }

        @Override
        public int getCount() {
            return mFragmentList.size(); // Fragment 리스트 크기 반환
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment); // Fragment 리스트에 추가
            mFragmentTitleList.add(title); // Fragment 타이틀 리스트에 추가
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position); // 위치에 따른 Fragment 타이틀 반환
        }

        public List<Fragment> getFragments() {
            return mFragmentList;
        }
    }
}



