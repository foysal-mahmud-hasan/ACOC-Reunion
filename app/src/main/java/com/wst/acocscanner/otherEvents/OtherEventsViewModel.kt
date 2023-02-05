package com.wst.acocscanner.otherEvents

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class OtherEventsViewModel (application: Application) : AndroidViewModel(application){

    private var _navigateToHomeFragment = MutableLiveData<Boolean>()

    val navigateToHomeFragment : LiveData<Boolean>
        get() = _navigateToHomeFragment

    fun backButtonPressed(){

        _navigateToHomeFragment.value = true

    }

    fun doneNavigateToHome(){

        _navigateToHomeFragment.value = false

    }

}