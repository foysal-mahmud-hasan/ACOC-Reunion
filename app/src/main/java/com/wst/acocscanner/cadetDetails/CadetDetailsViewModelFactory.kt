package com.wst.acocscanner.cadetDetails

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class CadetDetailsViewModelFactory(private val application: Application) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>) : T{
        if(modelClass.isAssignableFrom(CadetDetailsViewModel::class.java)){
            return CadetDetailsViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }

}