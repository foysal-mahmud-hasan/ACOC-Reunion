package com.wst.acocscanner.cadetDetails

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class CadetDetailsViewModel (application: Application) : AndroidViewModel(application){

    private var _navigateToRegistrationScanner = MutableLiveData<Boolean>()

    val navigateToRegistrationScanner : LiveData<Boolean>
        get() = _navigateToRegistrationScanner
    fun backButtonPressed(){
        _navigateToRegistrationScanner.value = true
    }
    fun doneNavigateToRegistrationScanner(){
        _navigateToRegistrationScanner.value = false
    }

}