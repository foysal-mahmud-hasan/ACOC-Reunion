package com.wst.acocscanner.commonURL

import com.wst.acocscanner.api.IMyApi
import com.wst.acocscanner.retrofitClient.RetrofitClient

object Common {

//    val BASE_URL = "http://192.168.1.27/acocReunionApi/"
    val BASE_URL = "https://white.services/acocReunionApi/"

    val api: IMyApi
        get() = RetrofitClient.getClient(BASE_URL).create(IMyApi::class.java)
}