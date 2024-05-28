package com.example.amap_teamproject.ui.home;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import java.util.List;

public class ImagePagerAdapter extends FragmentStateAdapter {

    private List<String> imageUrls;

    public ImagePagerAdapter(@NonNull FragmentActivity fragmentActivity, List<String> imageUrls) {
        super(fragmentActivity);
        this.imageUrls = imageUrls;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return ImageFragment.newInstance(imageUrls.get(position));
    }

    @Override
    public int getItemCount() {
        return imageUrls.size();
    }
}
