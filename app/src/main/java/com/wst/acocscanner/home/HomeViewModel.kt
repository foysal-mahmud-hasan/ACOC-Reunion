package com.wst.acocscanner.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewModel(application: Application) : AndroidViewModel(application) {



    private var _navigateToRegistrationScanner = MutableLiveData<Boolean>()

    val navigateToRegistrationScanner : LiveData<Boolean>
        get() = _navigateToRegistrationScanner

    private var _navigateToOEScanner = MutableLiveData<Boolean>()

    val navigateToOEScanner : LiveData<Boolean>
        get() = _navigateToOEScanner

    fun doneNavigateToRegistrationScanner(){
        _navigateToRegistrationScanner.value = false
    }

    fun regButtonPressed(){

        _navigateToRegistrationScanner.value = true

    }

    fun oeButtonPressed(){

        _navigateToOEScanner.value = true

    }

    fun doneNavigateToOEScanner(){
        _navigateToOEScanner.value = false
    }


}