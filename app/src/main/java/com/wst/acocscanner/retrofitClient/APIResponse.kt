package com.wst.acocscanner.retrofitClient

import com.wst.acocscanner.model.User

class APIResponse {

    var error : Boolean = false
    var id : Long = 0
    var error_msg : String? = null
    var user : User? = null

}