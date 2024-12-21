package com.example.donation_drive_app.ui.catalogue;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class CatalogueViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public CatalogueViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is dashboard fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}