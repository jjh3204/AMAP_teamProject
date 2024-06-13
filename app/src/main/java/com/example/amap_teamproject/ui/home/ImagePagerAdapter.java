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
        ImageItem item = imageItems.get(position);
        return ImageFragment.newInstance(item.getPosterUrl(), item.getTitle());
    }

    @Override
    public int getItemCount() {
        return imageItems.size();
    }
}
