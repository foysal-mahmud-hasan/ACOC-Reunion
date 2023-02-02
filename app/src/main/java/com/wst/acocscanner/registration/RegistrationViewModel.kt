package com.wst.acocscanner.registration

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class RegistrationViewModel(application: Application) : AndroidViewModel(application) {

    private var _navigateToHomeFragment = MutableLiveData<Boolean>()

    val navigateToHomeFragment : LiveData<Boolean>
        get() = _navigateToHomeFragment

    private var _barcode = MutableLiveData<String>()

    val barcode : LiveData<String>
        get() = _barcode

    fun backButtonPressed(){

        _navigateToHomeFragment.value = true

    }

    fun doneNavigateToHome(){

        _navigateToHomeFragment.value = false

    }

    fun barcodeScanned(barcode : String){
        _barcode.value = barcode
    }

    fun readyNewScan(){
        _barcode.value = ""
    }

}