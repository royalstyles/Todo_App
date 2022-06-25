package com.example.todo_app.ui.slideshow;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SlideshowViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public SlideshowViewModel() {
        Log.d(getClass().getName(), "KJH : " + Thread.currentThread().getStackTrace()[2].getMethodName());
        mText = new MutableLiveData<>();
        mText.setValue("This is slideshow fragment");
    }

    public LiveData<String> getText() {
        Log.d(getClass().getName(), "KJH : " + Thread.currentThread().getStackTrace()[2].getMethodName());
        return mText;
    }
}