package com.jhpj.todo_app.ui.movie;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MovieViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public MovieViewModel() {
        Log.d(getClass().getName(), "KJH : " + Thread.currentThread().getStackTrace()[2].getMethodName());
        mText = new MutableLiveData<>();
        mText.setValue("This is Movie fragment");
    }

    public LiveData<String> getText() {
        Log.d(getClass().getName(), "KJH : " + Thread.currentThread().getStackTrace()[2].getMethodName());
        return mText;
    }
}
