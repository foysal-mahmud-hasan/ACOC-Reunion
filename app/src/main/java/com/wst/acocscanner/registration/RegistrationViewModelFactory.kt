package com.wst.acocscanner.registration

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras

class RegistrationViewModelFactory(private val application: Application) : ViewModelProvider.Factory {

    override fun <T: ViewModel> create(modelClass: Class<T>) : T{

        if(modelClass.isAssignableFrom(RegistrationViewModel::class.java)){
            return RegistrationViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")

    }
}