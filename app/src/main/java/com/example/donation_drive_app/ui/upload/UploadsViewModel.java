package com.example.donation_drive_app.ui.upload;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class UploadsViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public UploadsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is an uploads fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}