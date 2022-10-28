package com.jhpj.todo_app.ui.company;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class CompanyViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public CompanyViewModel() {
        Log.d(getClass().getName(), "KJH : " + Thread.currentThread().getStackTrace()[2].getMethodName());
        mText = new MutableLiveData<>();
        mText.setValue("This is Company fragment");
    }

    public LiveData<String> getText() {
        Log.d(getClass().getName(), "KJH : " + Thread.currentThread().getStackTrace()[2].getMethodName());
        return mText;
    }
}