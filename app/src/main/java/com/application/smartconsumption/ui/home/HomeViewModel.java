package com.application.smartconsumption.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HomeViewModel extends ViewModel {
    private MutableLiveData<Boolean> updateNeeded = new MutableLiveData<>();

    public LiveData<Boolean> getUpdateNeeded() {
        return updateNeeded;
    }

    public void setUpdateNeeded(boolean needed) {
        updateNeeded.setValue(needed);
    }
}
