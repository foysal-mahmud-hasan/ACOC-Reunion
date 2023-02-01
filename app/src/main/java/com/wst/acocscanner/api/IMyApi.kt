package com.wst.acocscanner.api

import com.wst.acocscanner.retrofitClient.APIResponse
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface IMyApi {

    //connect with API

    @FormUrlEncoded
    @POST("login.php")
    fun loginUser(@Field("userName") userName : String, @Field("password") password : String) : Call<APIResponse>

}