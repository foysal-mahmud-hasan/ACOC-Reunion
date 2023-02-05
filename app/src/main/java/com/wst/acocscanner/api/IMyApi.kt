package com.wst.acocscanner.api

import com.wst.acocscanner.retrofitClient.APIResponse
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface IMyApi {

    //connect with API


    // for login
    @FormUrlEncoded
    @POST("login.php")
    fun loginUser(@Field("userName") userName : String, @Field("password") password : String) : Call<APIResponse>


    // register for entry
    @FormUrlEncoded
    @POST("validation.php")
    fun validateQrCode(@Field("barcode") barcode : String) : Call<APIResponse>


    // check barcode register
    @FormUrlEncoded
    @POST("check_coupon.php")
    fun checkCoupon(@Field("barcode") barcode: String, @Field("eventDetId") eventDetId: Int, @Field("registrationId") registrationId : Int, @Field("entryBy") entryBy : Int) : Call<APIResponse>


    // get cadet details
    @FormUrlEncoded
    @POST("cadet_details.php")
    fun getCadetDetails(@Field("barcode") barcode: String) : Call<APIResponse>


    // load other events
    @FormUrlEncoded
    @POST("load_events.php")
    fun loadEvents(@Field("submit") submit : String) : Call<APIResponse>

    @FormUrlEncoded
    @POST("cadet_parking.php")
    fun getCadetParking(@Field("barcode") barcode: String) : Call<APIResponse>

}