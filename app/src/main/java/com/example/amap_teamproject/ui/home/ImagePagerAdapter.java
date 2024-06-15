package com.example.amap_teamproject.ui.home;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import java.util.List;

public class ImagePagerAdapter extends FragmentStateAdapter {

    private List<ImageItem> imageItems;

    public ImagePagerAdapter(@NonNull FragmentActivity fragmentActivity, List<ImageItem> imageItems) {
        super(fragmentActivity);
        this.imageItems = imageItems;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            // 가상의 마지막 아이템을 첫 번째에 추가하여 무한 슬라이드 효과를 만듭니다.
            ImageItem lastItem = imageItems.get(imageItems.size() - 1);
            return ImageFragment.newInstance(lastItem.getPosterUrl(), lastItem.getTitle());
        } else if (position == imageItems.size() + 1) {
            // 가상의 첫 번째 아이템을 마지막에 추가하여 무한 슬라이드 효과를 만듭니다.
            ImageItem firstItem = imageItems.get(0);
            return ImageFragment.newInstance(firstItem.getPosterUrl(), firstItem.getTitle());
        } else {
            ImageItem item = imageItems.get(position - 1);
            return ImageFragment.newInstance(item.getPosterUrl(), item.getTitle());
        }
    }

    @Override
    public int getItemCount() {
        // 실제 아이템 개수보다 2개 더 반환하여 양방향 가상의 추가 아이템을 포함합니다.
        return imageItems.size() + 2;
    }
}

