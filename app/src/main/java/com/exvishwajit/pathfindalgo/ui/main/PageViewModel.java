package com.exvishwajit.pathfindalgo.ui.main;

import android.util.Log;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

public class PageViewModel extends ViewModel {
    private static final String TAG = "PageViewModel";

    private MutableLiveData<Integer> mIndex = new MutableLiveData<>();
    private LiveData<String> mText = Transformations.map(mIndex, new Function<Integer, String>() {
        @Override
        public String apply(Integer input) {
            Log.d(TAG, "apply: ");
            return "Hello world from section: " + input;
        }
    });

    public void setIndex(int index) {
        Log.d(TAG, "setIndex: ");
        mIndex.setValue(index);
    }

    public LiveData<String> getText() {
        Log.d(TAG, "getText: ");
        return mText;
    }
}