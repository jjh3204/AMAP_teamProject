package com.example.amap_teamproject.ui.MyPage;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MyPageViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public MyPageViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is notifications fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}